package server;

import java.io.*;

public interface Socket {

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;
}