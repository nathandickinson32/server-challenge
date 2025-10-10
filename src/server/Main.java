package server;

import handlers.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final int MAX_THREADS = 1;
        ArgumentHandler.parseArguments(args);
        if (ArgumentHandler.shouldExit()) {
            return;
        }

        int port = ArgumentHandler.getPort();
        String rootDir = ArgumentHandler.getRootDir();

        HttpServer server = new HttpServer(port, MAX_THREADS, new DirectoryHandler(rootDir));
        addHandlers(server, rootDir);
        server.start();
    }

    private static void addHandlers(HttpServer server, String root) {
        server.addHandler("GET", "/index", new FileHandler(root, "index.html"));
        server.addHandler("GET", "/hello", new HelloHandler(root, "hello.html"));

        var formHandler = new FormHandler();
        server.addHandler("GET", "/form", formHandler);
        server.addHandler("POST", "/form", formHandler);

        server.addHandler("GET", "/ping", new PingHandler());
        server.addHandler("GET", "/ping/1", new PingHandler());
        server.addHandler("GET", "/ping/2", new PingHandler());

        var guessHandler = new GuessHandler();
        server.addHandler("GET", "/guess", guessHandler);
        server.addHandler("POST", "/guess", guessHandler);
    }
}