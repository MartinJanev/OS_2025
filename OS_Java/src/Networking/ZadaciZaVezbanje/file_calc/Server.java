package Networking.ZadaciZaVezbanje.file_calc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private int port;
    private String totalLinesFilepath;
    private String aggregatedFilepath;

    public Server(int port, String totalLinesFilepath, String aggregatedFilepath) {
        this.port = port;
        this.totalLinesFilepath = totalLinesFilepath;
        this.aggregatedFilepath = aggregatedFilepath;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            Socket socket = null;

            //Wait for a client to connect and send to the worker class
            while (true) {
                socket = serverSocket.accept();
                new Worker(socket, totalLinesFilepath, aggregatedFilepath).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8081,
                "Networking/file_calc/data/total_lines.bin",
                "Networking/file_calc/data/aggregated.txt");
        server.start(); //Start the server
    }
}