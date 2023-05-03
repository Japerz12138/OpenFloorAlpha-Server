import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class newFileServer extends Thread{
    private ServerSocket serverSocket;
    private List<ClientHandler> clientHandlers;

    private int id;
    private String name;
    private byte[] data;
    private String fileExtension;

    public newFileServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientHandlers = new ArrayList<>();
    }

    public void start() {
        System.out.println("File service started.");

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

                            System.out.println("A new file \"" + fileName + "\" has been saved to " + savePath + " !");
                            //TODO: Do something shit with txt
                        } else if (getFileExtension(fileName).equalsIgnoreCase("png")) {
                            // Write file to disk
                            FileOutputStream fileOutputStream = new FileOutputStream(savePath);
                            fileOutputStream.write(fileContentBytes);
                            fileOutputStream.close();

                            System.out.println("A new file \"" + fileName + "\" has been saved to " + savePath + " !");
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
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
    }

    public static void main(String[] args) throws IOException{
        newFileServer server = new newFileServer(2334);
        server.start();
    }
}
