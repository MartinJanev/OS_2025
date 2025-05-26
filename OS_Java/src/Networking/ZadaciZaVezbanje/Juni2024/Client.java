package Networking.ZadaciZaVezbanje.Juni2024;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 7391);
        System.out.println("Connected to server.");

        BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);

        Scanner clientInput = new Scanner(System.in);

        socketWriter.println("HANDSHAKE");
        // note: Optional
        if (!socketReader.readLine().startsWith("Logged In"))
            throw new RuntimeException("Server did not response as expected.");

        System.out.println("Handshake established.");

        String word;
        while (true) {
            word = clientInput.nextLine();

            // note: Optional
            if (word.split("\\s+").length > 1) {
                throw new RuntimeException("Invalid input");
            }

            socketWriter.println(word);

            if (word.equalsIgnoreCase("STOP")) {
                int numberOfUniqueWords;
                try {
                    numberOfUniqueWords = Integer.parseInt(socketReader.readLine());
                } catch (NumberFormatException e) {
                    // note: Optional
                    throw new RuntimeException("Server did not return a number for unique words for session.");
                }
                if (!socketReader.readLine().equals("LOGGED OUT")) {
                    // note: Optional
                    throw new RuntimeException("Server did not return the string LOGGED OUT when terminating session.");
                }
                System.out.println();
                System.out.println("Number of words that server knows of: " + numberOfUniqueWords);
                if (!socketReader.readLine().equalsIgnoreCase("LOGGED OUT"))
                    throw new RuntimeException("Invalid input from server");
                break;
            } else {
                String infoAboutWord = socketReader.readLine();
                // note: Optional
                if (!(infoAboutWord.equals("IMA") || infoAboutWord.equals("NEMA"))) {
                    throw new RuntimeException("Server did not return expected response for sent word.");
                } else {
                    System.out.println(infoAboutWord);
                }
            }
        }

        socket.close();
        socketReader.close();
        socketWriter.close();

        System.out.println("Session terminated.");
    }
}
