package tests;

import org.junit.Test;
import server.ArgumentHandler;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArgumentHandlerTest {

    @Test
    public void testHelpArg() {
        PrintStream stdout = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);

        String[] args = {"-h"};
        ArgumentHandler.parseArguments(args);
        System.setOut(stdout);

        String output = baos.toString();
        assertTrue(output.contains("  -p     Specify the port.  Default is 80."));
        assertTrue(output.contains("  -r     Specify the root directory.  Default is the current working directory."));
        assertTrue(output.contains("  -h     Print this help message"));
        assertTrue(output.contains("  -x     Print the startup configuration without starting the server"));
        assertTrue(ArgumentHandler.shouldExit());
    }

    @Test
    public void testConfigArg() throws IOException {
        PrintStream stdout = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);

        String[] args = {"-x"};
        ArgumentHandler.parseArguments(args);
        System.setOut(stdout);

        String output = baos.toString();
        File root = new File(".");
        String canonicalPath = root.getCanonicalPath();
        assertTrue(output.contains("Example Server"));
        assertTrue(output.contains("Running on port: 1234"));
        assertTrue(output.contains("Serving files from: " + canonicalPath));
        assertTrue(ArgumentHandler.shouldExit());
    }

    @Test
    public void testMissingPortValue() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream error = System.err;
        System.setErr(new PrintStream(baos));

        String[] args = {"-p"};
        ArgumentHandler.parseArguments(args);
        System.setErr(error);

        String output = baos.toString();
        assertTrue(output.contains("Missing value for -p argument"));
    }

    @Test
    public void testInvalidPortValue() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream error = System.err;
        System.setErr(new PrintStream(baos));

        String[] args = {"-p", "ab12"};
        ArgumentHandler.parseArguments(args);
        System.setErr(error);

        String output = baos.toString();
        assertTrue(output.contains("Invalid port number: ab12"));
    }

    @Test
    public void testPortArg() {
        String[] args = {"-p", "1234"};
        ArgumentHandler.parseArguments(args);

        assertEquals(1234, ArgumentHandler.getPort());
    }

    @Test
    public void testMissingRootArg() {
        ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
        PrintStream error = System.err;
        System.setErr(new PrintStream(errBaos));

        String[] args = {"-r"};
        ArgumentHandler.parseArguments(args);
        System.setErr(error);

        String output = errBaos.toString();
        assertTrue(output.contains("Missing value for -r argument"));
    }

    @Test
    public void testRootArg() throws IOException {
        String[] args = {"-r", "testroot"};
        ArgumentHandler.parseArguments(args);

        File root = new File("testroot");
        assertEquals(root.getCanonicalPath(), new File(ArgumentHandler.getRootDir()).getCanonicalPath());
    }

    @Test
    public void testCombinedArgs() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(baos));

        String[] args = {"-p", "8080", "-r", "testroot", "-x"};
        ArgumentHandler.parseArguments(args);
        System.setOut(stdout);

        File root = new File("testroot");
        String canonicalPath = root.getCanonicalPath();

        String output = baos.toString();
        assertTrue(output.contains("Running on port: 8080"));
        assertTrue(output.contains("Serving files from: " + canonicalPath));
    }
}