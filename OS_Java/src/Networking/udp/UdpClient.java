package Networking.udp;

import java.io.IOException;
import java.net.*;

public class UdpClient extends Thread {

    int serverPort;
    String serverName;
    String message;
    InetAddress serverAddress;

    public UdpClient(int serverPort, String serverName, String message) {
        this.serverPort = serverPort;
        this.serverName = serverName;
        this.message = message;
        try {
            this.serverAddress = InetAddress.getByName(serverName); // bitno!!
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        DatagramSocket socket = null;
        byte[] buffer = message.getBytes();


        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
        try {
            socket = new DatagramSocket();
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Received packet from server:");
            System.out.println(new String(packet.getData(), 0, packet.getLength()));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        UdpClient client = new UdpClient(12345, "localhost", "Hello :)");
        client.start();
    }
}