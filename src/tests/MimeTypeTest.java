package tests;

import org.junit.Test;
import server.MimeType;

import static org.junit.Assert.assertEquals;

public class MimeTypeTest {

    @Test
    public void testGetMimeType() {
        MimeType mimeType = new MimeType();
        assertEquals("text/html", mimeType.getMimeType("index.html"));
//        assertEquals("image/png", mimeType.getMimeType("image.png"));
//        assertEquals("image/jpeg", mimeType.getMimeType("photo.JPG"));
//        assertEquals("application/pdf", mimeType.getMimeType("file.pdf"));
    }
}
//            File[] files = file.listFiles();
//            StringBuilder body = new StringBuilder();
//            body.append("<!DOCTYPE html><html><body><ul>");
//            for (File currentFile : files) {
//                String name = currentFile.getName();
//                body.append("<li><a href=\"/img/").append(name)
//                        .append("\">").append(name).append("</a></li>");
//            }
//            body.append("</ul>").append("</body></html>");