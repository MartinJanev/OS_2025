package Networking.Kostadin.TCP.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("TCP server is starting...");

        // Create a server socket class for waiting for incoming connections
        // Constantly listen for incoming connections
        ServerSocket serverSocket = null;

        try {
            // Create a server socket on the specified port
            // The server socket is used to accept incoming connections
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Socket Server failed to start: " + e.getMessage());
            return;
        }
        System.out.println("TCP server is started.");
        System.out.println("Waiting for incoming connections...");
        while (true) {
            // Accept incoming connections
            Socket socket = null;
            try {
                //accept() method blocks until a connection is made
                socket = serverSocket.accept();
                //Instance a new worker thread - this thread will handle the connection
                new Worker(socket).start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
       Server server = new Server(9000);
       server.start();
    }
}
