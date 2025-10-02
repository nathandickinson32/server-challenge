package handlers;

import server.Request;
import server.Response;
import server.GameState;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class GuessHandler implements RequestHandler {

    private final Map<String, GameState> gameSessions = new HashMap<>();
    private final Map<String, String> lastMessages = new HashMap<>();

    public void setTestGame(String sessionId, int target, int attempts) {
        gameSessions.put(sessionId, new GameState(target, attempts));
    }

    @Override
    public Response handle(Request request) {
        Response response = new Response();
        String sessionId = handleSession(request, response);

        GameState game = gameSessions.get(sessionId);
        game = maybeNewGame(game, sessionId);
        maybeResumeGame(sessionId, game);

        if ("POST".equals(request.getMethod())) {
            handlePostRequest(request, sessionId, game, response);
        } else if ("GET".equals(request.getMethod())) {
            handleGetRequest(sessionId, response);
        }

        return response;
    }

    private String handleSession(Request request, Response response) {
        String sessionId = getSessionId(request);

        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            response.addHeader("Set-Cookie", "sessionId=" + sessionId);
        }

        return sessionId;
    }

    private static String getSessionId(Request request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader == null) return null;

        for (String cookie : cookieHeader.split(";")) {
            String[] kv = cookie.trim().split("=", 2);
            if (kv.length == 2 && kv[0].equals("sessionId")) {
                return kv[1];
            }
        }
        return null;
    }

    private void handlePostRequest(Request request, String sessionId, GameState game, Response response) {
        String guessStr = request.getParams().get("guess");

        if (guessStr != null) {
            lastMessages.put(sessionId, processGuess(sessionId, game, guessStr));
        }

        response.setStatusCode(303);
        response.setStatusMessage("See Other");
        response.addHeader("Location", "/guess");
    }

    private void handleGetRequest(String sessionId, Response response) {
        String message = lastMessages.get(sessionId);
        response.setStatusCode(200);
        response.setStatusMessage("OK");
        response.setBody(buildPage(message));

        int contentLength = response.getBody().getBytes(StandardCharsets.ISO_8859_1).length;
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Content-Length", contentLength);
    }

    private String processGuess(String sessionId, GameState game, String guessStr) {
        int guess = Integer.parseInt(guessStr);
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

    private void maybeResumeGame(String sessionId, GameState game) {
        lastMessages.putIfAbsent(sessionId, getStartingMessage(game));
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