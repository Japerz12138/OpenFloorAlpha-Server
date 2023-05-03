package com.nyit.japerz;

import java.net.*;
import java.io.*;
import java.util.*;

public class newFileManageServer {

    private static final String FILES_DIR = "I:/CODE/SocketIOEX/Files";

    public static void main(String[] args) throws IOException {
        // Create a server socket listening on a specific port
        int port = 2335;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port " + port);

        while (true) {
            // Wait for a client to connect
            Socket socket = serverSocket.accept();
            System.out.println("Client connected: " + socket);

            // Get a list of the files in the directory
            File directory = new File(FILES_DIR);
            File[] files = directory.listFiles();
            List<String> fileNames = new ArrayList<>();
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }

            // Send the list of file names to the client
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(fileNames);
            System.out.println("Sent file list to client.");


            // Close the connection
            socket.close();
            System.out.println("Connection closed.");
        }
    }
}
//private static final int PORT = 2335;
//    private static final String FILES_DIR = "I:/CODE/SocketIOEX/Files/";
//
//    public static void main(String[] args) {
//        try {
//            ServerSocket serverSocket = new ServerSocket(PORT);
//            System.out.println("File server is running on port " + PORT);
//
//            while (true) {
//                Socket clientSocket = serverSocket.accept();
//                System.out.println("New connection from " + clientSocket.getInetAddress().getHostAddress());
//
//                // Send file list to client
//                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
//                List<String> fileNames = getFileNames();
//                outputStream.writeObject(fileNames);
//                outputStream.close();
//                System.out.println("Sent file list to " + clientSocket.getInetAddress().getHostAddress());
//
//
//                // Receive download requests from client
//                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
//                String fileName = (String) inputStream.readObject();
//                System.out.println("Received download request for file " + fileName + " from " + clientSocket.getInetAddress().getHostAddress());
//
//                // Send file contents to client
//                File file = new File(FILES_DIR + fileName);
//                if (file.exists() && file.isFile()) {
//                    FileInputStream fileInputStream = new FileInputStream(file);
//                    byte[] buffer = new byte[4096];
//                    int bytesRead;
//                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
//                        outputStream.write(buffer, 0, bytesRead);
//                    }
//                    System.out.println("Sent file " + fileName + " to " + clientSocket.getInetAddress().getHostAddress());
//                } else {
//                    System.out.println("File not found: " + fileName);
//                }
//
//                // Close the connection
//                clientSocket.close();
//                System.out.println("Connection closed with " + clientSocket.getInetAddress().getHostAddress());
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static List<String> getFileNames() {
//        File dir = new File(FILES_DIR);
//        String[] fileNames = dir.list();
//        return Arrays.asList(fileNames);
//    }
//}
