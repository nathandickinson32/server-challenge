package handlers;

import server.Request;
import server.Response;

import java.io.IOException;

public interface RequestHandler {

    Response handle(Request request) throws IOException;
}