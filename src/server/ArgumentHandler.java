package server;

import java.io.File;

public class ArgumentHandler {

    public final static String HELP_MESSAGE =
            """
                      -p     Specify the port.  Default is 80.
                      -r     Specify the root directory.  Default is the current working directory.
                      -h     Print this help message
                      -x     Print the startup configuration without starting the server
                    """;

    private static int port = 80;
    private static String rootDir = ".";
    private static boolean printConfig = false;

    public static void parseArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if ("-h".equals(arg)) {
                System.out.println(HELP_MESSAGE);
            } else if ("-x".equals(arg)) {
                printConfig = true;
            } else if ("-p".equals(arg)) {
                if (i + 1 < args.length) {
                    try {
                        port = Integer.parseInt(args[++i]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid port number: " + args[i]);
                    }
                } else {
                    System.err.println("Missing value for -p argument");
                }
            } else if ("-r".equals(arg)) {
                if (i + 1 < args.length) {
                    rootDir = args[++i];
                } else {
                    System.err.println("Missing value for -r argument");
                }
            }
        }
        if (printConfig) {
            printStartupConfig();
        }
    }

    private static void printStartupConfig() {
        System.out.println("Example Server");
        System.out.println("Running on port: " + port);

        File root = new File(rootDir);
        try {
            System.out.println("Serving files from: " + root.getCanonicalPath());
        } catch (Exception e) {
            System.out.println("Serving files from: " + root.getAbsolutePath());
        }
    }

    public static void reset() {
        port = 80;
        rootDir = ".";
        printConfig = false;
    }
}