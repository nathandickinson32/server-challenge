package handlers;

import server.Request;
import server.Response;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileHandler implements RequestHandler {

    private final File file;

    public FileHandler(String root, String relativePath) {
        this.file = new File(root, relativePath);
    }

    @Override
    public Response handle(Request request) throws IOException {
        Response response = new Response();

        if (!file.exists() || !file.isFile()) {
            response.setStatusCode(404);
            response.setStatusMessage("Not Found");
            response.setBody("<h1>404 Not Found</h1>");
            response.addHeader("Content-Type", "text/html");
            return response;
        }

        byte[] fileBytes = Files.readAllBytes(file.toPath());
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) contentType = "text/plain";

        response.setBody(new String(fileBytes, StandardCharsets.ISO_8859_1));
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", String.valueOf(fileBytes.length));
        response.setStatusCode(200);
        response.setStatusMessage("OK");
        return response;
    }
}