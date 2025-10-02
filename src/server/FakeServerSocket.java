package server;

import java.io.IOException;

public class FakeServerSocket implements ServerSocket {

    FakeSocket fakeSocket;

    public FakeServerSocket(FakeSocket fakeSocket) {
        this.fakeSocket = fakeSocket;
    }

    @Override
    public Socket accept() throws IOException {
        return fakeSocket;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}