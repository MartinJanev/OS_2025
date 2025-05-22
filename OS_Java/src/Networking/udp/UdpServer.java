package Networking.udp;

import Networking.as.udp.UDPServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpServer extends Thread {
    int port;
    byte[] buffer;

    public UdpServer(int port) {
        this.port = port;
        this.buffer = new byte[256];
    }

    public void run() {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(port);
            // We don't have address and port yet, because it can receive from any address and port
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true) {
                try {
                    socket.receive(packet);
                    System.out.println("RECEIVED: " + new String(packet.getData(), 0, packet.getLength()));
                    // Echo the packet back to the sender - with the same address and port of the sender
                    packet = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                    socket.send(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        UDPServer server = new UDPServer(12345);
        server.start();
    }
}
