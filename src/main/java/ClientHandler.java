import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private List<ClientHandler> clients;
    private String userName;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.clientSocket = socket;
        this.clients = clients;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public void run() {
        try {

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            userName = in.readLine();
            System.out.println("[INFO] " + userName + " joined the chat");
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.sendMessage("[INFO] " + userName + " joined the chat");
                }
            }

            // send file to all clients
            for (ClientHandler handler : clients) {

            }

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(userName + ": " + message);
                for (ClientHandler client : clients) {
                    if (client != this) {
                        client.sendMessage(userName + ": " + message);
                    }
                }
            }
        } catch (SocketException e) {
            // Handle client forcefully closing the program
            System.out.println("[INFO] " + userName + " has left the chat");
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.sendMessage("[INFO] " + userName + " has left the chat");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ChatServer.removeClient(this);
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
