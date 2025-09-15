package tests;

import org.junit.Test;
import server.Response;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ResponseTest {

    private String serverName = "Nathan's Server";
    private Response response;

    @Test
    public void defaultConstructorBuildsEmptyResponse() {
        response = new Response(serverName);
        assertEquals(200, response.getStatusCode());
        assertEquals(0, response.getBody().length());
        assertEquals(serverName, response.getHeaders().get("Server"));
        assertEquals("text/html", response.getHeaders().get("Content-Type"));
    }

    @Test
    public void addHeaderAddsAHeader() {
        response = new Response(serverName);
        Response result = response.addHeader("Cache-Control", "no-cache");

        assertSame(response, result);
        assertEquals("no-cache", response.getHeaders().get("Cache-Control"));

    }
}