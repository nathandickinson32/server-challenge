package server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private final int port;
    private final String root;
    private ServerSocket serverSocket;
    private HashMap<RouteKey, RequestHandler> handlers = new HashMap<>();

    public HttpServer(int port, String root) {
        this.port = port;
        this.root = root;
        addHandlers();
    }

    public void start() throws IOException {
        serverSocket = new NetworkServerSocket(port);
        while (!serverSocket.isClosed()) {
            Socket client = serverSocket.accept();
            handleClient(client);
        }
    }

    public void addHandlers() {
        handlers.put(new RouteKey("GET", "/hello"), new HelloHandler(root, "hello.html"));
        handlers.put(new RouteKey("GET", "/listing"), new ListingHandler(root));
        handlers.put(new RouteKey("GET", "/listing/img"), new ImageListingHandler(new File(root, "img")));
    }

    public void handleClient(Socket client) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))) {

            Request request = Request.requestParser(client.getInputStream());
            Response response;
            String path = request.getPath();
            RequestHandler handler = null;

            if (path.equals("/") || path.equals("/index")) {
                File indexFile = new File(root, "index.html");
                if (indexFile.exists() && indexFile.isFile()) {
                    handler = new HelloHandler(root, "index.html");
                } else {
                    handler = new ListingHandler(root);
                }
            }

            if (handler == null) {
                RouteKey key = new RouteKey(request.getMethod(), path);
                handler = handlers.get(key);
            }

            if (handler == null) {
                File file = new File(root, path.startsWith("/") ? path.substring(1) : path);
                if (file.exists() && file.isFile()) {
                    handler = new HelloHandler(root, file.getName()); // serve any static file
                }
            }

            if (handler != null) {
                response = handler.handle(request);
            } else {
                response = new Response();
                response.setStatusCode(404);
                response.setStatusMessage("Not Found");
                String body = "<h1>404 Not Found</h1>";
                response.setBody(body);
                response.addHeader("Content-Type", "text/html");
                response.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.ISO_8859_1).length));
            }
            sendResponse(response, out);
        }
    }

    private void sendResponse(Response response, BufferedWriter out) throws IOException {
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append("HTTP/1.1 ").append(response.getStatusCode()).append(" ").append(response.getStatusMessage()).append("\r\n");

        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            outputBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        outputBuilder.append("\r\n").append(response.getBody());
        out.write(outputBuilder.toString());
        out.flush();
    }
}