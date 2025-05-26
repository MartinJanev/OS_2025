package Networking.ZadaciZaVezbanje.Juni2024;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {

    public static void main(String[] args) throws IOException {
        new TcpServer().run();
    }

    ServerSocket serverSocket;

    TcpServer() throws IOException {
        serverSocket = new ServerSocket(7391);
    }

    public void run() {
        System.out.println("Server is ready to accept connections!");
        while(true) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("A new client is connected: " + client.getInetAddress());
                new Worker(client).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
