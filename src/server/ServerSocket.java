package server;

import java.io.IOException;

public interface ServerSocket {

    Socket accept() throws IOException;

    boolean isClosed();

    void close();
}