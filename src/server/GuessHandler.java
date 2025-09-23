package server;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.URLDecoder;

public class GuessHandler implements RequestHandler {

    private static GameState game = new GameState();

    public static void setTestGame(int target, int attemptsLeft) {
        game = new GameState(target, attemptsLeft);
    }

    static class GameState {
        int target;
        int attemptsLeft;

        GameState() {
            this.target = new Random().nextInt(100) + 1;
            this.attemptsLeft = 7;
        }

        GameState(int target, int attemptsLeft) {
            this.target = target;
            this.attemptsLeft = attemptsLeft;
        }
    }

    @Override
    public Response handle(Request request) {
        Response response = new Response();
        String message;

        if ("GET".equals(request.getMethod())) {
            message = "I'm thinking of a number between 1 and 100. You have " + game.attemptsLeft + " tries!";
        } else if ("POST".equals(request.getMethod())) {
            String bodyStr = new String(request.getRawBody(), StandardCharsets.ISO_8859_1);
            Map<String, String> params = parseForm(bodyStr);
            message = processGuess(params.get("guess"));
            System.out.println(message);

        } else {
            response.setStatusCode(405);
            response.setStatusMessage("Method Not Allowed");
            response.setBody("Only GET and POST supported.");
            response.addHeader("Content-Type", "text/plain");
            response.addHeader("Content-Length",
                    String.valueOf(response.getBody().getBytes(StandardCharsets.ISO_8859_1).length));
            return response;
        }

        response.setStatusCode(200);
        response.setStatusMessage("OK");
        response.setBody(buildPage(message));
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Content-Length",
                String.valueOf(response.getBody().getBytes(StandardCharsets.ISO_8859_1).length));

        return response;
    }

    private String processGuess(String guessStr) {
        if (guessStr == null) {
            return "I'm thinking of a number between 1 and 100. You have " + game.attemptsLeft + " tries!";
        }

        int guess;
        guess = Integer.parseInt(guessStr);
        game.attemptsLeft--;

        if (guess == game.target) {
            int answer = game.target;
            game = new GameState();
            return "Correct! The number was " + answer + ". A new game has started.";
        } else if (game.attemptsLeft <= 0) {
            int answer = game.target;
            game = new GameState();
            return "Out of tries! The number was " + answer + ". A new game has started.";
        } else if (guess < game.target) {
            return "Too low! Attempts left: " + game.attemptsLeft;
        } else {
            return "Too high! Attempts left: " + game.attemptsLeft;
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