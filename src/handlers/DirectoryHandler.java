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
        String requestPath = getRequestPath(request);
        var filePath = requestPath.replaceFirst("^/listing", "");
        var file = new File(root, filePath);

        if (file.isDirectory()) {
            return getIndexOrListing(request, file, requestPath);
        } else if (file.exists()) {
            return new FileHandler(file).handle(request);
        }
        return new NotFoundHandler().handle(request);
    }

    private Response getIndexOrListing(Request request, File file, String requestPath) throws IOException {
        File indexFile = new File(file, "index.html");
        if (isNotListingFile(indexFile, requestPath)) {
            return new FileHandler(indexFile).handle(request);
        }
        return buildListing(file.listFiles(), requestPath);
    }

    private static boolean isNotListingFile(File file, String requestPath) {
        return file.exists()
                && file.isFile()
                && !"/listing".equals(requestPath);
    }

    private static String getRequestPath(Request request) {
        String requestPath = request.getPath().split("\\?")[0];
        if ("/".equals(requestPath)) {
            return "/";
        }
        return requestPath.replaceFirst("/$", "");
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