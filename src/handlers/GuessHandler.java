package handlers;

import server.GameState;
import server.Request;
import server.Response;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.URLDecoder;

import static handlers.SuccessHandler.getSuccessResponse;

public class GuessHandler implements RequestHandler {

    private static GameState game = new GameState();

    public static void setTestGame(int target, int attemptsLeft) {
        game = new GameState(target, attemptsLeft);
    }

    @Override
    public Response handle(Request request) {
        String message = "";

        if ("GET".equals(request.getMethod())) {
            message = getStartMessage();
        } else if ("POST".equals(request.getMethod())) {
            message = getPostMessage(request);
        }

        return getSuccessResponse(buildPage(message));
    }

    private String getPostMessage(Request request) {
        String message;
        String bodyStr = new String(request.getRawBody(), StandardCharsets.ISO_8859_1);
        Map<String, String> params = parseForm(bodyStr);
        message = processGuess(params.get("guess"));
        return message;
    }

    private Map<String, String> parseForm(String body) {
        Map<String, String> params = new HashMap<>();
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

    private String processGuess(String guessStr) {
        int guess = Integer.parseInt(guessStr);
        int target = game.getTarget();
        game.decrementAttempts();

        if (guess == target) {
            return "Correct! The number was " + resetGame(target);
        } else if (game.getAttemptsLeft() <= 0) {
            return "Out of tries! The number was " + resetGame(target);
        } else if (guess < game.getTarget()) {
            return "Too low! Attempts left: " + game.getAttemptsLeft();
        } else {
            return "Too high! Attempts left: " + game.getAttemptsLeft();
        }
    }

    private static String resetGame(int target) {
        game = new GameState();
        return target + ". A new game has started.";
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

    private static String getStartMessage() {
        return "I'm thinking of a number between 1 and 100. You have " + game.getAttemptsLeft() + " tries!";
    }
}