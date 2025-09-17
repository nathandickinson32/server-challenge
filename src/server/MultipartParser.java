package server;

import java.nio.charset.StandardCharsets;

public class MultipartParser {

    public static class Result {
        public String filename = "unknown";
        public String contentType = "application/octet-stream";
        public int size = 0;
    }

    public static Result parse(byte[] bodyBytes, String contentType) {
        Result result = new Result();

        if (contentType == null || !contentType.contains("multipart/form-data")) {
            return result;
        }

        String[] boundarySplit = contentType.split("boundary=");

        if (boundarySplit.length < 2) {
            return result;
        }

        String boundary = boundarySplit[1];
        String body = new String(bodyBytes, StandardCharsets.ISO_8859_1);
        String[] parts = body.split("--" + boundary);

        for (String part : parts) {

            int fileIdx = part.indexOf("filename=\"");
            if (fileIdx >= 0) {
                int endIdx = part.indexOf("\"", fileIdx + 10);
                result.filename = part.substring(fileIdx + 10, endIdx);
            }

            int contentIdx = part.indexOf("Content-Type:");
            if (contentIdx >= 0) {
                int endIdx = part.indexOf("\r\n", contentIdx);
                result.contentType = part.substring(contentIdx + 13, endIdx).trim();
            }

            int carriageIdx = part.indexOf("\r\n\r\n");
            if (carriageIdx >= 0) {
                String fileData = part.substring(carriageIdx + 4).trim();
                result.size = fileData.getBytes(StandardCharsets.ISO_8859_1).length;
            }
        }
        return result;
    }
}