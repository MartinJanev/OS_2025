package Networking.Lab5.UDP;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class UdpClient {
    public static final String LOG_FILE = "chatlogUDP236040.txt";
    public static final int SERVER_PORT = 9753;
    public static final String SERVER_IP = "194.149.135.49";

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(); // наместо Socket - DatagramSocket
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP); // IP адреса на серверот
            BufferedWriter logWriter = new BufferedWriter(new FileWriter(LOG_FILE, true));

            Scanner input = new Scanner(System.in);

            // Send login message
            System.out.print("Внеси порака за логирање (hello:<index>) : ");
            String loginMessage = input.nextLine();

            //Ф-ии за испраќање и зачувување на порака во лог фајлот
            sendMessage(socket, serverAddress, loginMessage);
            log(logWriter, "SENT: " + loginMessage);

            String response = receiveMessage(socket);
            if (response == null) {
                System.out.println("Неуспешно логирање.");
                socket.close();
                return;
            }
            log(logWriter, "RECEIVE: " + response);

//            // Second login message
//            System.out.print("Внеси втора порака (hello:<index>): ");
//            loginMessage = input.nextLine();
//            sendMessage(socket, serverAddress, loginMessage);
//            log(logWriter, "SENT: " + loginMessage);
//
//            response = receiveMessage(socket);
//            if (response == null) {
//                System.out.println("Неуспешна потврда по втора порака.");
//                socket.close();
//                return;
//            }
//            log(logWriter, "RECEIVE: " + response);

            System.out.println("Успешно сте најавени на серверот! Сега можете да праќате пораки со другите колеги!!");

            // Start threads
            new UDPKeyboardSender(socket, serverAddress, logWriter).start();
            new UDPReceiver(socket, logWriter).start();

        } catch (IOException e) {
            System.err.println("Грешка при поврзување: " + e.getMessage());
        }
    }

    // Ф-ии за испраќање и примање пораки
    // Како аргументи се праќаат сокетот, адресата на серверот и пораката
    private static void sendMessage(DatagramSocket socket, InetAddress address, String message) throws IOException {
        // Претворање на пораката во бајтови
        byte[] buffer = message.getBytes();
        // Создавање на пакетот за испраќање
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
        // Испраќање на пакетот
        socket.send(packet);
    }

    private static String receiveMessage(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[1024];
        // Создавање на пакетот за примање
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        // Поставување на времето за чекање
        socket.setSoTimeout(5000); // timeout after 5 seconds
        try {
            socket.receive(packet);
            // Претворање на примената порака во String
            // packet.getLength() дава колку бајти се примени
            // packet.getData() дава референца на бајт низа
            return new String(packet.getData(), 0, packet.getLength());
        } catch (SocketTimeoutException e) {
            return null;
        }
    }

    // Ф-ии за логирање
    // Како аргументи се праќаат лог фајлот и пораката
    // Запишува во лог фајлот
    private static void log(BufferedWriter logWriter, String text) throws IOException {
        logWriter.write(text);
        logWriter.newLine();
        logWriter.flush();
    }
}

class UDPKeyboardSender extends Thread {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final BufferedWriter logWriter;

    public UDPKeyboardSender(DatagramSocket socket, InetAddress address, BufferedWriter logWriter) {
        this.socket = socket;
        this.address = address;
        this.logWriter = logWriter;
    }

    @Override
    public void run() {
        Scanner input = new Scanner(System.in);
        while (true) {
            try {
                // Читање на пораката од тастатура
                String line = input.nextLine();
                byte[] buffer = line.getBytes();
                // Создавање на пакетот за испраќање
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 9753);
                socket.send(packet);
                // Запишување во лог фајлот
                logWriter.write("SENT: " + line);
                logWriter.newLine();
                logWriter.flush();
            } catch (IOException e) {
                System.err.println("Грешка при испраќање порака.");
                break;
            }
        }
    }
}

class UDPReceiver extends Thread {
    private final DatagramSocket socket;
    private final BufferedWriter logWriter;

    public UDPReceiver(DatagramSocket socket, BufferedWriter logWriter) {
        this.socket = socket;
        this.logWriter = logWriter;
    }

    @Override
    public void run() {
        // Initial buffer for receiving messages
        byte[] buffer = new byte[1024];
        while (true) {
            try {
                // Instantiate a DatagramPacket for receiving messages
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                // Receive the packet
                socket.receive(packet);
                // Convert the received data to a string
                String line = new String(packet.getData(), 0, packet.getLength());

                logWriter.write("RECEIVE: " + line);
                logWriter.newLine();
                logWriter.flush();

                // Print the message to the console
                // Split the message into parts
                // The first part is the index, the second part is the message
                // If the message is not in the expected format, print it as is
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    System.out.println("> " + parts[0] + ":" + parts[1]);
                } else {
                    System.out.println("> " + line);
                }
            } catch (IOException e) {
                System.err.println("Грешка при читање порака.");
                break;
            }
        }
    }
}
