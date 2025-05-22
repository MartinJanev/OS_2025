package Networking.tcp;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Worker extends Thread {
    Socket socket;

    public Worker(Socket socket) {
        this.socket = socket;
    }


    public void run() {
        //Initialize the input and output streams
        BufferedReader reader = null;
        BufferedWriter writer = null;

        System.out.println();

        try {
            //Create a new input and output stream for the socket
            //BufferedReader is used to read text from a character-input stream
            //BufferedWriter is used to write text to a character-output stream
            //InputStreamReader is used to read bytes and decode them into characters
            //OutputStreamWriter is used to write characters to a byte stream

            //getInputStream() returns an input stream for the socket
            //getOutputStream() returns an output stream for the socket

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //Read the first line of the request
            String line;
            line = reader.readLine();
            Request request = new Request(line.split("\\s+"));

            while (!(line = reader.readLine()).isEmpty()) {
                //Split the line into key and value
                String[] parts;
                parts = line.split(":\\s+");
                //Put the key and value into the request headers
                //Meaning we have read the headers of the request and put them into the request object
                request.headers.put(parts[0], parts[1]);
            }

            // If headers is null, we set it to a predefined value
            String clientName = Optional
                    .ofNullable(request.headers.get("User"))
                    .orElse(request.headers.get("UserAgent"));

            //
            writer.write("HTTP/1.1 200 OK\r\n");
            writer.write(request.method + " " + request.uri + "\n");
            writer.write("Hello " + clientName + "\n");
            //We forcefully clear the buffer
            writer.flush();


            //Close the socket
            this.socket.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //Close the input and output streams
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    //Mockup for the HTTP request
    public class Request {
        String method;                  //GET POST PUT DELETE
        String uri;                     //index.html
        String version;                 //HTTP/1.1
        Map<String, String> headers;    //key value pairs


        public Request(String[] line) {
            method = line[0];
            uri = line[1];
            version = line[2];
            headers = new HashMap<String, String>();
        }
    }
}
