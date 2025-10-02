package tests;

import handlers.*;
import org.junit.Test;
import server.FakeSocket;
import server.HttpServer;
import server.Request;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class GuessHandlerTest {

    private GuessHandler guessHandler;

    public HttpServer createTestServer() {
        HttpServer server = new HttpServer(0, 10, new DirectoryHandler("testroot"));
        guessHandler = new GuessHandler();
        server.addHandler("GET", "/guess", guessHandler);
        server.addHandler("POST", "/guess", guessHandler);
        return server;
    }

    @Test
    public void testGetGuessInitialPage() throws IOException {
        HttpServer server = createTestServer();
        FakeSocket fakeSocket = new FakeSocket("GET /guess HTTP/1.1");
        server.handleClient(fakeSocket);
        String response = fakeSocket.getResponse();

        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("<h1>Guess the Number</h1>"));
        assertTrue(response.contains("<form method=\"post\" action=\"/guess\">"));
        assertTrue(response.contains("<label>Enter guess (1 to 100):</label>"));
        assertTrue(response.contains("<input type=\"number\" name=\"guess\" min=\"1\" max=\"100\" required>"));
        assertTrue(response.contains("<button type=\"submit\">Submit</button>"));
    }

    @Test
    public void testPostGuessTooLow() throws IOException {
        HttpServer server = createTestServer();
        String sessionCookie = "sessionId=test1";
        guessHandler.setTestGame("test1", 50, 7);

        String postData = "guess=10";
        FakeSocket fakeSocket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Cookie: " + sessionCookie + "\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(fakeSocket);

//        var request = new Request()
//                .setMethod("POST")
//                .setCookies(new HashMap<String, String>().put("sessionId", sessionCookie))
//                .setParams(new HashMap<String, String>().put("guess", "10"));
//        guessHandler.handle(request);

        FakeSocket getSocket = new FakeSocket("GET /guess HTTP/1.1\r\nCookie: " + sessionCookie + "\r\n\r\n");
        server.handleClient(getSocket);
        String getResponse = getSocket.getResponse();

        assertTrue(getResponse.contains("Too low! Attempts left: 6"));
    }

    @Test
    public void testPostGuessTooHigh() throws IOException {
        HttpServer server = createTestServer();
        String sessionCookie = "sessionId=test2";
        guessHandler.setTestGame("test2", 50, 7);

        String postData = "guess=90";
        FakeSocket socket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Cookie: " + sessionCookie + "\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(socket);

        FakeSocket getSocket = new FakeSocket("GET /guess HTTP/1.1\r\nCookie: " + sessionCookie + "\r\n\r\n");
        server.handleClient(getSocket);
        String getResponse = getSocket.getResponse();

        assertTrue(getResponse.contains("Too high! Attempts left: 6"));
    }

    @Test
    public void testPostCorrectGuess() throws IOException {
        HttpServer server = createTestServer();
        String sessionCookie = "sessionId=test3";
        guessHandler.setTestGame("test3", 50, 7);

        String postData = "guess=50";
        FakeSocket socket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Cookie: " + sessionCookie + "\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(socket);

        FakeSocket getSocket = new FakeSocket("GET /guess HTTP/1.1\r\nCookie: " + sessionCookie + "\r\n\r\n");
        server.handleClient(getSocket);
        String getResponse = getSocket.getResponse();

        assertTrue(getResponse.contains("Correct! The number was 50"));
        assertTrue(getResponse.contains("Starting a new game..."));
    }

    @Test
    public void testPostOutOfAttempts() throws IOException {
        HttpServer server = createTestServer();
        String sessionCookie = "sessionId=test4";
        guessHandler.setTestGame("test4", 50, 1);

        String postData = "guess=10";
        FakeSocket socket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Cookie: " + sessionCookie + "\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(socket);

        FakeSocket getSocket = new FakeSocket("GET /guess HTTP/1.1\r\nCookie: " + sessionCookie + "\r\n\r\n");
        server.handleClient(getSocket);
        String getResponse = getSocket.getResponse();

        assertTrue(getResponse.contains("Out of tries! The number was 50"));
        assertTrue(getResponse.contains("Starting a new game..."));
    }

    @Test
    public void testNewSessionStartsFreshGame() throws IOException {
        HttpServer server = createTestServer();
        String sessionCookie1 = "sessionId=test5";

        FakeSocket postSocket1 = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: 7\r\n" +
                        "Cookie: " + sessionCookie1 + "\r\n\r\n" +
                        "guess=1"
        );
        server.handleClient(postSocket1);

        FakeSocket getSocket1 = new FakeSocket(
                "GET /guess HTTP/1.1\r\n" +
                        "Cookie: " + sessionCookie1 + "\r\n\r\n"
        );
        server.handleClient(getSocket1);
        String response1 = getSocket1.getResponse();
        assertTrue(response1.contains("Attempts left: 6"));

        String sessionCookie2 = "sessionId=test6";
        FakeSocket postSocket2 = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: 7\r\n" +
                        "Cookie: " + sessionCookie2 + "\r\n\r\n" +
                        "guess=2"
        );
        server.handleClient(postSocket2);

        FakeSocket getSocket2 = new FakeSocket(
                "GET /guess HTTP/1.1\r\n" +
                        "Cookie: " + sessionCookie2 + "\r\n\r\n"
        );
        server.handleClient(getSocket2);
        String response2 = getSocket2.getResponse();
        assertTrue(response2.contains("Attempts left: 6"));
    }
}