package handlers;

import dto.Request;
import dto.Response;

import java.io.IOException;

public interface RequestHandler {

    Response handle(Request request) throws IOException;
}