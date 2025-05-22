package Networking.http;

import java.io.*;
import java.net.*;
import java.util.*;

public class HTTPServer {

    public static void main(String[] args) {
        int port = 8081;
        ServerSocket serverSocket = null;
        try {
            Socket socket = null;
            serverSocket = new ServerSocket(port);
            while (true) {
                socket = serverSocket.accept();
                Worker worker = new Worker(socket);
                worker.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Worker extends Thread {
    Socket socket;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String request = in.readLine();

            Map<String, String> params = parseUrl(request);

            String name = params.getOrDefault("name", "default");
            String surname = params.getOrDefault("surname", "default");


            //Important: The HTTP response must be sent in the correct format and correct stuff
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println();
            out.println("<html><head><title>Hello</title></head><body>");
            out.println("<h1>Hello, " + name + " " + surname + "!</h1>");
            out.println("</body></html>");

            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Map<String, String> parseUrl(String request) {
        String[] parts = request.split("\\s+");
        String[] p = parts[1].split("\\?")[1].split("&+");
        HashMap<String, String> map = new HashMap<String, String>();
        for (String params : p) {
            String[] keyValue = params.split("=");
            map.put(keyValue[0], keyValue[1]);
        }
        return map;
    }
}

