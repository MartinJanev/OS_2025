package Networking.Lab5.TCP;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ChatClient2 {
    public static final String LOG_FILE = "chatlog236040.txt";
    //private static String fileName = "chatScr.txt";

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(InetAddress.getByName("194.149.135.49"), 9753);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedWriter logWriter = new BufferedWriter(new FileWriter(LOG_FILE, true));
            //BufferedReader fileReader = new BufferedReader(new FileReader("chatScr.txt"));

            Scanner input = new Scanner(System.in);
            System.out.print("Внеси порака за логирање (hello:<index>) : ");
            String loginMessage = input.nextLine();// внесуваме hello:index

            //  String loginMessage = fileReader.readLine(); // читање на првата линија од фајлот

            writer.write(loginMessage); // испраќање на порака за логирање
            writer.newLine();
            writer.flush();
            // запиши во лог фајлот
            logWriter.newLine();
            logWriter.write("SENT: " + loginMessage);
            logWriter.newLine();
            logWriter.flush();

            // Прими потврда
            String response = reader.readLine();
            if (response == null) {
                System.out.println("Неуспешно логирање. Конекцијата ќе се затвори.");
                socket.close();
                return;
            }
            //ако е null, значи серверот не одговорил
            logWriter.write("RECEIVE: " + response);
            logWriter.newLine();
            logWriter.flush();


//            // Чекај внес од корисникот за втората порака
//            System.out.print("Внеси втора порака (hello:<index>): ");
//            loginMessage = input.nextLine();
//            writer.write(loginMessage); // испраќање на втора порака
//            writer.newLine();
//            writer.flush();
//            // запиши во лог фајлот
//            logWriter.newLine();
//            logWriter.write("SENT: " + loginMessage);
//            logWriter.newLine();
//            logWriter.flush();
//
//            response = reader.readLine();
//            if (response == null) {
//                System.out.println("Неуспешна потврда по втора порака. Конекцијата ќе се затвори.");
//                socket.close();
//                return;
//            }
//            //запиши во лог фајлот дека е приман одговор
//            logWriter.write("RECEIVE: " + response);
//            logWriter.newLine();
//            logWriter.flush();
            logWriter.newLine();
            logWriter.write("========== " + java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " ==========");
            logWriter.newLine();
            logWriter.flush();
            System.out.println("Успешно сте најавени на серверот! Сега можете да праќате пораки со другите колеги!!");

            // Иницијализирај нишки

            //FileReaderThread keyboardReader = new FileReaderThread(writer, logWriter, fileReader);
            KeyboardReader keyboardReader = new KeyboardReader(writer, logWriter);
            TCPReader tcpReader = new TCPReader(reader, logWriter);
            // Стартувај ги нишките
            keyboardReader.start();
            tcpReader.start();

        } catch (IOException e) {
            System.err.println("Грешка при поврзување или логирање: " + e.getMessage());
        }
    }
}

class KeyboardReader extends Thread {
    private final BufferedWriter bw;
    private final BufferedWriter logWriter;

    public KeyboardReader(BufferedWriter bw, BufferedWriter logWriter) {
        this.bw = bw;
        this.logWriter = logWriter;
    }

    @Override
    public void run() {
        Scanner input = new Scanner(System.in);
        while (true) {
            try {
                String line = input.nextLine();
                bw.write(line);
                bw.newLine();
                bw.flush();
                logWriter.newLine();
                logWriter.write("SENT: " + line);
                logWriter.newLine();
                logWriter.flush();
            } catch (IOException e) {
                System.err.println("Грешка при испраќање порака.");
                break;
            }
        }
    }
}

class TCPReader extends Thread {
    private final BufferedReader br;
    private final BufferedWriter logWriter;

    public TCPReader(BufferedReader br, BufferedWriter logWriter) {
        this.br = br;
        this.logWriter = logWriter;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String line = br.readLine();
                if (line == null) break;
                String index = line.split("\\s+")[0];
                String message = line.split(":")[1];

                logWriter.write("RECEIVE: " + line);
                logWriter.newLine();
                logWriter.flush();

                //Испечати во терминала
                System.out.println("> " + index + ":" + message);

            } catch (IOException e) {
                System.err.println("Грешка при читање порака.");
                break;
            }
        }
    }

}

class FileReaderThread extends Thread {
    private final BufferedWriter bw;
    private final BufferedWriter logWriter;
    private final BufferedReader fileReader;

    public FileReaderThread(BufferedWriter bw, BufferedWriter logWriter, BufferedReader fileReader) {
        this.bw = bw;
        this.logWriter = logWriter;
        this.fileReader = fileReader;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = fileReader.readLine()) != null) {
                Thread.sleep(3000);
                bw.write(line);
                bw.newLine();
                bw.flush();
                logWriter.newLine();
                logWriter.write("SENT: " + line);
                logWriter.newLine();
                logWriter.flush();
            }
        } catch (IOException e) {
            System.err.println("Грешка при испраќање порака.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
