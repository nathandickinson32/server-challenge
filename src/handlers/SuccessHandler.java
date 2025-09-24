package handlers;

import server.Response;

import java.nio.charset.StandardCharsets;

public class SuccessHandler {

    public static Response getSuccessResponse(String message) {
        Response response = new Response();
        response.setStatusCode(200);
        response.setStatusMessage("OK");
        response.setBody(message);
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Content-Length",
                String.valueOf(response.getBody().getBytes(StandardCharsets.ISO_8859_1).length));

        return response;
    }
}