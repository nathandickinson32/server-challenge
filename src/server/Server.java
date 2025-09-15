package server;

import java.io.*;

public class Server {
    private int port;
    private String rootDir;
    private ServerSocket serverSocket;

    public Server(int port, String rootDir) {
        this.port = port;
        this.rootDir = rootDir;
    }

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void start() throws IOException {
        Socket socket = serverSocket.accept();
        handleRequest(socket.getInputStream(), socket.getOutputStream());
        socket.getOutputStream().flush();
        socket.close();
    }

    public void handleRequest(InputStream request, OutputStream response) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(request));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(response))) {
            out.write("HTTP/1.1 200 OK\r\n\r\n<h1>Hello, World!</h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public void handleRequest(InputStream request, OutputStream response) {
//        try (BufferedReader in = new BufferedReader(new InputStreamReader(request));
//             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(response))) {
//            out.write("HTTP/1.1 200 OK\r\n\r\n<h1>Hello, World!</h1>");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


//        if (requestLine == null || requestLine.trim().isEmpty()) {
//            return "<h1>Hello, World!</h1>";
//        }
//
//        String[] requestParts = RequestHandler.parseRequestLine(requestLine);
//        if (requestParts.length < 2) {
//            return "<h1>400 Bad Request</h1>";
//        }
//
//        String method = requestParts[0];
//        String path = requestParts[1];
//        if (!"GET".equals(method)) {
//            return "<h1>405 Method Not Allowed</h1>";
//        }
//
//        if ("/".equals(path) || "/index.html".equals(path)) {
//            return "<h1>Hello, World!</h1>";
//        }
//
//        if (path.startsWith("/")) {
//            path = path.substring(1);
//        }
//
//        File file = new File(rootDir, path);
//
//        try {
//            if (!file.getCanonicalPath().startsWith(new File(rootDir).getCanonicalPath()) || !file.exists()) {
//                return "<h1>404 Not Found</h1>";
//            }
//            return new String(Files.readAllBytes(file.toPath()));
//        } catch (Exception e) {
//            return "<h1>500 Internal Server Error</h1>";
//        }
   // }

    public int getStatusCode(String response) {
        if (response.equals("<h1>Hello, World!</h1>")) {
            return 200;
        } else if (response.equals("<h1>400 Bad Request</h1>")) {
            return 400;
        } else if (response.equals("<h1>405 Method Not Allowed</h1>")) {
            return 405;
        } else if (response.equals("<h1>404 Not Found</h1>")) {
            return 404;
        } else if (response.equals("<h1>500 Internal Server Error</h1>")) {
            return 500;
        } else return 200;
    }

    public String getBody(String response) {
        return response;
    }
}