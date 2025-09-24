package server;

import handlers.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private final int port;
    private final String root;
    private ServerSocket serverSocket;
    private HashMap<RoutePair, RequestHandler> handlers = new HashMap<>();

    public HttpServer(int port, String root) {
        this.port = port;
        this.root = root;
        addHandlers();
    }

    public void start() throws IOException {
        serverSocket = new NetworkServerSocket(port);
        while (!serverSocket.isClosed()) {
            Socket client = serverSocket.accept();
            new Thread(() -> {
                try {
                    handleClient(client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }).start();
        }
    }

    public void addHandlers() {
        handlers.put(new RoutePair("GET", "/"), new FileHandler(root, "index.html"));
        handlers.put(new RoutePair("GET", "/index"), new FileHandler(root, "index.html"));
        handlers.put(new RoutePair("GET", "/hello"), new HelloHandler(root, "hello.html"));
        handlers.put(new RoutePair("GET", "/listing"), new ListingHandler(root));
        handlers.put(new RoutePair("GET", "/listing/img"), new ImageListingHandler(new File(root, "img")));
        handlers.put(new RoutePair("GET", "/form"), new FormHandler(root));
        handlers.put(new RoutePair("POST", "/form"), new FormHandler(root));
        handlers.put(new RoutePair("GET", "/ping"), new PingHandler());
        handlers.put(new RoutePair("GET", "/guess"), new GuessHandler());
        handlers.put(new RoutePair("POST", "/guess"), new GuessHandler());
    }

    public void handleClient(Socket client) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.ISO_8859_1))) {
            Request request = Request.requestParser(client.getInputStream());
            RequestHandler handler = resolveHandler(request.getMethod(), request.getPath());
            Response response = handler.handle(request);
            sendResponse(response, out);
        }
    }

    private RequestHandler resolveHandler(String method, String path) {

        String cleanPath = path.split("\\?")[0];
        if (cleanPath.endsWith("/") && cleanPath.length() > 1) {
            cleanPath = cleanPath.substring(0, cleanPath.length() - 1);
        }
        String normalizedMethod = method.toUpperCase();

        RequestHandler handler = handlers.get(new RoutePair(normalizedMethod, cleanPath));
        if (handler != null) return handler;

        if (handlers.containsKey(new RoutePair(normalizedMethod, cleanPath + "/"))) {
            return handlers.get(new RoutePair(normalizedMethod, cleanPath + "/"));
        }

        if (cleanPath.startsWith("/ping")) {
            return handlers.get(new RoutePair(method.toUpperCase(), "/ping"));
        }

        File file = new File(root, cleanPath);
        if (file.exists() && file.isDirectory()) {
            return new DirectoryHandler(root, cleanPath);
        }
        if (file.exists() && file.isFile()) {
            return new FileHandler(root, cleanPath);
        }

        return new NotFoundHandler();
    }

    private void sendResponse(Response response, BufferedWriter out) throws IOException {
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append("HTTP/1.1 ")
                .append(response.getStatusCode())
                .append(" ")
                .append(response.getStatusMessage())
                .append("\r\n");

        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            outputBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        outputBuilder.append("\r\n").append(response.getBody());
        out.write(outputBuilder.toString());
        out.flush();
    }
}