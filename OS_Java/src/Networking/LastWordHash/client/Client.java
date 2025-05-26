package Networking.LastWordHash.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread{
    private int serverPort;
    private String serverAddress;

    public Client(int serverPort, String serverAddress) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
    }

    public void run(){
        Socket socket = null;
        BufferedReader socketReader = null;
        BufferedWriter socketWriter = null;

        try {
            socket = new Socket(InetAddress.getByName(serverAddress), serverPort);

            socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            socketWriter.write("HANDSHAKE");
            socketWriter.newLine();
            socketWriter.flush();

            String line;

            line = socketReader.readLine();
            System.out.println(line);

            if(!line.contains("Logged in")){
                System.out.println("Error: Unsuccessful login!");
                return;
            }

            socketWriter.write("operativni\n");
            socketWriter.write("OS\n");
            socketWriter.write("operativni\n");
            socketWriter.flush();

            socketWriter.write("STOP");
            socketWriter.newLine();
            socketWriter.flush();

            while(!(line=socketReader.readLine()).equals("")){
                System.out.println(line);
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (socket != null)
                    socket.close();
                if (socketReader != null)
                    socketReader.close();
                if (socketWriter != null)
                    socketWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void main(String[] args) {
        Client client = new Client(7391, "localhost");
        client.start();
    }
}

