package Networking.Kostadin.UDP.client;

import Networking.as.udp.UDPClient;

import java.io.IOException;
import java.net.*;

public class Client extends Thread {

    private String serverName;
    private int serverPort;

    private DatagramSocket socket;
    private InetAddress inetAddress;
    private String message;
    private byte[] buffer;

    public Client(String serverName, int serverPort, String message) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.message = message;

        try {
            this.socket = new DatagramSocket();
            this.inetAddress = InetAddress.getByName(serverName);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.inetAddress, this.serverPort);
        try {
            socket.send(packet);
            packet = new DatagramPacket(buffer, buffer.length, inetAddress, serverPort);
            socket.receive(packet);
            System.out.println(new String(packet.getData(), 0, packet.getLength()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        UDPClient client = new UDPClient("localhost", 9000, "Hello from UDP client");
        client.start();
    }
}
