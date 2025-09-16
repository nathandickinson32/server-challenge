package server;

import java.io.IOException;

public interface RequestHandler {
    Response handle(Request request) throws IOException;
}