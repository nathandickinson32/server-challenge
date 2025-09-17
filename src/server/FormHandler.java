package server;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FormHandler implements RequestHandler {

    private final String root;

    public FormHandler(String root) {
        this.root = root;
    }

    @Override
    public Response handle(Request request){
        Response response = new Response();

        if ("GET".equals(request.getMethod())) {
            StringBuilder body = new StringBuilder();
            Map<String, String> params = request.getQueryParams();

            body.append("<h2>GET Form</h2><ul>");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                body.append("<li>").append(entry.getKey()).append(": ").append(entry.getValue()).append("</li>");
            }
            body.append("</ul>");

            response.setStatusCode(200);
            response.setStatusMessage("OK");
            response.setBody(body.toString());
            response.addHeader("Content-Type", "text/html");
            response.addHeader("Content-Length", String.valueOf(body.toString().getBytes(StandardCharsets.ISO_8859_1).length));

        }

        if ("POST".equals(request.getMethod())) {
            byte[] bodyBytes = request.getRawBody();
            String contentTypeHeader = request.getHeader("Content-Type");

            MultipartParser.Result parsed = MultipartParser.parse(bodyBytes, contentTypeHeader);

            StringBuilder body = new StringBuilder();
            body.append("<h2>POST Form</h2><ul>");
            body.append("<li>file name: ").append(parsed.filename).append("</li>");
            body.append("<li>content type: ").append(parsed.contentType).append("</li>");
            body.append("<li>file size: ").append(parsed.size).append("</li>");
            body.append("</ul>");

            response.setStatusCode(200);
            response.setStatusMessage("OK");
            response.setBody(body.toString());
            response.addHeader("Content-Type", "text/html");
            response.addHeader("Content-Length", String.valueOf(body.toString().getBytes(StandardCharsets.ISO_8859_1).length));
        }
        return response;
    }
}