package server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private final int port;
    private final String root;
    private ServerSocket serverSocket;
    private String serverName = "HttpServer";
    private static Map<String, String> mimeTypes = new HashMap<>();

    public HttpServer(int port, String root) {
        this.port = port;
        this.root = root;
        addMimeTypes();
    }

    public void addMimeTypes(){
        mimeTypes.put("html", "text/html");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("pdf", "application/pdf");
    }

    public void start() throws IOException {
        serverSocket = new NetworkServerSocket(port);
        while (!serverSocket.isClosed()) {
            Socket client = serverSocket.accept();
            handleClient(client);
        }
    }

    public void handleClient(Socket client) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        Request request = Request.requestParser(client.getInputStream());
        String path = request.getPath();

        if (path.equals("/") || path.equals("/index")) {
            path = "/index.html";
            File file = new File(root, path);
            if (!file.exists()) {
                path = "/listing";
            }
        }

        if (path.equals("/hello")) {
            path = "/hello.html";
        }

        if (path.equals("/listing")) {
            Response response = new Response(serverName);

            File file = new File(root);
            StringBuilder body = new StringBuilder();
            body.append("<!DOCTYPE html><html><body>").append("<ul>");

            File[] files = file.listFiles();

            for (File currentFile : files) {
                String name = currentFile.getName();
                if (currentFile.getPath().contains("img")) {
                    body.append("<li><a href=\"/listing/").append(name)
                            .append("\">").append(name).append("</a></li>");
                } else {
                    body.append("<li><a href=\"/").append(name).append("\">").append(name).append("</a></li>");
                }
            }

            body.append("</ul>").append("</body></html>");
            response.setBody(body.toString());
            response.addHeader("Content-Type", "text/html");
            response.addHeader("Content-Length", String.valueOf(body.toString().getBytes(StandardCharsets.ISO_8859_1).length));
            sendResponse(response, out);
            return;
        }

        if (path.equals("/listing/img")) {
            Response response = new Response(serverName);

            File file = new File(root, "img");
            StringBuilder body = new StringBuilder();
            body.append("<!DOCTYPE html><html><body><ul>");

            File[] files = file.listFiles();

            for (File currentFile : files) {
                String name = currentFile.getName();
                body.append("<li><a href=\"/img/").append(name)
                        .append("\">").append(name).append("</a></li>");
            }

            body.append("</ul>").append("</body></html>");
            response.setBody(body.toString());
            response.addHeader("Content-Type", "text/html");
            response.addHeader("Content-Length", String.valueOf(body.toString().getBytes(StandardCharsets.ISO_8859_1).length));
            sendResponse(response, out);
            return;
        }

        Response response = new Response(serverName);
        File file = new File(root, path);

        if (file.exists() && file.isFile()) {
            String body = java.nio.file.Files.readString(file.toPath());
            response.setBody(body);
            response.addHeader("Content-Type", "text/html");
            response.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.ISO_8859_1).length));
        } else {
            String body = "<h1>404 Not Found</h1>";
            response.setStatusCode(404);
            response.setStatusMessage("Not Found");
            response.setBody(body);
            response.addHeader("Content-Type", "text/html");
            response.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.ISO_8859_1).length));
        }
        sendResponse(response, out);
    }

    private void sendResponse(Response response, BufferedWriter out) throws IOException {
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append("HTTP/1.1 ").append(response.getStatusCode())
                .append(" ").append(response.getStatusMessage()).append("\r\n");

        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            outputBuilder.append(entry.getKey())
                    .append(": ").append(entry.getValue()).append("\r\n");
        }
        outputBuilder.append("\r\n").append(response.getBody());
        out.write(outputBuilder.toString());
        out.flush();
    }
}