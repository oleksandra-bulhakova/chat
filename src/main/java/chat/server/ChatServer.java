package chat.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static final String CLIENTS_FILE = "clients.txt";
    private static final Set<ClientHandler> clients = new HashSet<>();
    private static final Set<String> clientNames = new HashSet<>();

    public static void main(String[] args) {
        loadClientsFromFile();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is working on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static synchronized void addClient(String name) {
        clientNames.add(name);
        saveClientsToFile();
    }

    public static synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
        clientNames.remove(client.getClientName());
        saveClientsToFile();
    }

    private static void loadClientsFromFile() {
        File file = new File(CLIENTS_FILE);

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Created new client file: " + CLIENTS_FILE);
                }
            } catch (IOException ex) {
                System.out.println("Failed to create client file.");
                ex.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CLIENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                clientNames.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading client file.");
            e.printStackTrace();
        }
    }

    private static void saveClientsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CLIENTS_FILE))) {
            for (String name : clientNames) {
                writer.println(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
