package server;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class HelloHandler implements RequestHandler {

    private final String root;
    private final String filename;

    public HelloHandler(String root, String filename) {
        this.root = root;
        this.filename = filename;
    }

    @Override
    public Response handle(Request request) throws IOException {
        Response response = new Response();
        File file = new File(root, filename);
        String body = Files.readString(file.toPath(), StandardCharsets.ISO_8859_1);
        response.setBody(body);
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.ISO_8859_1).length));
        return response;
    }
}