package server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NotFoundHandler implements RequestHandler {

    @Override
    public Response handle(Request request) throws IOException {
        Response response = new Response();
        response.setStatusCode(404);
        response.setStatusMessage("Not Found");
        String body = "<h1>404 Not Found</h1>";
        response.setBody(body);
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.ISO_8859_1).length));
        return response;
    }
}