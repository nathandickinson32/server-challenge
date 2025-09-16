package server;

import java.util.HashMap;
import java.util.Map;

public class MimeType {

    private static Map<String, String> mimeTypes = new HashMap<>();

    public MimeType() {
        addMimeTypes();
    }

    public void addMimeTypes() {
        mimeTypes.put("html", "text/html");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("pdf", "application/pdf");
    }

    public static Map<String, String> getMimeTypes() {
        return mimeTypes;
    }

    public static String getMimeType(String fileName) {
        return "text/html";
    }
}
