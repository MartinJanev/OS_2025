package Networking.ZadaciZaVezbanje.file_calc;


import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Worker extends Thread {
    Socket socket;
    String totalLinesFilepath;
    String aggregatedFilepath;

    Lock lock;

    public Worker(Socket socket, String totalLinesFilepath, String aggregatedFilepath) {
        this.socket = socket;
        this.totalLinesFilepath = totalLinesFilepath;
        this.aggregatedFilepath = aggregatedFilepath;
        this.lock = new ReentrantLock();
    }

    @Override
    public void run() {
        //We use it to
        //read the lines from the socket
        BufferedReader socketReader = null;
        //Then we need to create a socketWriter
        //We use it to write the lines to the socket
        BufferedWriter socketWriter = null;

        //Then we need to create a file reader for the total lines file and aggregated file
        BufferedWriter aggregatedFileWrite = null;
        //We use RandomAccessFile to read and write the total lines file
        RandomAccessFile raf = null;

        try {
            //Then we need to create a socket reader and writer
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //Then we need to create a file reader for the aggregated file
            aggregatedFileWrite = new BufferedWriter(new FileWriter(aggregatedFilepath, true));

            //Then we need to create a file reader for the total lines file
            //rw to read and write
            //It is important!!!!! for --new-- File because without it does not show error
            //it is wrong however
            raf = new RandomAccessFile(new File(totalLinesFilepath), "rw");

            String line;
            if (!(line = socketReader.readLine()).equals("Hello")) {
                socketWriter.write("Error occurred!");
                socketWriter.newLine();
                socketWriter.flush();

                //Close the socket and the reader,writer and file readers

                socket.close();
                socketReader.close();
                socketWriter.close();
                aggregatedFileWrite.close();
                raf.close();

                return;
            }
            //2. Successfully connected to the server - now we need to send a message to the client for step 3
            socketWriter.write("Send files");
            socketWriter.newLine();
            socketWriter.flush();

            //4. Receive files from client and process them

            //Read the first line from the file, bit ignore 1 row for actual data later
            //prviot red e problem, treba da se ignorira
            socketReader.readLine();
            //Next we need to read the file line by line
            int totalLinesFromFile = 0;
            int totalPointsFromFile = 0;
            while (!(line = socketReader.readLine()).isEmpty()) {
                //201006,David,Georgiev,88
                String[] lineParts = line.split(",");
                totalPointsFromFile += Integer.parseInt(lineParts[3]);
                totalLinesFromFile++;
            }

            //totalPoints - poeni za daden fajl
            aggregatedFileWrite.write("Total points: " + totalPointsFromFile);
            aggregatedFileWrite.newLine();
            aggregatedFileWrite.flush();

            //totalLinesFromFile vo binaryFile --> poeni za kolku studenti

//            int totalPoints = 0;
            int totalLines = 0;

            lock.lock();

            //1. Read the integer from the total lines file
            //2. Retuen the file pointer to the beginning of the file, so that
            //we can write the new value
            //3. Write the new value to the file

            totalLines = raf.readInt();
            raf.seek(0);
            raf.writeInt(totalLines + totalLinesFromFile);

            lock.unlock();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}