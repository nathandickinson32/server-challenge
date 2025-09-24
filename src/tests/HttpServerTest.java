package tests;

import org.junit.Test;
import server.FakeSocket;
import server.HttpServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static handlers.PingHandler.FORMATTER;

public class HttpServerTest {

    @Test
    public void testRespondsToInvalidPath() throws IOException {
        HttpServer server = new HttpServer(0, ".");
        FakeSocket socket = new FakeSocket("GET /blah HTTP/1.1");
        server.handleClient(socket);
        String response = socket.getResponse();
        assertTrue(response.contains("404 Not Found"));
    }

    @Test
    public void testRespondsToHelloHtml() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        FakeSocket socket = new FakeSocket("GET /hello HTTP/1.1");
        server.handleClient(socket);
        String response = socket.getResponse();
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Server"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("Content-Length:"));
        assertTrue(response.contains("<h1>Hello, Welcome to Nathan's Server!</h1>"));
    }

    @Test
    public void testRespondsHelloWorld() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        FakeSocket socket = new FakeSocket("GET / HTTP/1.1");
        server.handleClient(socket);
        String response = socket.getResponse();
        int contentLength = Integer.parseInt(response.split("Content-Length: ")[1].split("\r\n")[0]);
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Server"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("Content-Length:"));
        assertTrue(response.contains("<h1>Hello, World!</h1>"));
        assertEquals(106, contentLength);
    }

    @Test
    public void testRespondsToIndex() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        FakeSocket socket = new FakeSocket("GET /index HTTP/1.1");
        server.handleClient(socket);
        String response = socket.getResponse();
        int contentLength = Integer.parseInt(response.split("Content-Length: ")[1].split("\r\n")[0]);
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Server"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("Content-Length:"));
        assertTrue(response.contains("<h1>Hello, World!</h1>"));
        assertEquals(106, contentLength);
    }

    @Test
    public void testRequestForListing() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        FakeSocket socket = new FakeSocket("GET /listing HTTP/1.1");
        server.handleClient(socket);
        String response = socket.getResponse();
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Server"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("Content-Length:"));
        assertTrue(response.contains("<li><a href=\"/index.html\">index.html</a></li>"));
        assertTrue(response.contains("<li><a href=\"/forms.html\">forms.html</a></li>"));
        assertTrue(response.contains("<li><a href=\"/hello.pdf\">hello.pdf</a></li>"));
    }

    @Test
    public void testRequestForListingImages() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        FakeSocket socket = new FakeSocket("GET /listing/img HTTP/1.1");
        server.handleClient(socket);

        String response = socket.getResponse();

        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Server"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("Content-Length:"));
        assertTrue(response.contains("<li><a href=\"/img/autobot.jpg\">autobot.jpg</a></li>"));
        assertTrue(response.contains("<li><a href=\"/img/autobot.png\">autobot.png</a></li>"));
        assertTrue(response.contains("<li><a href=\"/img/decepticon.jpg\">decepticon.jpg</a></li>"));
        assertTrue(response.contains("<li><a href=\"/img/decepticon.png\">decepticon.png</a></li>"));
    }

    @Test
    public void testRequestForPdf() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        FakeSocket socket = new FakeSocket("GET /hello.pdf HTTP/1.1");
        server.handleClient(socket);

        String response = socket.getResponse();
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Server"));
        assertTrue(response.contains("Content-Type: application/pdf"));
        assertTrue(response.contains("Content-Length:"));
    }

    @Test
    public void testRequestForPng() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        FakeSocket socket = new FakeSocket("GET /img/decepticon.png HTTP/1.1");

        server.handleClient(socket);
        String response = socket.getResponse();

        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Server"));
        assertTrue(response.contains("Content-Type: image/png"));
        assertTrue(response.contains("Content-Length:"));
    }

    @Test
    public void testRequestForJpg() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        FakeSocket socket = new FakeSocket("GET /img/decepticon.jpg HTTP/1.1");

        server.handleClient(socket);
        String response = socket.getResponse();

        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Server"));
        assertTrue(response.contains("Content-Type: image/jpeg"));
        assertTrue(response.contains("Content-Length:"));
    }

    @Test
    public void testGetFormWithQueryParams() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        FakeSocket socket = new FakeSocket("GET /form?foo=1&bar=2 HTTP/1.1");
        server.handleClient(socket);
        String response = socket.getResponse();
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("<h2>GET Form</h2>"));
        assertTrue(response.contains("<li>foo: 1</li>"));
        assertTrue(response.contains("<li>bar: 2</li>"));
    }

    @Test
    public void testPostFormFileUpload() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");

        String body = "--XYZ\r\n Content-Disposition: form-data; name=\"file\"; filename=\"autobot.jpg\"\r\n" +
                "Content-Type: image/jpeg\r\n\r\n" +
                "FAKEJPEGDATA123" + "\r\n" +
                "--XYZ--\r\n";

        String request = "POST /form HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Type: multipart/form-data; boundary=XYZ\r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.ISO_8859_1).length + "\r\n" +
                "\r\n" +
                body;

        FakeSocket socket = new FakeSocket(request);

        server.handleClient(socket);
        String response = socket.getResponse();

        assertTrue(response.startsWith("HTTP/1.1 200 OK"));
        assertTrue(response.contains("<li>file name: autobot.jpg</li>"));
        assertTrue(response.contains("<li>content type: image/jpeg</li>"));
        assertTrue(response.contains("<h2>POST Form</h2>"));
        assertTrue(response.contains("\r\n\r\n"));
    }

    @Test
    public void testPingHandler() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        FakeSocket socket = new FakeSocket("GET /ping HTTP/1.1");
        server.handleClient(socket);
        String response = socket.getResponse();

        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("<h2>Ping</h2>"));
        assertTrue(response.contains("<li>start time:"));
        assertTrue(response.contains("<li>end time:"));
    }

    @Test
    public void testPingOneSecondDelay() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");

        FakeSocket socket = new FakeSocket("GET /ping/1 HTTP/1.1");
        server.handleClient(socket);
        String response = socket.getResponse();

        String startTimeStr = response.split("<li>start time: ")[1].split("</li>")[0];
        String endTimeStr = response.split("<li>end time: ")[1].split("</li>")[0];
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, FORMATTER);
        long secondsPassed = java.time.Duration.between(startTime, endTime).getSeconds();
        assertTrue(secondsPassed >= 1);
    }

    @Test
    public void testPingTwoSecondDelay() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");

        FakeSocket socket = new FakeSocket("GET /ping/2 HTTP/1.1");
        server.handleClient(socket);
        String response = socket.getResponse();

        String startTimeStr = response.split("<li>start time: ")[1].split("</li>")[0];
        String endTimeStr = response.split("<li>end time: ")[1].split("</li>")[0];
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, FORMATTER);
        long secondsPassed = java.time.Duration.between(startTime, endTime).getSeconds();
        assertTrue(secondsPassed >= 2);
    }
}