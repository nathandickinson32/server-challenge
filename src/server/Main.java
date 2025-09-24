package server;

import handlers.ArgumentHandler;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        ArgumentHandler.parseArguments(args);
        if (ArgumentHandler.shouldExit()) {
            return;
        }

        int port = ArgumentHandler.getPort();
        String rootDir = ArgumentHandler.getRootDir();
        HttpServer server = new HttpServer(port, rootDir);
        server.start();
    }
}