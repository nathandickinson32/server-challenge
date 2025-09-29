package tests;

import org.junit.Test;
import dto.Request;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class RequestTest {
    private Request request = new Request();

    @Test
    public void testMethodResponse() {
        InputStream is = new ByteArrayInputStream("".getBytes());
        assertThrows(IllegalArgumentException.class, () -> {
            Request.requestParser(is);
        });
    }

    @Test
    public void testInvalidMethodResponse() {
        String rawRequest = "blah \r\n second line";
        InputStream is = new ByteArrayInputStream(rawRequest.getBytes());
        assertThrows(IllegalArgumentException.class, () -> {
            Request.requestParser(is);
        });
    }

    @Test
    public void testGetMethodResponse() throws IOException {
        String rawRequest = "GET / HTTP/1.1";
        InputStream is = new ByteArrayInputStream(rawRequest.getBytes());
        request = Request.requestParser(is);
        assertEquals("GET", request.getMethod());
    }

    @Test
    public void parserParsesIntoParts() throws IOException {
        String rawRequest = "GET / HTTP/1.1";
        InputStream is = new ByteArrayInputStream(rawRequest.getBytes());
        request = Request.requestParser(is);
        assertEquals("GET", request.getMethod());
        assertEquals("/", request.getPath());
        assertEquals("HTTP/1.1", request.getProtocol());
    }

    @Test
    public void parserParsesIntoPartsReflectingTheRequest() throws IOException {
        String rawRequest = "POST /hello.html HTTP/2.1";
        InputStream is = new ByteArrayInputStream(rawRequest.getBytes());
        request = Request.requestParser(is);
        assertEquals("POST", request.getMethod());
        assertEquals("/hello.html", request.getPath());
        assertEquals("HTTP/2.1", request.getProtocol());
    }

    @Test
    public void testRetrievableHeaders() throws IOException {
        String rawRequest = "GET /hello HTTP/1.1\r\nHost: example.com\r\n";
        InputStream is = new ByteArrayInputStream(rawRequest.getBytes());
        request = Request.requestParser(is);
        assertEquals("example.com", request.getHeader("Host"));
    }
}