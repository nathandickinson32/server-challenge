package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ClientSocket implements Socket{

    private final java.net.Socket socket;

    public ClientSocket (java.net.Socket socket) {
        this.socket = socket;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }
}