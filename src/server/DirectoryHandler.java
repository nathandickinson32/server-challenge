package server;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static server.SuccessHandler.getSuccessResponse;

public class DirectoryHandler implements RequestHandler {

    private final File directory;
    private final Path rootPath;

    public DirectoryHandler(String root, String relativePath) {
        this.directory = new File(root, relativePath);
        this.rootPath = Paths.get(root).toAbsolutePath().normalize();
    }

    @Override
    public Response handle(Request request) {
        Response response = new Response();

        if (!directory.exists() || !directory.isDirectory()) {
            response.setStatusCode(404);
            response.setStatusMessage("Not Found");
            response.setBody("<h1>404 Not Found</h1>");
            response.addHeader("Content-Type", "text/html");
            return response;
        }

        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html><html><body><ul>");

        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                Path filePath = f.toPath().toAbsolutePath().normalize();
                Path relative = rootPath.relativize(filePath);
                String urlPath = "/" + relative.toString().replace(File.separatorChar, '/');

                body.append("<li><a href=\"").append(urlPath).append("\">")
                        .append(f.getName())
                        .append("</a></li>");
            }
        }

        body.append("</ul></body></html>");

        return getSuccessResponse(body.toString());
    }
}