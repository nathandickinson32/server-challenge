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

    public static Request requestParser(InputStream request) throws IOException {
        Request request1 = new Request();
        BufferedReader in = new BufferedReader(new InputStreamReader(request, StandardCharsets.ISO_8859_1));
        String requestLine = in.readLine();
        request1.parseRequestLine(requestLine);
        request1.parseHeaders(in);
        return request1;
    }

    private void parseRequestLine(String line) {
        System.out.println(line);
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
            String[] headerlines = headerLine.split(": ");
            if (headerlines.length != 2) {
                throw new IllegalArgumentException("Invalid Headers");
            }
            headers.put(headerlines[0], headerlines[1]);

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