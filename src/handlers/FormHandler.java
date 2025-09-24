package handlers;

import server.MultipartParser;
import server.Request;
import server.Response;

import java.util.Map;

import static handlers.SuccessHandler.getSuccessResponse;

public class FormHandler implements RequestHandler {

    private final String root;

    public FormHandler(String root) {
        this.root = root;
    }

    @Override
    public Response handle(Request request) {
        Response response = new Response();
        if ("GET".equals(request.getMethod())) {
            StringBuilder body = new StringBuilder();
            Map<String, String> params = request.getQueryParams();

            body.append("<h2>GET Form</h2><ul>");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                body.append("<li>").append(entry.getKey()).append(": ").append(entry.getValue()).append("</li>");
            }
            body.append("</ul>");

            response = getSuccessResponse(body.toString());
        }

        if ("POST".equals(request.getMethod())) {
            byte[] bodyBytes = request.getRawBody();
            String contentTypeHeader = request.getHeader("Content-Type");

            MultipartParser.Result parsed = MultipartParser.parse(bodyBytes, contentTypeHeader);

            String body = "<h2>POST Form</h2><ul>" +
                    "<li>file name: " + parsed.filename + "</li>" +
                    "<li>content type: " + parsed.contentType + "</li>" +
                    "<li>file size: " + parsed.size + "</li>" +
                    "</ul>";
            response = getSuccessResponse(body);
        }
        return response;
    }
}