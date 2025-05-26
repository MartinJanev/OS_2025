package Networking.LastWordHash.server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Worker extends Thread {
    Socket socket;
    String logFilePath;
    static HashMap<String, Integer> wordsHashMap = new HashMap<>();
    static Lock lock = new ReentrantLock();
    static int totalWords = 0;

    public Worker(Socket socket, String logFilePath) {
        this.socket = socket;
        this.logFilePath = logFilePath;
    }

    public void run() {
        BufferedReader socketReader = null;
        BufferedWriter socketWriter = null;
        BufferedWriter fileWriter = null;

        try {
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            fileWriter = new BufferedWriter(new FileWriter(logFilePath, true));

            String line;

            if(!(line = socketReader.readLine()).equals("HANDSHAKE")){
                System.out.println("Error: Unsuccessful HANDSHAKE!");
                return;
            }

            socketWriter.write("Logged in "+ socket.getRemoteSocketAddress());
            socketWriter.newLine();
            socketWriter.flush();

            String word;
            while(!(word = socketReader.readLine()).equals("STOP")){
                lock.lock();
                if(wordsHashMap.containsKey(word)){
                    wordsHashMap.put(word, wordsHashMap.get(word) + 1);
                    lock.unlock();

                    socketWriter.write(word + " IMA\n");
                    socketWriter.flush();
                }
                else{
                    totalWords++;
                    wordsHashMap.put(word, 1);
                    lock.unlock();

                    socketWriter.write(word + " NEMA\n");
                    socketWriter.flush();

                    fileWriter.write(String.format("%s %s %s\n", word, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), socket.getRemoteSocketAddress()));
                    fileWriter.flush();
                }
            }

            if (word.equals("STOP")) {
                socketWriter.write(String.format("Total Words: %d\n",totalWords));
                socketWriter.write("LOGGED OUT\n");
                socketWriter.newLine();
                socketWriter.flush();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(socket != null)
                    socket.close();
                if(socketReader != null)
                    socketReader.close();
                if(socketWriter != null)
                    socketWriter.close();
                if(fileWriter != null)
                    fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
