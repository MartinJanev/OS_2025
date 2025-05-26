package Networking.ZadaciZaVezbanje.file_calc;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    String ipAdress;
    int port;
    String filePath;

    public Client(String ipAdress, int port, String filePath) {
        this.ipAdress = ipAdress;
        this.port = port;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        //First we need to create a socket - first null to avoid null pointer exception
        Socket socket = null;
        //First we need to create a socketReader - first null to avoid null pointer exception
        //Then we need to create a socketWriter - first null to avoid null pointer exception
        //Then we need to create a file reader - first null to avoid null pointer exception
        BufferedReader socketReader = null;
        BufferedWriter socketWriter = null;
        BufferedReader fileReader = null;


        try {

            //1. Send hello

            //Then we need to connect to the server
            socket = new Socket(InetAddress.getByName(ipAdress), port);
            //Then we need to create a socket reader and writer
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //Then we need to create a file reader
            fileReader = new BufferedReader(new FileReader(filePath));

            socketWriter.write("Hello"); //Send a hello message to the server
            socketWriter.newLine(); //Send a new line to the server
            socketWriter.flush(); //Flush the writer to send the message - to force the message to be sent

            //It is sent to the server - now we wait for the server to respond
            //if we receive a message from the server to send files, then we send the file


            //3. Send files to server
            String line; //Read the line from the server
            //We need to check if the server is ready to receive files
            if ((line = socketReader.readLine()).equals("Send files")) {
                //We need to send the file to the server, while reading the file line by line
                while ((line = fileReader.readLine()) != null) {
                    socketWriter.write(line); //Send the line to the server
                    socketWriter.newLine(); //Send a new line to the server
                }
                socketWriter.newLine(); //Send a new line to the server - for indicating the end of the file
                socketWriter.flush();
            } else {
                System.out.println("Error occurred!");
            }


            //Send the file path to the server
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //Close the socket and the readers and writers
            if (socketReader != null) {
                try {
                    socketReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (socketWriter != null) {
                try {
                    socketWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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

    public static void main(String[] args) {
        Client client = new Client("localhost", 8081, "C:/Users/Media/Labs_OS/OS_Java/src/Networking/file_calc/data/points1.csv");
        client.start(); //Start the client
    }
}