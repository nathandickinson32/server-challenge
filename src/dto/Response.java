package dto;

import java.util.HashMap;
import java.util.Map;

public class Response {

    private String serverName;
    private String body;
    private Map<String, String> headers;
    private int statusCode;
    private String statusMessage;

    public Response() {
        this.serverName = "HttpServer";
        this.body = "";
        this.headers = new HashMap<>();
        this.headers.put("Server", serverName);
        this.statusCode = 200;
        this.statusMessage = "OK";
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Response addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}