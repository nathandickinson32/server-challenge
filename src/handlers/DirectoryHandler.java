package handlers;

import dto.Request;
import dto.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryHandler implements RequestHandler {

    private final String root;
    private final String relativePath;

    public DirectoryHandler(String root, String relativePath) {
        this.root = root;
        this.relativePath = relativePath;
    }

    @Override
    public Response handle(Request request) throws IOException {
        File file;

        if ("/listing/img".equals(relativePath)) {
            file = new File(root, "img");
        }
        else if ("/listing".equals(relativePath)) {
            file = new File(root);
        }
        else {
            file = new File(root, relativePath);
            File indexFile = new File(file, "index.html");
            if (indexFile.exists() && indexFile.isFile()) {
                String indexPath;
                if (relativePath.endsWith("/")) {
                    indexPath = relativePath + "index.html";
                } else {
                    indexPath = relativePath + "/index.html";
                }
                return new FileHandler(root, indexPath).handle(request);
            }
        }

        File[] files = file.listFiles();
        return buildListing(files);
    }

    private Response buildListing(File[] files) {
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html><html><body><ul>");

        if (files != null) {
            Path rootPath = Paths.get(root).toAbsolutePath().normalize();
            for (File file : files) {
                Path filePath = file.toPath().toAbsolutePath().normalize();
                Path relative = rootPath.relativize(filePath);
                String urlPath = "/" + relative.toString().replace(File.separatorChar, '/');

                if ("/listing".equals(relativePath) && file.isDirectory() && file.getName().equals("img")) {
                    urlPath = "/listing/img";
                }

                body.append("<li><a href=\"").append(urlPath).append("\">")
                        .append(file.getName())
                        .append("</a></li>");
            }
        }

        body.append("</ul></body></html>");
        return SuccessHandler.getSuccessResponse(body.toString());
    }
}