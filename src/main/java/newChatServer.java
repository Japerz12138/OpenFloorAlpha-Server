import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class newChatServer {
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

        System.out.println("Server started");

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
//    Original Function, Leave it here just for now
//    @Override
//    public void run() {
//        try {
//            // Read the user's nickname from the client
//            String line = reader.readLine();
//            if (line.startsWith("join ")) {
//                username = line.substring(5);
//                System.out.println("[INFO] User " + username + " joined the chat!");
//                broadcast("\n[INFO] " + username + " has joined the chat!");
//                broadcast("join " + username);
//            }
//
//            InputStream inputStream = clientSocket.getInputStream();
//            while (true) {
//                line = reader.readLine();
//                if (line == null || line.equals("quit")) {
//                    broadcast("quit " + username);
//                    break;
//                }
//                System.out.println(username + ": " + line);
//                broadcast("\n" + username + ": " + line);
//
//                // The client has sent a file
//                if (line.startsWith("file ")) {
//                    String fileName = line.substring(5);
//                    byte[] fileContentBytes = readBytesFromStream(inputStream);
//                    String savePath = "I:/CODE/DATA/" + fileName;
//                    // Write file to disk
//                    FileOutputStream fileOutputStream = new FileOutputStream(savePath);
//                    fileOutputStream.write(fileContentBytes);
//                    fileOutputStream.close();
//
//                    broadcast(username + " just sent a file: " + fileName);
//                    broadcast("file " + fileName);
//
//                    if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
//                        //TODO: Do something with txt
//                    }
//                    // Send file to all clients
//                    for (ClientHandler handler : clientHandlers) {
//                        if (handler != this) {
//                            handler.writer.println("file " + fileName + "\n");
//                            handler.clientSocket.getOutputStream().write(fileContentBytes);
//                        }
//                    }
//                }
//            }
    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

            int fileNameLength = dataInputStream.readInt();

            if (fileNameLength > 0) {
                byte[] fileNameBytes = new byte[fileNameLength];
                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                String fileName = new String(fileNameBytes);

                int fileContentLength = dataInputStream.readInt();

                if (fileContentLength > 0) {
                    byte[] fileContentBytes = new byte[fileContentLength];
                    dataInputStream.readFully(fileContentBytes, 0, fileContentLength);

                    String savePath = "I:/CODE/DATA/" + fileName;

                    if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                        String txtContent = new String(fileContentBytes);
                        // Write file to disk
                        FileOutputStream fileOutputStream = new FileOutputStream(savePath);
                        fileOutputStream.write(fileContentBytes);
                        fileOutputStream.close();
                        broadcast(username + " just send a file: " +fileName);
                        broadcast(txtContent);
                        System.out.println("A new file \"" + fileName + "\" has been saved to " + savePath + " !");
                        //TODO: Do something shit with txt
                    } else if (getFileExtension(fileName).equalsIgnoreCase("png")) {
                        // Write file to disk
                        FileOutputStream fileOutputStream = new FileOutputStream(savePath);
                        fileOutputStream.write(fileContentBytes);
                        fileOutputStream.close();
                        broadcast(username + " just send a file: " +fileName);
                        System.out.println("A new file \"" + fileName + "\" has been saved to " + savePath + " !");
                    }

    //                // send file to all clients
    //                for (ClientHandler handler : clientHandlers) {
    //                    if (handler != this) {
    //                        handler.writer.println("file " + fileName);
    //                        handler.clientSocket.getOutputStream().write(fileContentBytes); //TODO: I can't just return this damn metadata, do something with it!
    //                    }
    //                }
                }
            }

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
            return fileName.substring(i+1);
        } else {
            return "No extension found!";
        }
    }

    public static void main(String[] args) throws IOException {
        newChatServer server = new newChatServer(2333);
        server.start();

    }

    public void Myfile (int id, String name, byte[] data, String fileExtension) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.fileExtension = fileExtension;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public int getId() {
        return id;
    }
}
