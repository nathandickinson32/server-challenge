package tests;

import org.junit.Test;
import server.FakeSocket;
import handlers.GuessHandler;
import server.HttpServer;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class GuessHandlerTest {

    @Test
    public void testGetGuessInitialPage() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
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
        HttpServer server = new HttpServer(0, "testroot");
        String sessionId = "test1";
        GuessHandler.setTestGame(sessionId, 50, 7);

        String postData = "guess=10";
        FakeSocket fakeSocket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "X-Client-Id: " + sessionId + "\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(fakeSocket);
        String postResponse = fakeSocket.getResponse();

        assertTrue(postResponse.contains("HTTP/1.1 303 See Other"));
        assertTrue(postResponse.contains("Location: /guess"));

        FakeSocket getSocket = new FakeSocket("GET /guess HTTP/1.1\r\nX-Client-Id: " + sessionId + "\r\n\r\n");
        server.handleClient(getSocket);
        String getResponse = getSocket.getResponse();

        assertTrue(getResponse.contains("Too low! Attempts left: 6"));
    }

    @Test
    public void testPostGuessTooHigh() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        String sessionId = "test2";
        GuessHandler.setTestGame(sessionId, 50, 7);

        String postData = "guess=90";
        FakeSocket socket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "X-Client-Id: " + sessionId + "\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(socket);

        FakeSocket getSocket = new FakeSocket("GET /guess HTTP/1.1\r\nX-Client-Id: " + sessionId + "\r\n\r\n");
        server.handleClient(getSocket);
        String getResponse = getSocket.getResponse();

        assertTrue(getResponse.contains("Too high! Attempts left: 6"));
    }

    @Test
    public void testPostCorrectGuess() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        String sessionId = "test3";
        GuessHandler.setTestGame(sessionId, 50, 7);

        String postData = "guess=50";
        FakeSocket socket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "X-Client-Id: " + sessionId + "\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(socket);

        FakeSocket getSocket = new FakeSocket("GET /guess HTTP/1.1\r\nX-Client-Id: " + sessionId + "\r\n\r\n");
        server.handleClient(getSocket);
        String getResponse = getSocket.getResponse();

        assertTrue(getResponse.contains("Correct! The number was 50"));
        assertTrue(getResponse.contains("Starting a new game..."));
    }

    @Test
    public void testPostOutOfAttempts() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        String sessionId = "test4";
        GuessHandler.setTestGame(sessionId, 50, 1);

        String postData = "guess=10";
        FakeSocket socket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "X-Client-Id: " + sessionId + "\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(socket);

        FakeSocket getSocket = new FakeSocket("GET /guess HTTP/1.1\r\nX-Client-Id: " + sessionId + "\r\n\r\n");
        server.handleClient(getSocket);
        String getResponse = getSocket.getResponse();

        assertTrue(getResponse.contains("Out of tries! The number was 50"));
        assertTrue(getResponse.contains("Starting a new game..."));
    }

    @Test
    public void testNewSessionStartsFreshGame() throws Exception {
        HttpServer server = new HttpServer(0, "testroot");
        String session1 = "test5";

        FakeSocket postSocket1 = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: 7\r\n" +
                        "X-Client-Id: " + session1 + "\r\n\r\n" +
                        "guess=1"
        );
        server.handleClient(postSocket1);

        FakeSocket getSocket1 = new FakeSocket(
                "GET /guess HTTP/1.1\r\n" +
                        "X-Client-Id: " + session1 + "\r\n\r\n"
        );
        server.handleClient(getSocket1);
        String response1 = getSocket1.getResponse();
        assertTrue(response1.contains("Attempts left: 6"));

        String session2 = "test6";
        FakeSocket postSocket2 = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: 7\r\n" +
                        "X-Client-Id: " + session2 + "\r\n\r\n" +
                        "guess=2"
        );
        server.handleClient(postSocket2);

        FakeSocket getSocket2 = new FakeSocket(
                "GET /guess HTTP/1.1\r\n" +
                        "X-Client-Id: " + session2 + "\r\n\r\n"
        );
        server.handleClient(getSocket2);
        String response2 = getSocket2.getResponse();
        assertTrue(response2.contains("Attempts left: 6"));
    }
}