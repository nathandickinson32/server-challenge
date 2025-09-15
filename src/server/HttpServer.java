package server;

import java.io.*;
import java.util.Map;

public class HttpServer {

    private final int port;
    private final String root;
    private ServerSocket serverSocket;
    private String serverName = "Nathan's Server";

    public HttpServer(int port, String root) {
        this.port = port;
        this.root = root;
    }

    public void start() throws IOException {
        serverSocket = new NetworkServerSocket(port);
        while (!serverSocket.isClosed()) {
            Socket client = serverSocket.accept();
            handleClient(client);
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public void handleClient(Socket client) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))) {
            Request request;
            request = Request.requestParser(client.getInputStream());
            System.out.println(request.getMethod() + "  jfdkf");


            Response response = new Response(serverName);

//            (Fix the response to be correct)
            sendResponse(response, out);
            out.write("HTTP/1.1 200 OK");
            out.write("Content-Length: 13");
            out.write("\r\n\r\n");
            out.write("<body>Hello, World!<body>");
        }
    }

    private void sendResponse(Response response, BufferedWriter out) throws IOException {
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append("HTTP/1.1 ").append(response.getStatusCode()).append(" ").append("OK\r\n");
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            outputBuilder.append(key).append(": ").append(value).append("\r\n");
        }
        if (!response.getBody().isEmpty()) {
            outputBuilder.append(response.getBody());
        }
        out.write(outputBuilder.toString());
    }
}