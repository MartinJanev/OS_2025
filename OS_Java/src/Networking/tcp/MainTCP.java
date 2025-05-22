package Networking.tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class MainTCP {

    public static void main(String[] args) throws InterruptedException, IOException {
        Server server = new Server(8000);
        server.start();

        Thread.sleep(1000);

        for (int i = 0; i < 10; ++i) {
            Client client = new Client(8000);
            client.start();
        }

        Thread.sleep(1000);

        System.out.println("\nGET/POST a new movie from/to the server:");
        System.out.println("========================================\n");

        Scanner scanner = new Scanner(System.in);
        String action, resource, user;

        System.out.print("Enter action: ");
        action = scanner.nextLine();

        System.out.print("Enter movie: ");
        resource = scanner.nextLine();

        System.out.print("What is your name: ");
        user = scanner.nextLine();

        Socket socket = new Socket(InetAddress.getLocalHost(), 8000);

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        bw.write(action + " " + resource + "\n");
        bw.write("User: " + user + "\n");
        bw.write("\n");
        bw.flush();

        Thread.sleep(100);

        String line = null;
        while (!(line = br.readLine()).equals(""))
            System.out.println("Simulated request: " + line);

        socket.close();
        System.out.println("\n========================================\n");
        System.out.println("We are done! :)");
    }
}
