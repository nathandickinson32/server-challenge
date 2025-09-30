package server;

import handlers.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
    protected void handleSocket(InputStream in, OutputStream out) throws IOException {
        Request request = Request.requestParser(in);

        String path = request.getPath();
        String cleanPath = path.split("\\?")[0];
        if (cleanPath.endsWith("/") && cleanPath.length() > 1) {
            cleanPath = cleanPath.substring(0, cleanPath.length() - 1);
        }

        String method = request.getMethod().toUpperCase();
        RequestHandler handler = handlers.get(new RoutePair(method, cleanPath));

        Response response;
        if (handler != null) {
            response = handler.handle(request);
        } else {
            response = new DirectoryHandler(root).handle(request);
        }

        sendResponse(response, out);
    }

    private void sendResponse(Response response, OutputStream out) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ")
                .append(response.getStatusCode()).append(" ")
                .append(response.getStatusMessage()).append("\r\n");

        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        sb.append("\r\n").append(response.getBody());
        out.write(sb.toString().getBytes(StandardCharsets.ISO_8859_1));
        out.flush();
    }
}