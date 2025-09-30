package handlers;

import server.Request;
import server.Response;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static handlers.SuccessHandler.getSuccessResponse;

public class PingHandler implements RequestHandler {

    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        int delaySeconds = 0;

        if (path.startsWith("/ping/")) {
            try {
                delaySeconds = Integer.parseInt(path.substring("/ping/".length()));
            } catch (NumberFormatException ignored) {}
        }

        Instant start = Instant.now();

        if (delaySeconds > 0) {
            try {
                Thread.sleep(delaySeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Instant end = Instant.now();
        String body = "<h2>Ping</h2>\n" +
                "<li>start time: " + FORMATTER.format(start) + "</li>\n" +
                "<li>end time: " + FORMATTER.format(end) + "</li>\n";

        return getSuccessResponse(body);
    }
}