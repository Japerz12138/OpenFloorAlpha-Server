package com.nyit.japerz;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class newChatServer extends Thread {
    private ServerSocket serverSocket;
    private List<ClientHandler> clientHandlers;

    private int id;
    private String name;
    private byte[] data;
    private String fileExtension;


    public newChatServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientHandlers = new ArrayList<>();
    }

    public void start() {

        System.out.println("Chat service started");

        while (true) {
            try {
                // Wait for a new client to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create a new ClientHandler thread to handle the client's connection
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                clientHandler.start();

            } catch (IOException e) {
                System.out.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String username;

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
                // Read the user's nickname from the client
                String line = reader.readLine();
                if (line.startsWith("join ")) {
                    username = line.substring(5);
                    System.out.println("[INFO] User " + username + "joined the chat!");
                    broadcast("[INFO] " + username + " has joined the chat!");
                    broadcast("join " + username);
                }

                // Read messages from the client and broadcast them to all other clients
                while (true) {

                    line = reader.readLine();
                    if (line == null || line.equals("quit")) {
                        broadcast("quit " + username);
                        break;
                    }
                    System.out.println(username + ": " + line);
                    broadcast("\n" + username + ": " + line);
                }

                // Handle client disconnection
                System.out.println("[INFO] User " + username + " left the chat!");
                broadcast("[INFO] " + username + " has left the chat!");
                clientHandlers.remove(this);
                clientSocket.close();

            } catch (IOException e) {
                System.out.println("Error handling client connection: " + e.getMessage());
            }
        }



        // Helper method to read all bytes from an input stream
        private byte[] readBytesFromStream(InputStream inputStream) throws IOException {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int length;
            while ((length = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, length);
            }
            buffer.flush();
            return buffer.toByteArray();
        }

        // Broadcast a message to all connected clients
        private void broadcast(String message) {
            for (ClientHandler handler : clientHandlers) {
                if (handler != this) {
                    handler.writer.println(message);
                }
            }
        }
    }

    public static String getFileExtension(String fileName) {
        //This would not work with .tar.gz
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i + 1);
        } else {
            return "No extension found!";
        }
    }

    public static void main(String[] args) throws IOException{
        newChatServer chatServer = new newChatServer(2333);
        chatServer.start();
    }
}

