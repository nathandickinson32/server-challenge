package server;

import java.io.*;

public abstract class AbstractServer {

    private final int port;
    private ServerSocket serverSocket;

    protected AbstractServer(int port) {
        this.port = port;
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

    public void handleClient(Socket client) throws IOException {
        try (OutputStream out = client.getOutputStream()) {
            handleSocket(client.getInputStream(), out);
        }
    }

    protected abstract void handleSocket(InputStream in, OutputStream out) throws IOException;
}