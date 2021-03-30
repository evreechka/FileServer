package server;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class FileManager {
    transient Map<Integer, String> map;
    Path root = Path.of(System.getProperty("user.dir"), "File Server", "task", "src", "server", "data");
//    Path root = Path.of(System.getProperty("user.dir"), "src", "server", "data");
    public FileManager(Map<Integer, String> map) {
        this.map = map;
    }
//    public void start() {
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//            String[] action = scanner.nextLine().split(" ");
//            switch (action[0]) {
//                case "add":
//                    add(action[1]);
//                    break;
//                case "get":
//                    get(action[1]);
//                    break;
//                case "delete":
//                    delete(action[1]);
//                    break;
//                case "exit":
//                    return;
//                default:
//                    System.out.println("Wrong command, try again");
//                    break;
//            }
//        }
//    }
    public synchronized void put(String fileName, byte[] content, DataOutputStream output) throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
//            output.writeUTF(fileName);
            output.writeUTF("403");
            return;
        }
        int id = 0;
        if (!map.isEmpty()) {
            id = getMaxId(map.keySet()) + 1;
        }
        file.createNewFile();
        try(FileOutputStream fw = new FileOutputStream(file)) {
            fw.write(content);
            map.put(id, fileName);
//            output.writeUTF(fileName);
            output.writeUTF("200");
            output.writeInt(id);
        }

    }
    private int getMaxId(Set<Integer> idSet) {
        int max = -1;
        for (Integer id: idSet) {
            if (id > max) max = id;
        }
        return max;
    }
    public void get (String choice, String name, DataOutputStream output) throws IOException {
        File file;
        if (choice.equals("1")) {
            file = new File(name);
        } else {
            int id = Integer.parseInt(name);
            if (map.keySet().contains(id)) {
                file = new File(map.get(id));
            } else {
                output.writeUTF("404");
                return;
            }
        }
        if (!file.exists()) {
            output.writeUTF("404");
            return;
        }
        byte[] buffer;
        try(FileInputStream fis = new FileInputStream(file)) {
            buffer = new byte[fis.available()];
            fis.read(buffer);
        }
        output.writeUTF("200");
        output.writeInt(buffer.length);
        output.write(buffer);
    }
    public void delete (String choice, String name, DataOutputStream output) throws IOException {
        File file;
        int id = 0;
        if (choice.equals("1")) {
            file = new File(name);
            for (Map.Entry<Integer, String> entry : map.entrySet()) {
                if (entry.getValue().equals(name)) {
                    id = entry.getKey();
                }
            }
        } else {
            id = Integer.parseInt(name);
            if (map.keySet().contains(id)) {
                file = new File(map.get(id));
            } else {
                output.writeUTF("404");
                return;
            }
        }
        if (!file.exists()) {
            output.writeUTF("404");
            return;
        }
        if (file.delete()) {
            map.remove(id);
            output.writeUTF("200");
        } else {
            output.writeUTF("404");
        }
    }

    public Map<Integer, String> getMap() {
        return map;
    }
}
