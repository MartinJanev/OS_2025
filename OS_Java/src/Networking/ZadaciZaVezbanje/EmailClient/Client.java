package Networking.ZadaciZaVezbanje.EmailClient;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

    private static final String INDEX = "236040";
    private static final String NAME = "Martin";
    private static final String SURNAME = "Janev";

    private static final String IP_ADDRESS = "194.159.135.49";
    private static final int PORT = 7391;

    public static void main(String[] args) {
        System.out.println("Client for connecting to " + IP_ADDRESS + ":" + PORT);
        System.out.println("Student: " + NAME + " " + SURNAME + " (Index: " + INDEX + ")");

        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new BufferedReader((new InputStreamReader(socket.getInputStream())));
            out = new PrintWriter(socket.getOutputStream(), true);

            String welcomeMessage = "hello:" + INDEX;
            System.out.println("Sending message: " + welcomeMessage);
            out.println(welcomeMessage);

            String response = in.readLine();
            String expectedResponse = "hello:" + INDEX;
            if (response != null && response.equals(expectedResponse)) {
                System.out.println("Hello message successfully received by server.");
            } else {
                System.err.println("Server did not respond with expected hello message. Closing connection.");
                closeResources(socket, in, out);
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            System.err.println("Terminating connection.");
            closeResources(socket, in, out);
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("The following commands are for human interaction:");
        System.out.println("\n--- Commands ---");
        System.out.println("1. Send a file (e.g., 'send <filename>')");
        System.out.println("2. Send file size (e.g., 'size <filesize>')");

        while (true) {
            System.out.println("Enter command");
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }

            if (input.toLowerCase().startsWith("send ")) {
                String[] parts = input.split(" ", 2);
                if (parts.length == 2) {
                    try {
                        String filename = parts[1];
                        filename = "src/" + filename;
                        sendFile(filename, out, in);
                        String response = in.readLine();
                        if (response != null && response.equals("ok")) {
                            System.out.println("File sent successfully.");
                        } else {
                            System.err.println("Server did not acknowledge file. Closing connection.");
                            break;
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Invalid command. Use 'send <filename>'");
                }
            } else if (input.toLowerCase().startsWith("size ")) {
                String[] parts = input.split(" ", 2);
                if (parts.length == 2) {
                    try {
                        int size = Integer.parseInt(parts[1]);
                        String messageToServer = INDEX + ":size:" + size;
                        System.out.println("Sending message: " + messageToServer);
                        out.println(messageToServer);
                        String response = in.readLine();
                        if (response != null && response.equals(messageToServer + "ok")) {
                            System.out.println("Server acknowledged size: " + size);
                        } else {
                            System.err.println("Server did not acknowledge size. Closing connection.");
                            break;
                        }

                    } catch (IOException e) {
                        System.err.println("Error sending size: " + e.getMessage());
                        System.err.println("Terminating connection.");
                        break;
                    }
                } else {
                    System.out.println("Invalid command. Use 'size <filesize>'");
                }

            } else {
                System.out.println("Invalid command. Use 'send <filename>' or 'size <filesize>'");
            }
        }
        closeResources(socket, in, out);
        scanner.close();
    }

    public static void closeResources(Socket socket, BufferedReader in, PrintWriter out) {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    public static void sendFile(String filename, PrintWriter out, BufferedReader in) {
        if (!filename.endsWith(".txt")) {
            System.out.println("Invalid file type. Only .txt files are allowed.");
            return;
        }

        BufferedReader fileReader = null;

        try {
            fileReader = new BufferedReader(new FileReader(filename));
            filename = filename.split("/")[1];
            String message = INDEX + ":attach:" + filename;

            System.out.println("Sending message: " + message);
            out.println(message);

            System.out.println("Sending file content of: " + filename);
            String line;
            while ((line = fileReader.readLine()) != null) {
                out.println(line);
            }

            String overMessage = INDEX+":over";
            System.out.println("Sending message: " + overMessage);
            out.println(overMessage);

            String response = in.readLine();
            if (response != null && response.equals("ok")) {
                System.out.println("File content sent successfully.");
            } else {
                System.err.println("Server did not acknowledge file content. Closing connection.");
            }
        } catch (IOException e) {
            System.err.println("Error sending file: " + e.getMessage());
            System.err.println("Terminating connection.");
            closeResources(null, in, out);
            System.exit(1);
        }

    }
}