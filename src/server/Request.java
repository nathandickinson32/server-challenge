package server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private String method;
    private String path;
    private String protocol;
    public Map<String, String> headers = new HashMap<>();

    public static Request requestParser(InputStream inputStream) throws IOException {
        Request request = new Request();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
        String requestLine = in.readLine();
        request.parseRequestLine(requestLine);
        request.parseHeaders(in);
        return request;
    }

    private void parseRequestLine(String line) {
        if (line == null)
            throw new IllegalArgumentException("Empty request line");

        String[] parts = line.split(" ");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid request line");
        }

        this.method = parts[0];
        this.path = parts[1];
        this.protocol = parts[2];
    }

    private void parseHeaders(BufferedReader in) throws IOException {
        String headerLine;
        while ((headerLine = in.readLine()) != null && !headerLine.trim().isEmpty()) {
            String[] headerLines = headerLine.split(": ");
            if (headerLines.length != 2) {
                throw new IllegalArgumentException("Invalid Headers");
            }
            headers.put(headerLines[0], headerLines[1]);
        }
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getMethod() {
        return method;
    }
}