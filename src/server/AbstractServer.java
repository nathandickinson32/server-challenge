package server;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractServer {
    private final int port;
    private final int maxThreads;

    protected AbstractServer(int port, int maxThreads) {
        this.port = port;
        this.maxThreads = maxThreads;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new NetworkServerSocket(port);
        ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads);

        while (!serverSocket.isClosed()) {
            Socket client = serverSocket.accept();
            threadPool.submit(() -> {
                try {
                    handleClient(client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void handleClient(Socket client) throws IOException {
        try (OutputStream out = client.getOutputStream()) {
            handleSocket(client.getInputStream(), out);
        }
    }

    protected abstract void handleSocket(InputStream in, OutputStream out) throws IOException;
}