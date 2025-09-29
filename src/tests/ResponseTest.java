package tests;

import org.junit.Test;
import dto.Response;
import static org.junit.Assert.*;

public class ResponseTest {

    private String serverName = "HttpServer";
    private Response response;

    @Test
    public void defaultConstructorBuildsEmptyResponse() {
        response = new Response();
        assertEquals(200, response.getStatusCode());
        assertEquals(0, response.getBody().length());
        assertEquals(serverName, response.getHeaders().get("Server"));
    }

    @Test
    public void addHeaderAddsAHeader() {
        response = new Response();
        Response result = response.addHeader("Cache-Control", "no-cache");

        assertSame(response, result);
        assertEquals("no-cache", response.getHeaders().get("Cache-Control"));
    }
}