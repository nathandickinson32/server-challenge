package tests;

import org.junit.Test;
import server.*;
import java.io.IOException;
import static org.junit.Assert.*;

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
    public void testListingsWhenNoIndexHtml() throws IOException {
        HttpServer server = new HttpServer(0, ".");
        FakeSocket socket = new FakeSocket("GET /index HTTP/1.1");
        server.handleClient(socket);
        String response = socket.getResponse();
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Server"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("<!DOCTYPE html><html><body>"));
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
}