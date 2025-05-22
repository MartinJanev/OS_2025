package Networking.Kostadin.UDP.server;


import Networking.as.udp.UDPServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server extends Thread {

    private DatagramSocket socket;
    private byte[] buffer = new byte[1024];

    public Server(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    @Override
    public void run() {
        // Instantiate a new DatagramSocket object
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            try {
                // Receive a packet from the socket
                socket.receive(packet);

                // Print the received packet
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("RECEIVED: " + received);

                // Send the packet back to the sender
                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                packet = new DatagramPacket(buffer, buffer.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        UDPServer server = new UDPServer(9000);
        server.start();
    }
}
