package tests;

import org.junit.Test;
import server.Request;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class RequestTest {
    private Request request = new Request();

    @Test
    public void testMethodResponse() throws IOException {
        InputStream is = new ByteArrayInputStream("".getBytes());
        assertThrows(IllegalArgumentException.class, () -> {
            Request.requestParser(is);
        });
    }

    @Test
    public void testInvalidMethodResponse() throws IOException {
        InputStream is = new ByteArrayInputStream("blah \r\n second line".getBytes());
        assertThrows(IllegalArgumentException.class, () -> {
            Request.requestParser(is);
        });
    }

    @Test
    public void testGetMethodResponse() throws IOException {
        InputStream is = new ByteArrayInputStream("GET / HTTP/1.1".getBytes());
        request = Request.requestParser(is);
        assertEquals("GET", request.getMethod());
    }

    @Test
    public void parserParsesIntoParts() throws IOException {
        InputStream is = new ByteArrayInputStream("GET / HTTP/1.1".getBytes());
        request = Request.requestParser(is);
        assertEquals("GET", request.getMethod());
        assertEquals("/", request.getPath());
        assertEquals("HTTP/1.1", request.getProtocol());
    }

    @Test
    public void parserParsesIntoPartsReflectingTheRequest() throws IOException {
        InputStream is = new ByteArrayInputStream("POST /hello.html HTTP/2.1".getBytes());
        request = Request.requestParser(is);
        assertEquals("POST", request.getMethod());
        assertEquals("/hello.html", request.getPath());
        assertEquals("HTTP/2.1", request.getProtocol());
    }

    @Test
    public void testMalformedHeaders() throws IOException {
        InputStream is = new ByteArrayInputStream("GET / HTTP/1.1\r\nblah".getBytes());
        assertThrows(IllegalArgumentException.class, () -> {
            Request.requestParser(is);
        });
    }

    @Test
    public void testRetrievableHeaders() throws IOException {
        String rawRequest = "GET /hello HTTP/1.1\r\n"
                + "Host: example.com\r\n";
        InputStream is = new ByteArrayInputStream(rawRequest.getBytes());
        request = Request.requestParser(is);
        assertEquals("example.com", request.getHeader("Host"));
    }
}