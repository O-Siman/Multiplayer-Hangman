import org.apache.commons.io.FileUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class Server {
    private JPanel panel;
    private JLabel wordGuess;
    private JLabel hangedManImageLabel;
    private JLabel playersConnectedLabel;
    private JTextArea logTextArea;

    static int playersConnected = 0;

    public void main() throws IOException {
        Server server = new Server();
        JFrame frame = new JFrame("Server");
        frame.setContentPane(server.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        ServerWebSocket serverWebSocket = new ServerWebSocket(new InetSocketAddress(8082), server);
        serverWebSocket.start();
        System.out.println(serverWebSocket.getAddress());

        // Pick word
        List<String> words = FileUtils.readLines(new File("src/main/assets/words.txt"), StandardCharsets.UTF_8);
        Collections.shuffle(words);
        String word = words.get(0);
        System.out.println("word = " + word);
        int wordLength = word.length();
        SwingUtilities.invokeLater(() -> {
            wordGuess.setText("_ ".repeat(wordLength));
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    public JLabel getWordGuess() {
        return wordGuess;
    }

    public JLabel getHangedManImageLabel() {
        return hangedManImageLabel;
    }

    public JTextArea getLogTextArea() {
        return logTextArea;
    }

    public JLabel getPlayersConnectedLabel() {
        return playersConnectedLabel;
    }

    static class ServerWebSocket extends WebSocketServer {
        Server server;

        public ServerWebSocket(InetSocketAddress address, Server server) {
            super(address);
            this.server = server;
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            server.getLogTextArea().setText(server.getLogTextArea().getText() + conn.getRemoteSocketAddress() + " joined the game.\n");
            playersConnected++;
            server.getPlayersConnectedLabel().setText(playersConnected + " players connected.");
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            server.getLogTextArea().setText(server.getLogTextArea().getText() + conn.getRemoteSocketAddress() + " left the game.\n");
            playersConnected--;
            server.getPlayersConnectedLabel().setText(playersConnected + " players connected.");
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            System.out.println("Message from " + conn.getRemoteSocketAddress() + ": " + message);
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            System.err.print("ERROR: ");
            ex.printStackTrace();
        }

        @Override
        public void onStart() {
            System.out.println("Server started.");
        }
    }
}
