package Networking.Zborovi;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// poveke workeres per konekcija da moze da startuvame -> Thread
public class Worker extends Thread {
    Socket socket;
    String logFile;
    // od site klienti (threads) site brojcinja
    static long total = 0;
    // spodelen za site -> lock
    static Lock lock = new ReentrantLock();

    public Worker(Socket socket, String logFile) {
        this.socket = socket;
        this.logFile = logFile;
    }

    @Override
    public void run() {
        BufferedReader socketReader = null;
        BufferedWriter socketWriter = null;
        BufferedWriter fileWriter = null;

        try {
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            fileWriter = new BufferedWriter(new FileWriter(logFile, true));

            String line = socketReader.readLine();
            if (line.equals("HANDSHAKE")) {
                // adresata na klientot e preku remotesocketaddress
                socketWriter.write("Logged in " + socket.getRemoteSocketAddress() + "\n");
                socketWriter.flush();

                // lock unlock vo while e sporo, zatoa
                int localSum = 0;
                // pobrzo e so Sb
                StringBuilder stringBuilder = new StringBuilder();
                while(!(line = socketReader.readLine()).equals("STOP")) {
                    int number = Integer.parseInt(line);
                    // lock logika deka e spodelena treba, no ne vo whileov
                    localSum += number;
                    // [%d] dava [broj]
                    stringBuilder.append(String.format("[%d] %s %s\n", number, LocalDateTime.now(), socket.getRemoteSocketAddress()));
                }
                fileWriter.write(stringBuilder.toString());
                fileWriter.flush();

                // pobrzo e vaka
                lock.lock();
                total += localSum;
                socketWriter.write(total + "\n");
                lock.unlock();

                socketWriter.write("Logged out \n");
                socketWriter.flush();

                fileWriter.close();
                socketWriter.close();
                socketReader.close();
                socket.close();

            } else {
                socketWriter.write("Could not connect\n");
                socketWriter.flush();
                socketWriter.close();
                socketReader.close();
                fileWriter.close();
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
