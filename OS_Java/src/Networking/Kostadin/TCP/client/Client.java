package Networking.Kostadin.TCP.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {
    private String serverName;
    private int serverPort;

    public Client(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        Socket socket = null;

        //Here we enter the info we want to send to the server
        Scanner scanner = null;
        //for the responses from the server
        PrintWriter writer = null;
        BufferedReader reader = null;

        try {
            //Instantiate new socket object
            //The socket is used to connect to the server
            socket = new Socket(serverName, serverPort);

            writer = new PrintWriter(socket.getOutputStream());
            scanner = new Scanner(System.in);

            while (true) {
                String line = scanner.nextLine();
                //We send the line to the server
                writer.println(line);
                writer.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 9000);
        client.start();
    }
}
