package server;

import handlers.*;

import java.io.*;
import java.util.HashMap;

public class HttpServer extends AbstractServer {

    private final String root;
    private final HashMap<RoutePair, RequestHandler> handlers = new HashMap<>();

    public HttpServer(int port, String root) {
        super(port);
        this.root = root;
    }

    public void addHandler(String method, String path, RequestHandler handler) {
        handlers.put(new RoutePair(method, path), handler);
    }

    @Override
    protected Response handleRequest(Request request) throws IOException {
        String path = request.getPath();
        String cleanPath = path.split("\\?")[0];

        if (cleanPath.endsWith("/") && cleanPath.length() > 1) {
            cleanPath = cleanPath.substring(0, cleanPath.length() - 1);
        }

        String normalizedMethod = request.getMethod().toUpperCase();

        RequestHandler handler = handlers.get(new RoutePair(normalizedMethod, cleanPath));
        if (handler != null) {
            return handler.handle(request);
        }

        return new DirectoryHandler(root).handle(request);
    }
}