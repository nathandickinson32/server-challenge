package handlers;

import server.Request;
import server.Response;

import java.io.File;

import static handlers.SuccessHandler.getSuccessResponse;

public class ImageListingHandler implements RequestHandler {

    private final File file;

    public ImageListingHandler(File file) {
        this.file = file;
    }

    @Override
    public Response handle(Request request) {
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html><html><body><ul>");
        File[] files = file.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String name = file.getName();
                    body.append("<li><a href=\"/img/").append(name).append("\">").append(name).append("</a></li>");
                }
            }
        }
        body.append("</ul></body></html>");

        return getSuccessResponse(body.toString());
    }
}