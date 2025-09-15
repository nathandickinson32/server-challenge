package server;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FakeSocket implements Socket {

    private final ByteArrayInputStream in;
    private ByteArrayOutputStream out;

    public FakeSocket(String request) {
        this.in = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
        this.out = new ByteArrayOutputStream();
    }

    public String getResponse() {
        return out.toString();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return in;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.out;
    }
}