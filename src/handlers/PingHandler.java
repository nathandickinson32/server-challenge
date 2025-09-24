package handlers;

import server.Request;
import server.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static handlers.SuccessHandler.getSuccessResponse;

public class PingHandler implements RequestHandler {
    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        int delaySeconds = 0;

        if (path.startsWith("/ping/")) {
            try {
                delaySeconds = Integer.parseInt(path.substring("/ping/".length()));
            } catch (NumberFormatException ignored) {
            }
        }

        LocalDateTime start = LocalDateTime.now();

        if (delaySeconds > 0) {
            try {
                Thread.sleep(delaySeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        LocalDateTime end = LocalDateTime.now();

        String body = "<h2>Ping</h2>\n" +
                "<li>start time: " + FORMATTER.format(start) + "</li>\n" +
                "<li>end time: " + FORMATTER.format(end) + "</li>\n";

        return getSuccessResponse(body);
    }
}