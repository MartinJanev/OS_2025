package Networking.Kostadin.TCP1.server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HTTPWorker extends Thread {
    private Socket socket;

    public HTTPWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //Data from browser
        BufferedReader reader = null;
        //For sending data to the browser
        PrintWriter writer = null;

        try {
            //We print in order to see if the connection is established
            System.out.printf("Connected:%s:%d\n", socket.getInetAddress(), socket.getPort());

            //we can read all responses sent by the client
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //we sent data to the computer - to the client
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line = null; // we use this to receive all lines sent

            StringBuilder builder = new StringBuilder();

            while (!(line = reader.readLine()).isEmpty()) { // while we have data for each message sent
                builder.append(line).append("\n");
                System.out.println(line);
            }

            RequestProcessor request = RequestProcessor.of(builder.toString());
            writer.write("HTTP/1.1 200 OK\n\n");
            if (request.getCommand().equals("GET") && request.getUri().equals("/time")) {
                writer.printf("<html><body><h1>%s</h1></body></html>", LocalDateTime.now().format(DateTimeFormatter.ISO_TIME));
            }else {
                writer.printf("<html><body><h1>HELLO WORLD</h1></body></html>");
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (writer != null) {
                writer.close();
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
