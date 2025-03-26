package chat.client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private PrintWriter out;

    public ChatGUI(String serverAddress, int serverPort) {
        setTitle("Chat");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        messageField = new JTextField();
        add(messageField, BorderLayout.SOUTH);

        connectToServer(serverAddress, serverPort);

        messageField.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                out.println(message);

                chatArea.append("You: " + message + "\n");

                messageField.setText("");
            }
        });
    }

    private void connectToServer(String serverAddress, int serverPort) {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        chatArea.append(serverMessage + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Couldn't connect the server", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatGUI("localhost", 12345).setVisible(true));
    }
}