package handlers;

import server.Request;
import server.Response;
import server.GameState;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.URLDecoder;

public class GuessHandler implements RequestHandler {

    private static final Map<String, GameState> gameSessions = new HashMap<>();
    private static final Map<String, String> lastMessages = new HashMap<>();

    public static void setTestGame(String sessionId, int target, int attempts) {
        gameSessions.put(sessionId, new GameState(target, attempts));
    }

    @Override
    public Response handle(Request request) {
        Response response = new Response();
        String sessionId = request.getHeader("X-Client-Id");
        sessionId = handleSession(request, sessionId, response);
        GameState game = gameSessions.get(sessionId);
        game = maybeNewGame(game, sessionId);
        maybeResumeGame(sessionId, game);

        handleGetRequest(request, sessionId, response);
        handlePostRequest(request, sessionId, game, response);

        return response;
    }

    private String handleSession(Request request, String sessionId, Response response) {
        if (sessionId == null || sessionId.isEmpty()) {
            String cookieHeader = request.getHeader("Cookie");
            if (cookieHeader != null) {
                for (String cookie : cookieHeader.split(";")) {
                    String[] kv = cookie.trim().split("=", 2);
                    if (kv.length == 2 && kv[0].equals("sessionId")) {
                        sessionId = kv[1];
                    }
                }
            }
        }

        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            response.addHeader("Set-Cookie", "sessionId=" + sessionId);
        }
        return sessionId;
    }

    private void handlePostRequest(Request request, String sessionId, GameState game, Response response) {
        if ("POST".equals(request.getMethod())) {
            String contentType = request.getHeader("Content-Type");
            if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded")) {
                String bodyStr = new String(request.getRawBody(), StandardCharsets.ISO_8859_1);
                Map<String, String> params = parseForm(bodyStr);
                String guessStr = params.get("guess");

                lastMessages.put(sessionId, processGuess(sessionId, game, guessStr));

                response.setStatusCode(303);
                response.setStatusMessage("See Other");
                response.addHeader("Location", "/guess");
            }
        }
    }

    private void handleGetRequest(Request request, String sessionId, Response response) {
        if ("GET".equals(request.getMethod())) {
            String message = lastMessages.get(sessionId);
            response.setStatusCode(200);
            response.setStatusMessage("OK");
            response.setBody(buildPage(message));
            response.addHeader("Content-Type", "text/html");
            response.addHeader("Content-Length",
                    String.valueOf(response.getBody().getBytes(StandardCharsets.ISO_8859_1).length));
        }
    }

    private String processGuess(String sessionId, GameState game, String guessStr) {

        int guess;
        guess = Integer.parseInt(guessStr);
        game.decrementAttempts();

        if (guess == game.getTarget()) {
            int answer = game.getTarget();
            gameSessions.put(sessionId, new GameState());
            return "Correct! The number was " + answer + ". Starting a new game...";
        } else if (game.getAttemptsLeft() <= 0) {
            int answer = game.getTarget();
            gameSessions.put(sessionId, new GameState());
            return "Out of tries! The number was " + answer + ". Starting a new game...";
        } else if (guess < game.getTarget()) {
            return "Too low! Attempts left: " + game.getAttemptsLeft();
        } else {
            return "Too high! Attempts left: " + game.getAttemptsLeft();
        }
    }

    private Map<String, String> parseForm(String body) {
        Map<String, String> params = new HashMap<>();
        if (body == null || body.isEmpty()) return params;
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(
                        URLDecoder.decode(kv[0], StandardCharsets.ISO_8859_1),
                        URLDecoder.decode(kv[1], StandardCharsets.ISO_8859_1)
                );
            }
        }
        return params;
    }

    private void maybeResumeGame(String sessionId, GameState game) {
        if (!lastMessages.containsKey(sessionId)) {
            lastMessages.put(sessionId, getStartingMessage(game));
        }
    }

    private GameState maybeNewGame(GameState game, String sessionId) {
        if (game == null) {
            game = new GameState();
            gameSessions.put(sessionId, game);
        }
        return game;
    }

    private String getStartingMessage(GameState game) {
        return "I'm thinking of a number between 1 and 100. You have " + game.getAttemptsLeft() + " tries!";
    }

    private String buildPage(String message) {
        return """
                <html>
                  <head><title>Guess the Number</title></head>
                  <body>
                    <h1>Guess the Number</h1>
                    <p>%s</p>
                    <form method="post" action="/guess">
                      <label>Enter guess (1 to 100):</label>
                      <input type="number" name="guess" min="1" max="100" required>
                      <button type="submit">Submit</button>
                    </form>
                  </body>
                </html>
                """.formatted(message);
    }
}