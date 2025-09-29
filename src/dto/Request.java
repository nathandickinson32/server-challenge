package dto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private String method;
    private String path;
    private String protocol;
    private byte[] rawBody = new byte[0];
    public Map<String, String> headers = new HashMap<>();

    public static Request requestParser(InputStream inputStream) throws IOException {
        Request request = new Request();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
        String requestLine = in.readLine();
        request.parseRequestLine(requestLine);
        request.parseHeaders(in);

        String strContentLength = request.headers.get("Content-Length");
        if (strContentLength != null) {
            int contentLength = Integer.parseInt(strContentLength);
            char[] content = new char[contentLength];
            int bodyCharsRead = in.read(content, 0, contentLength);
            if (bodyCharsRead > 0) {
                request.rawBody = new String(content, 0, bodyCharsRead).getBytes(StandardCharsets.ISO_8859_1);
            }
        }
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
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }
    }

    public Map<String, String> getQueryParams() {
        Map<String, String> params = new HashMap<>();
        if (path != null && path.contains("?")) {
            String[] parts = path.split("\\?", 2);
            String query = parts[1];
            for (String pair : query.split("&")) {
                String[] queryPairs = pair.split("=", 2);
                String key = queryPairs[0];
                String value;
                if (queryPairs.length > 1) {
                    value = queryPairs[1];
                } else {
                    value = "";
                }
                params.put(key, value);
            }
        }
        return params;
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

    public byte[] getRawBody() {
        return rawBody;
    }
}