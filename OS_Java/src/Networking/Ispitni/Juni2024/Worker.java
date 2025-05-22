package Networking.Ispitni.Juni2024;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Worker extends Thread {

    Socket clientSocket;
    BufferedReader clientReader;
    PrintWriter clientWriter;

    Worker(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    private void terminateConnection() throws IOException {
        this.clientSocket.close();
        this.clientReader.close();
        this.clientWriter.close();
    }

    private void writeDataToLogFile(String word, LocalDateTime currentDateTime, String ipAddress) throws IOException {
        synchronized (Worker.class) {
            PrintWriter logFileWriter = new PrintWriter(new FileWriter(System.getenv("WORDS_FILE"), true), true);
            logFileWriter.println(word + "\t" + currentDateTime + "\t" + ipAddress);
        }
    }

    private List<String> getKnownWords() {
        synchronized (Worker.class) {
            try {
                BufferedReader logFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(System.getenv("WORDS_FILE"))));
                return logFileReader
                        .lines()
                        .map(line -> line.split("\\s+")[0])
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException("Error reading file", e);
            }
        }
    }

    @Override
    public void run() {
        try {
            if (!clientReader.readLine().equals("HANDSHAKE"))
                throw new RuntimeException("Client does not follow agreed contract for logging in.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clientWriter.println("Logged In " + clientSocket.getInetAddress());

        System.out.println("Waiting for client input.");
        while (true) {

            String wordFromClient;

            try {
                wordFromClient = clientReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Could not read line from client reader.");
            }

            List<String> knownWords = getKnownWords();

            if (wordFromClient.equalsIgnoreCase("STOP")) {

                clientWriter.println(knownWords.size());
                clientWriter.println("LOGGED OUT");

                try {
                    terminateConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }


            if (!knownWords.contains(wordFromClient)) {
                try {
                    writeDataToLogFile(wordFromClient, LocalDateTime.now(), String.valueOf(clientSocket.getInetAddress()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                clientWriter.println("NEMA");
            } else {
                clientWriter.println("IMA");
            }

        }
    }
}
