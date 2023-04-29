import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static List<ClientHandler> clients = new ArrayList<>();
    private static int port = 2333;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[INFO] Chat Server is listening on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("[INFO] New client connected");

            ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
            clients.add(clientHandler);
            clientHandler.start();
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println(clientHandler.getUserName() + " left the chat");
        for (ClientHandler client : clients) {
            client.sendMessage(clientHandler.getUserName() + " left the chat");
        }
    }
}
