package Networking.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPClient {
//    public static void main(String[] args) {
//        String name = "M";
//        String surname = "J";
//        String serverName = "localhost";
//        int serverPort = 8081;
//        try {
//            String serverUrl = "http://" + serverName + ":" + serverPort + "/?name= " + name + "&surname=" + surname;
//            URL url = new URL(serverUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            //we have the url, now we set the request method
//            connection.setRequestMethod("GET");
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String line;
//            while (!(line = in.readLine()).isEmpty()){
//                System.out.println(line);
//            }
//            in.close();
//            connection.disconnect();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 8080;
        String name = "A";
        String surname = "T";

        try {
            String url = "http://" + serverAddress + ":" + serverPort + "/?name=" + name + "&surname=" + surname;
            URL serverUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            connection.disconnect();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
