package tests;

import org.junit.Test;
import server.FakeServerSocket;
import server.FakeSocket;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class FakeServerSocketTest {

    @Test
    public void testAcceptsFakeSocket() throws IOException {
        FakeSocket fakeSocket = new FakeSocket("blah");
        FakeServerSocket fakeServerSocket = new FakeServerSocket(fakeSocket);
        assertEquals(fakeSocket, fakeServerSocket.accept());
    }
}
