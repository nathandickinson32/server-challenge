package server;

import java.io.*;
import java.nio.file.Files;

public class FileHandler implements RequestHandler {

    private final File file;

    public FileHandler(String root, String relativePath) {
        this.file = new File(root, relativePath);
    }

    @Override
    public Response handle(Request request) throws IOException {
        Response response = new Response();
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        String contentType = Files.probeContentType(file.toPath());

        if (contentType == null) contentType = "application/octet-stream";

        response.setBody(new String(fileBytes, "ISO_8859_1"));
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", String.valueOf(fileBytes.length));
        return response;
    }
}