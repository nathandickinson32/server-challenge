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
        handlers.put(new RouteKey("GET", "/form"), new FormHandler(root));
        handlers.put(new RouteKey("POST", "/form"), new FormHandler(root));
    }

    public void handleClient(Socket client) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.ISO_8859_1))) {

            Request request = Request.requestParser(client.getInputStream());
            Response response;
            String path = request.getPath();
            RequestHandler handler = null;

            if (path.equals("/") || path.equals("/index")) {
                File indexFile = new File(root, "index.html");
                if (indexFile.exists() && indexFile.isFile()) {
                    handler = new FileHandler(root, "index.html");
                } else {
                    handler = new ListingHandler(root);
                }
            }

            if (handler == null) {
                RouteKey key = new RouteKey(request.getMethod(), path.split("\\?")[0]);
                handler = handlers.get(key);
            }

            if (handler == null) {
                File file = new File(root, path);
                if (file.exists() && file.isFile()) {
                    handler = new FileHandler(root, path);
                }
            }

            if (handler == null) {
                handler = new NotFoundHandler();
            }

            response = handler.handle(request);
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