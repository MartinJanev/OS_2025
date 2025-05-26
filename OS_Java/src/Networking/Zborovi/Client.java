package Networking.Zborovi;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

// moze da ima poveke clients
public class Client extends Thread {
    String address;
    int port;
    String filePath;

    public Client(String address, int port, String filePath) {
        this.address = address;
        this.port = port;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            //searches by name of the host to find the ip address for that host
            Socket socket = new Socket(InetAddress.getByName(address), port);

            // socket manager with buffered reader and writer
            BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // file reader
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath));

            socketWriter.write("HANDSHAKE\n");
            socketWriter.flush();

            String line = socketReader.readLine();
            if (line.contains("Logged in")) {
                String numberLine;
                // EOF == null, ako imase empty line ko kraj mozese so isEmpty()
                while ((numberLine = fileReader.readLine()) != null) {
                    socketWriter.write(numberLine + "\n");
                }
                socketWriter.write("STOP\n");
                socketWriter.flush();

                line = socketReader.readLine();
                System.out.println("Total is: " + line);
                //LOGGED OUT message
                line = socketReader.readLine();
                System.out.println(line);

                fileReader.close();
                socketReader.close();
                socketWriter.close();
                socket.close();
            }

            else {
                System.out.println(line);
                socketReader.close();
                socketWriter.close();
                fileReader.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // treba da se export vo samiot folder kaj sto e client i od tamu da se runne
        // (ili export vo root ali fileot da e so path do clientot)
        String numbersFilePath = System.getenv("NUMBERS_FILE_PATH");
        if (numbersFilePath == null) {
            throw new RuntimeException("NUMBERS_FILE_PATH environment variable not set");
        }

        Client client = new Client("localhost", 7391, numbersFilePath);
        client.start();
    }
}
