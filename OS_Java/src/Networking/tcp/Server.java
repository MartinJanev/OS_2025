package Networking.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    int portNumber;

    public Server(int portNumber) {
        this.portNumber = portNumber;
    }

    public void run() {
        //Initialize the server socket - null for now
        ServerSocket serverSocket = null;

        try {
            //Create a new server socket
            serverSocket = new ServerSocket(portNumber);

            //Wait for a client to connect
            while (true) {
                Socket newClient = null;
                //Accept a connection from a client
                //it is a blocking line, meaning if there is no client, it will wait
                try {
                    newClient = serverSocket.accept();

                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
                //we sent the socket to the worker and start the worker
                new Worker(newClient).start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        //Create a new server on port 1234
        Server server = new Server(1234);
        //Start the server
        server.start();
    }
}
