package Networking.tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class Client extends Thread {
    int serverPort;

    public Client(int serverPort) {
        this.serverPort = serverPort;
    }


    public void run() {
        //Initialize the input and output streams
        BufferedReader reader = null;
        BufferedWriter writer = null;

        //Socket that we will read/write from I/O, not server socket
        Socket socket = null;
        Random random = new Random();


        //Create a new socket with parameters destination adress and port
        try {
            //Create a new socket
            socket = new Socket(InetAddress.getLocalHost(), serverPort);
            //Create a new input and output stream for the socket
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //Depending on the random number, we will send a GET or POST request
            writer.write((random.nextInt() % 2 == 0 ? "GET" : "POST") + " " + "/finki/os" + " " + "HTTP/1.1" + "\r\n");
            writer.write("User: Martin\n");
            //Important to add a new row to indicate the end of the headers
            writer.write("\n");
            writer.flush();

            String line = "/";
            while ((line = reader.readLine()) != null) {
                System.out.println("RECEIVED: " + line);
            }
            socket.close();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        //Create a new client on port 1234 - must for port number to be the same as server
        Client client = new Client(1234);
        //Start the client
        client.start();
    }
}
