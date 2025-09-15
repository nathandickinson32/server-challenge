package tests;

import org.junit.Test;
import server.FakeSocket;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;

public class FakeSocketTest {

    @Test
    public void testGetInputStream() throws IOException {
        FakeSocket socket = new FakeSocket("blah");
        assertEquals("blah", new String(socket.getInputStream().readAllBytes()));
    }

    @Test
    public void testGetOutStream() throws IOException {
        FakeSocket socket = new FakeSocket("");
        OutputStream out = socket.getOutputStream();
        out.write("blah".getBytes());

        assertEquals("blah", new String(socket.getResponse()));
    }
}