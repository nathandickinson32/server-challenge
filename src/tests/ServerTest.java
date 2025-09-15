package tests;

import org.junit.Test;
import server.FakeServerSocket;
import server.FakeSocket;
import server.Server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// HTTP/1.1 200 OK
// HEADER: some-val
// ANOTHER_HEADER: another-val
//
 // maybe a body

public class ServerTest {

    @Test
    public void testRespondsHelloWorld() {
        Server server = new Server(0, ".");
        ByteArrayInputStream request = new ByteArrayInputStream("GET / HTTP/1.1".getBytes());
        OutputStream response = new ByteArrayOutputStream();
        server.handleRequest(request, response);
        String[] responseLines = response.toString().split("\r\n");
        assertEquals("HTTP/1.1 200 OK", responseLines[0]);
        assertEquals("", responseLines[1]);
        assertEquals("<h1>Hello, World!</h1>", responseLines[2]);

        //assertEquals(200, server.getStatusCode(response.toString()));
    }

    @Test
    public void testServerAcceptsConnectionAndRespondsHelloWorld() throws IOException {
        String request = "GET / HTTP/1.1";
        FakeSocket fakeSocket = new FakeSocket(request);
        FakeServerSocket fakeServerSocket = new FakeServerSocket(fakeSocket);

        Server server = new Server(fakeServerSocket);
        server.start();

        String response = new String(fakeSocket.getResponse());
        assertTrue(response.startsWith("HTTP/1.1 200 OK"));
        assertTrue(response.contains("<h1>Hello, World!</h1>"));
    }

//    @Test
//    public void testInvalidRequestReturns400() {
//        Server server = new Server(0, ".");
//        ByteArrayInputStream request = new ByteArrayInputStream("GET".getBytes());
//        OutputStream response = new ByteArrayOutputStream();
//        server.handleRequest(request, response);
//        assertEquals(400, server.getStatusCode(response.toString()));
//    }

//    @Test
//    public void testInvalidPathReturns404() {
//        Server server = new Server(0, ".");
//        String response = server.handleRequest("GET /blah HTTP/1.1");
//        assertEquals(404, server.getStatusCode(response));
//    }
//
//
//    @Test
//    public void testRootResponse() {
//        Server server = new Server(0, ".");
//        String response = server.handleRequest("GET / HTTP/1.1");
//        assertEquals(200, server.getStatusCode(response));
//        assertEquals("<h1>Hello, World!</h1>", server.getBody(response));
//    }
//
//    @Test
//    public void testIndexHtmlPath() {
//        Server server = new Server(0, ".");
//        String response = server.handleRequest("GET /index.html HTTP/1.1");
//        assertEquals(200, server.getStatusCode(response));
//        assertEquals("<h1>Hello, World!</h1>", server.getBody(response));
//    }
//
//    @Test
//    public void testEmptyRequestServesIndexHtml() {
//        Server server = new Server(0, ".");
//        String response = server.handleRequest("");
//        assertEquals(200, server.getStatusCode(response));
//        assertEquals("<h1>Hello, World!</h1>", server.getBody(response));
//    }
//
//    @Test
//    public void testUnsupportedMethodReturns405() {
//        Server server = new Server(0, ".");
//        String response = server.handleRequest("POST / HTTP/1.1");
//        assertEquals(405, server.getStatusCode(response));
//        assertEquals("<h1>405 Method Not Allowed</h1>", server.getBody(response));
//    }
//
//    @Test
//    public void testServerErrorReturns500() throws IOException {
//        Path tempDir = Files.createTempDirectory("testroot");
//        Path badFile = tempDir.resolve("badfile.txt");
//        Files.createDirectory(badFile);
//
//        Server server = new Server(0, tempDir.toString());
//
//        String response = server.handleRequest("GET /badfile.txt HTTP/1.1");
//
//        assertEquals(500, server.getStatusCode(response));
//        assertEquals("<h1>500 Internal Server Error</h1>", server.getBody(response));
//    }
//
//    @Test
//    public void testServeFileFromRootDir() throws IOException {
//        Path tempDir = Files.createTempDirectory("testroot");
//        Path testFile = tempDir.resolve("hello.txt");
//        Files.writeString(testFile, "Hello World");
//
//        Server server = new Server(0, tempDir.toString());
//
//        String response = server.handleRequest("GET /hello.txt HTTP/1.1");
//
//        assertEquals(200, server.getStatusCode(response));
//        assertTrue(server.getBody(response).contains("Hello World"));
//    }
//
//    @Test
//    public void testEscapeRootAttemptReturns404() throws IOException {
//        Path tempDir = Files.createTempDirectory("testroot");
//        Path secretFile = tempDir.getParent().resolve("secret.txt");
//        Files.writeString(secretFile, "Private Access");
//
//        Server server = new Server(0, tempDir.toString());
//
//        String response = server.handleRequest("GET /../secret.txt HTTP/1.1");
//
//        assertEquals(404, server.getStatusCode(response));
//        assertTrue(server.getBody(response).contains("404"));
//    }
}