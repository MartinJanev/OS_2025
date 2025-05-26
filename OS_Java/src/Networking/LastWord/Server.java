package Networking.LastWord;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private int port;
    private String logfile;

    public Server(int port, String logfile) {
        this.port = port;
        this.logfile = logfile;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                new Worker(socket, logfile).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void main(String[] args) {
        // Ja citame LOG_FILE env variabla preku System.getenv
        String logfile = System.getenv("LOG_FILE");
        int port = 7301;
        // Ako nema LOG_FILE env variabla, znaci RuntimeException
        if (logfile == null || logfile.isEmpty()) {
            throw new RuntimeException("Please add env variablle with file name and adress");
        }
        Server server = new Server(port, logfile);
        server.start();
    }
}
