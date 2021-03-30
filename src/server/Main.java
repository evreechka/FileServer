package server;

import javax.sound.sampled.Port;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Path root = Path.of(System.getProperty("user.dir"), "src", "server", "data");
//        Path root = Path.of(System.getProperty("user.dir"), "File Server", "task", "src", "server", "data");
        File mapFile = new File(root.toString() + "/map.txt");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(mapFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Map<Integer, String> map = null;
        try {
            if (fis.available() > 0) {
                Map<Integer, String> oldMap = (HashMap<Integer, String>) new ObjectInputStream(fis).readObject();
                map = new HashMap<>(oldMap);
            } else {
                map = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        FileManager fileManager = new FileManager(map);
//        System.out.println("Server started!");
        ServerSocket serverSocket = new ServerSocket(6666);
        while (true) {
            try (
                    Socket socket = serverSocket.accept();
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                while (input.available() <= 0) {
                }
                String command = input.readUTF();
                if (command.equals("EXIT")) {
                    new ObjectOutputStream(new FileOutputStream(mapFile)).writeObject(fileManager.getMap());
                    serverSocket.close();
//                    socket.close();
                    System.exit(0);
                }
                if (command.equals("PUT")) {
                    while (input.available() <= 0) {
                    }
                    String fileName = input.readUTF();
                    while (input.available() <= 0) {
                    }
                    int size = input.readInt();
                    byte[] content = new byte[size];
                    input.read(content);
                    fileManager.put(fileName, content, output);
                }
                if (command.equals("GET")) {
                    while (input.available() <= 0) {
                    }
                    String choice = input.readUTF();
                    while (input.available() <= 0) {
                    }
                    String name = input.readUTF();
                    fileManager.get(choice, name, output);
                }
                if (command.equals("DELETE")) {
                    while (input.available() <= 0) {
                    }
                    String choice = input.readUTF();
                    while (input.available() <= 0) {
                    }
                    String name = input.readUTF();
                    fileManager.delete(choice, name, output);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}