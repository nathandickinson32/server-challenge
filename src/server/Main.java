package server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

//        for(String arg :args){
//            System.out.println(arg);
//        }
//       int port = 80;
//       String rootDir = ".";
//
//       HttpServer server = new HttpServer(port, rootDir);
//       try {
//           System.out.println("Starting server on port " + port);
//           server.start();
//       } catch (IOException e) {
//           e.printStackTrace();
//       }

        ArgumentHandler.parseArguments(args);
    }
}