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
        addHandlers();
    }

    private void addHandlers() {
        handlers.put(new RoutePair("GET", "/index"), new FileHandler(root, "index.html"));
        handlers.put(new RoutePair("GET", "/hello"), new HelloHandler(root, "hello.html"));

        var formHandler = new FormHandler();
        handlers.put(new RoutePair("GET", "/form"), formHandler);
        handlers.put(new RoutePair("POST", "/form"), formHandler);
        handlers.put(new RoutePair("GET", "/ping"), new PingHandler());
        handlers.put(new RoutePair("GET", "/ping/1"), new PingHandler());
        handlers.put(new RoutePair("GET", "/ping/2"), new PingHandler());

        var guessHandler = new GuessHandler();
        handlers.put(new RoutePair("GET", "/guess"), guessHandler);
        handlers.put(new RoutePair("POST", "/guess"), guessHandler);
        handlers.put(new RoutePair("GET", "/listing"), new DirectoryHandler(root, "/listing"));
        handlers.put(new RoutePair("GET", "/listing/img"), new DirectoryHandler(root, "/listing/img"));
        handlers.put(new RoutePair("GET", "/"), new DirectoryHandler(root, ""));
//        handlers.put(new RoutePair("GET", "*"), new DirectoryHandler(null));
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
        if (handler != null) return handler.handle(request);

        File file = new File(root, cleanPath);
        if (file.isFile()) {
            return new FileHandler(root, cleanPath).handle(request);
        }

        return new NotFoundHandler().handle(request);
    }
}