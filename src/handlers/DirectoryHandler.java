package handlers;

import server.Request;
import server.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryHandler implements RequestHandler {

    private final String root;

    public DirectoryHandler(String root) {
        this.root = root;
    }

    @Override
    public Response handle(Request request) throws IOException {
        String requestPath = request.getPath().split("\\?")[0];

        if (requestPath.endsWith("/") && requestPath.length() > 1) {
            requestPath = requestPath.substring(0, requestPath.length() - 1);
        }

        File file;

        if ("/listing/img".equals(requestPath)) {
            file = new File(root, "img");
        } else if ("/listing".equals(requestPath)) {
            file = new File(root);
        } else {
            file = new File(root, requestPath);

            if (file.isDirectory()) {
                File indexFile = new File(file, "index.html");
                if (indexFile.exists() && indexFile.isFile()) {
                    return new FileHandler(root, requestPath + "/index.html").handle(request);
                }
            }
        }

        File[] files;
        if (file.isDirectory()) {
            files = file.listFiles();
        } else {
            files = null;
        }

        if (files != null) {
            return buildListing(files, requestPath);
        }

        if (file.isFile()) {
            return new FileHandler(root, requestPath).handle(request);
        }

        return new NotFoundHandler().handle(request);
    }

    private Response buildListing(File[] files, String requestPath) {
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html><html><body><ul>");

        Path rootPath = Paths.get(root).toAbsolutePath().normalize();
        for (File file : files) {
            Path filePath = file.toPath().toAbsolutePath().normalize();
            Path relative = rootPath.relativize(filePath);
            String urlPath = "/" + relative.toString().replace(File.separatorChar, '/');

            if ("/listing".equals(requestPath) && file.isDirectory() && file.getName().equals("img")) {
                urlPath = "/listing/img";
            }

            body.append("<li><a href=\"").append(urlPath).append("\">")
                    .append(file.getName())
                    .append("</a></li>");
        }

        body.append("</ul></body></html>");
        return SuccessHandler.getSuccessResponse(body.toString());
    }
}