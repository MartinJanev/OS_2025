package Networking.Kostadin.TCP.server;

import java.io.*;
import java.net.Socket;

public class Worker extends Thread {

    private Socket socket = null;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            //We print in order to see if the connection is established
            System.out.printf("Connected:%s:%d\n", socket.getInetAddress(), socket.getPort());

            //we can read all responses sent by the client
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //we sent data to the computer - to the client
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line = null; // we use this to receive all lines sent
            while (!(line = reader.readLine()).isEmpty()) { // while we have data for each message sent
                //print the message recieved
                System.out.printf("New message from %s:%d:\t%s\n", socket.getInetAddress(), socket.getPort(), line);
                //sent it back to the sender
                writer.println(line);
                writer.flush();
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (writer != null) {
                writer.close();
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
