package server;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        response.setStatusCode(200);
        response.setStatusMessage("OK");
        response.setBody(body.toString());
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Content-Length", String.valueOf(body.toString().getBytes(StandardCharsets.ISO_8859_1).length));

        return response;
    }
}