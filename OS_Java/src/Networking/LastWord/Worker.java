package Networking.LastWord;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Worker extends Thread {
    private Socket socket;
    private String filePath;
    private Lock lock = new ReentrantLock();
    private static int counter = 0; // Shared counter for total messages

    public Worker(Socket socket, String filePath) {
        this.filePath = filePath;
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        PrintWriter fileWriter = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            fileWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath, true)));

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.equalsIgnoreCase("LOGIN")) {
                    writer.println("Logged in: " + socket.getInetAddress());
                    // Because it is accessed by clients, we must synchronize access to the counter
                    lock.lock();
                    writer.println("total messages " + counter);
                    lock.unlock();
                }

                if (line.equalsIgnoreCase("LOGOUT")) {
                    // Because it is accessed by clients, we must synchronize access to the counter
                    lock.lock();
                    writer.println("total messages " + counter);
                    lock.unlock();
                    writer.println("LOGGED OUT");
                } else {
                    lock.lock();
                    counter++;
                    lock.unlock();
                    writer.println(line);
                    fileWriter.println(line);
                    fileWriter.println(socket.getInetAddress());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (socket != null) socket.close();
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (fileWriter != null) fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
