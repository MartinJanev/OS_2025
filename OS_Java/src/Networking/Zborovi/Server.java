package Networking.Zborovi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// opsluzuva poveke klienti (extends Thread)
public class Server extends Thread{
    int port;
    String logFilePath;

    public Server(int port, String logFilePath) {
        this.port = port;
        this.logFilePath = logFilePath;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Socket created");
        while(true) {
            // wait
            System.out.println("Waiting for connection");
            try {
                //blockiracka operacija
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection!");
                new Worker(clientSocket, logFilePath).start();
                System.out.println("Worker started");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // sekogas vraka string, ako portata e env togas treba integer.parseint
        String filePath = System.getenv("LOG_FILE_PATH");
        if (filePath == null) {
            throw new RuntimeException("Enter env variable.");
        }
        // za da go testiras env:
        // 1. so fiksen file napocetok, pa posle napravi env
        // ili
        // 2. vo terminal export LOG_FILE_PATH=logs.txt
        // java ...path/Server.java
        Server server = new Server(7391, filePath);
        server.start();
    }
}
