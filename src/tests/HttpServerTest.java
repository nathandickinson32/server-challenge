package tests;

import org.junit.Test;
import server.*;
import java.io.IOException;
import static org.junit.Assert.assertTrue;

public class HttpServerTest {

    @Test
    public void testRespondsHelloWorld() throws IOException {
        HttpServer server = new HttpServer(0, ".");
        FakeSocket socket = new FakeSocket("GET / HTTP/1.1");
        server.handleClient(socket);
        assertTrue(socket.getResponse().contains("HTTP/1.1 "));
        assertTrue(socket.getResponse().contains("Content-Type: text/html"));
        assertTrue(socket.getResponse().contains("<body>"));
        assertTrue(socket.getResponse().contains("Hello, World!"));
    }
}