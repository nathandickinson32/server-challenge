package server;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private String method;
    private String path;
    private String protocol;
    private byte[] rawBody = new byte[0];
    private Map<String, String> params = new HashMap<>();
    public Map<String, String> headers = new HashMap<>();

    public Request(String method, String path, String protocol) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
    }

    public Request(String method, String path, String protocol, Map<String, String> params, Map<String, String> headers, byte[] body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.params = params != null ? params : new HashMap<>();
        this.headers = headers != null ? headers : new HashMap<>();
        this.rawBody = body != null ? body : new byte[0];
    }


    public Request() {

    }

    public static Request requestParser(InputStream inputStream) throws IOException {
        Request request = new Request();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

        String requestLine = in.readLine();
        request.parseRequestLine(requestLine);
        request.parseHeaders(in);
        request.parseKeyValueStringFromUrl(request.getQueryString());

        String strContentLength = request.headers.get("Content-Length");
        if (strContentLength != null) {
            int contentLength = Integer.parseInt(strContentLength);
            char[] content = new char[contentLength];
            int bodyCharsRead = in.read(content, 0, contentLength);
            if (bodyCharsRead > 0) {
                String bodyString = new String(content, 0, bodyCharsRead);
                request.rawBody = bodyString.getBytes(StandardCharsets.ISO_8859_1);
                String contentType = request.headers.getOrDefault("Content-Type", "");
                if (contentType.startsWith("application/x-www-form-urlencoded")) {
                    request.parseKeyValueStringFromUrl(bodyString);
                }
            }
        }

        return request;
    }

    private void parseRequestLine(String line) {
        if (line == null) throw new IllegalArgumentException("Empty request line");

        String[] parts = line.split(" ");
        if (parts.length < 3) throw new IllegalArgumentException("Invalid request line");

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

    private String getQueryString() {
        if (path != null && path.contains("?")) {
            return path.split("\\?", 2)[1];
        }
        return "";
    }

    private void parseKeyValueStringFromUrl(String input) {
        if (input == null || input.isEmpty()) return;

        for (String pair : input.split("&")) {
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";
            params.put(key, value);
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

    public byte[] getRawBody() {
        return rawBody;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
}