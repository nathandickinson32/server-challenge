package server;

import dto.Request;
import dto.Response;
import dto.RoutePair;
import handlers.*;

import java.io.*;
import java.util.HashMap;

public class HttpServer extends AbstractServer {

    private final String root;
    private final HashMap<RoutePair, RequestHandler> handlers = new HashMap<>();

    public HttpServer(int port, String root) {
        super(port);
        this.root = root;
        addHandlers();
    }

    private void addHandlers() {
        handlers.put(new RoutePair("GET", "/index"), new FileHandler(root, "index.html"));
        handlers.put(new RoutePair("GET", "/hello"), new HelloHandler(root, "hello.html"));
        handlers.put(new RoutePair("GET", "/form"), new FormHandler());
        handlers.put(new RoutePair("POST", "/form"), new FormHandler());
        handlers.put(new RoutePair("GET", "/ping"), new PingHandler());
        handlers.put(new RoutePair("GET", "/guess"), new GuessHandler());
        handlers.put(new RoutePair("POST", "/guess"), new GuessHandler());
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
        if (handler != null) return handler.handle(request);

        if (cleanPath.startsWith("/ping")) {
            return handlers.get(new RoutePair(normalizedMethod, "/ping")).handle(request);
        }

        File file = new File(root, cleanPath);

        if (cleanPath.startsWith("/listing") || file.isDirectory()) {
            return new DirectoryHandler(root, cleanPath).handle(request);
        }

        if (file.isFile()) {
            return new FileHandler(root, cleanPath).handle(request);
        }

        return new NotFoundHandler().handle(request);
    }
}