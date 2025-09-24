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
        FakeSocket socket = new FakeSocket("GET /guess HTTP/1.1");
        server.handleClient(socket);
        String response = socket.getResponse();

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
        GuessHandler.setTestGame(50, 7);

        String postData = "guess=10";
        FakeSocket socket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(socket);
        String response = socket.getResponse();

        assertTrue(response.contains("Too low! Attempts left: 6"));
    }

    @Test
    public void testPostGuessTooHigh() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        GuessHandler.setTestGame(50, 7);

        String postData = "guess=90";
        FakeSocket socket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(socket);
        String response = socket.getResponse();

        assertTrue(response.contains("Too high! Attempts left: 6"));
    }

    @Test
    public void testPostCorrectGuess() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        GuessHandler.setTestGame(50, 7);

        String postData = "guess=50";
        FakeSocket socket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(socket);
        String response = socket.getResponse();

        assertTrue(response.contains("Correct! The number was 50"));
        assertTrue(response.contains("A new game has started."));
    }

    @Test
    public void testPostOutOfAttempts() throws IOException {
        HttpServer server = new HttpServer(0, "testroot");
        GuessHandler.setTestGame(50, 1);

        String postData = "guess=10";
        FakeSocket socket = new FakeSocket(
                "POST /guess HTTP/1.1\r\n" +
                        "Content-Type: application/x-www-form-urlencoded\r\n" +
                        "Content-Length: " + postData.length() + "\r\n\r\n" +
                        postData
        );
        server.handleClient(socket);
        String response = socket.getResponse();
        assertTrue(response.contains("Out of tries! The number was 50"));
        assertTrue(response.contains("A new game has started."));
    }
}