package handlers;

import dto.Request;
import dto.Response;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static handlers.SuccessHandler.getSuccessResponse;

public class HelloHandler implements RequestHandler {

    private final String root;
    private final String filename;

    public HelloHandler(String root, String filename) {
        this.root = root;
        this.filename = filename;
    }

    @Override
    public Response handle(Request request) throws IOException {

        File file = new File(root, filename);
        String body = Files.readString(file.toPath(), StandardCharsets.ISO_8859_1);
        return getSuccessResponse(body);
    }
}