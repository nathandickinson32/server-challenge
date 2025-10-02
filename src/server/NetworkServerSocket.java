package server;

import java.io.IOException;

public class NetworkServerSocket implements ServerSocket{

    private java.net.ServerSocket serverSocket;

    public NetworkServerSocket(int port) throws IOException {
        this.serverSocket = new java.net.ServerSocket(port);
    }

    @Override
    public Socket accept() throws IOException {
        return new ClientSocket(this.serverSocket.accept());
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }
}