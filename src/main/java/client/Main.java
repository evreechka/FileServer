package client;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        try (Socket socket = new Socket("localhost", 6666);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {
            while (true) {
                System.out.println("Enter action (1 - get a file, 2 - create a file, 3 - delete a file):");
                String commandNum = scanner.nextLine();
                if (commandNum.equals("exit")) {
                    output.writeUTF("EXIT");
                    System.out.println("The request was sent.");
                    return;
                }
                Path root = Path.of(System.getProperty("user.dir"), "src", "server", "data");
                Path clientRoot = Path.of(System.getProperty("user.dir"), "src", "client", "data");
                switch (commandNum) {
                    case ("1"): {
                        Scanner scanner1 = new Scanner(System.in);
                        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id): ");
                        String choice = scanner1.nextLine();
                        String name;
                        if (choice.equals("1")) {
                            System.out.println("Enter name of the file:");
                            name = root.toString() + "/" + scanner1.nextLine();
                        } else {
                            System.out.println("Enter id:");
                            name = scanner1.nextLine();
                        }
                        output.writeUTF("GET");
                        output.writeUTF(choice);
                        output.writeUTF(name);
                        System.out.println("The request was sent.");
                        while (input.available() <= 0) {
                        }
                        String answer = input.readUTF();
                        if (answer.equals("404")) {
                            System.out.println("The response says that this file is not found!");
                        } else {
                            while (input.available() <= 0) {
                            }
                            int length = input.readInt();
                            System.out.println(length);
                            byte[] buffer = new byte[length];
                            while (input.available() <= 0) {
                            }
                            input.read(buffer);
                            System.out.println(Arrays.toString(buffer));
                            System.out.println("The file was downloaded! Specify a name for it:");
                            String savedName = scanner.nextLine();
                            File saveFile = new File(clientRoot.toString() + "/" + savedName);
                            saveFile.createNewFile();
                            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                                fos.write(buffer);
                            }
                            System.out.println(Arrays.toString(buffer));
                            System.out.println("File saved on the hard drive!");
                        }
                        break;
                    }
                    case ("2"): {
                        System.out.println("Enter name of the file:");
                        String fileName = scanner.nextLine();
                        System.out.println("Enter name of the file to be saved on server:");
                        String name = scanner.nextLine();
                        if (name.equals("")) name = fileName;
                        File file = new File(clientRoot.toString() + "/" + fileName);
                        byte[] content;
                        try (FileInputStream fis = new FileInputStream(file)) {
                            content = new byte[fis.available()];
                            fis.read(content);
                        }
                        output.writeUTF("PUT");
                        output.writeUTF(root.toString() + "/" + name);
                        output.writeInt(content.length);
                        output.write(content);
                        System.out.println("The request was sent.");
                        while (input.available() <= 0) {
                        }
                        String answer = input.readUTF();
                        if (answer.equals("403")) {
                            System.out.println("The response says that creating the file was forbidden!");
                        } else {
                            while (input.available() <= 0) {
                            }
                            int id = input.readInt();
                            System.out.println("Response says that file is saved! ID = " + id);
                        }
                        break;
                    }
                    case ("3"): {
                        System.out.println("Do you want to delete the file by name or by id (1 - name, 2 - id): ");
                        String choice = scanner.nextLine();
                        String name;
                        if (choice.equals("1")) {
                            System.out.println("Enter name of the file:");
                            name = root.toString() + "/" + scanner.nextLine();
                        } else {
                            System.out.println("Enter id:");
                            name = scanner.nextLine();
                        }
                        output.writeUTF("DELETE");
                        output.writeUTF(choice);
                        output.writeUTF(name);
                        System.out.println("The request was sent.");
                        while (input.available() <= 0) {
                        }
                        String answer = input.readUTF();
                        if (answer.equals("404")) {
                            System.out.println("The response says that the file was not found!");
                        } else {
                            System.out.println("The response says that the file was successfully deleted!");
                        }
                        break;
                    }
                }
            }
        }
    }
}
