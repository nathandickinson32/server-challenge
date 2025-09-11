package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private final int port;
    private final String root;
    private ServerSocket serverSocket;

    public HttpServer(int port, String root) {
        this.port = port;
        this.root = root;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        while (!serverSocket.isClosed()) {
            Socket client = serverSocket.accept();
            handleClient(client);
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private void handleClient(Socket client) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

            while (in.ready()) {
                System.out.println(in.readLine());
            }

            // process the request somehow

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Length: 13");
            out.println();
            out.println("Hello, Nathan");
        }
    }
}