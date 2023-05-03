package com.nyit.japerz;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class newFileDownloadServer {
    private static final int PORT = 2336;
    private static final String FILES_DIR = "I:/CODE/SocketIOEX/Files/";

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("File server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from " + clientSocket.getInetAddress().getHostAddress());

                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

                int fileNameLength = dataInputStream.readInt();
                System.out.println("OK");
                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);

                    System.out.println("Received download request for file " + fileName + " from " + clientSocket.getInetAddress().getHostAddress());

                    // Send file contents to client
                    File file = new File(FILES_DIR + fileName);
                    if (file.exists() && file.isFile()) {
                        byte[] fileBytes = Files.readAllBytes(file.toPath());
                        OutputStream outputStream = clientSocket.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeInt(fileBytes.length);
                        dataOutputStream.write(fileBytes);
                        System.out.println("Sent file " + fileName + " to " + clientSocket.getInetAddress().getHostAddress());
                    } else {
                        System.out.println("File not found: " + fileName);
                    }
                } else {
                    System.out.println("Nothing");
                }

                // Close the connection
                clientSocket.close();
                System.out.println("Connection closed with " + clientSocket.getInetAddress().getHostAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getFileNames() {
        File dir = new File(FILES_DIR);
        String[] fileNames = dir.list();
        return Arrays.asList(fileNames);
    }
}
