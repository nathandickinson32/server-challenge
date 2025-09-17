package server;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class ListingHandler implements RequestHandler {

    private final File file;

    public ListingHandler(String root) {
        this.file = new File(root);
    }

    @Override
    public Response handle(Request request) {
        Response response = new Response();

        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html><html><body><ul>");
        File[] files = file.listFiles();
        if (files != null) {
            for (File currentFile : files) {
                String name = currentFile.getName();
                if (currentFile.isDirectory()) {
                    body.append("<li><a href=\"/listing/").append(name).append("\">").append(name).append("</a></li>");
                } else {
                    body.append("<li><a href=\"/").append(name).append("\">").append(name).append("</a></li>");
                }
            }
        }
        body.append("</ul></body></html>");

        response.setBody(body.toString());
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Content-Length", String.valueOf(body.toString().getBytes(StandardCharsets.ISO_8859_1).length));

        return response;
    }
}