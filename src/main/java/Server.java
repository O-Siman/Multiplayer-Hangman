import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Server {
    private JPanel panel;
    private JLabel wordGuess;
    private JLabel hangedManImageLabel;
    private JLabel playersConnectedLabel;
    private JTextArea logTextArea;
    private JLabel joinCodeLabel;

    private JFrame frame;

    static int playersConnected = 0;
    static String word;
    // [['w', 'o', 'r', 'd']
    //  ['_', '_', '_', '_']]
    static char[][] wordAndSolving;
    static boolean gameRunning = false;
    static int wrongGuesses = 0;
    static int correctGuesses = 0;

    public void main() {
        frame = new JFrame("Server");
        frame.setContentPane(this.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DefaultCaret caret = (DefaultCaret) logTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        int port;
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            port = serverSocket.getLocalPort();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        ServerWebSocket serverWebSocket = new ServerWebSocket(new InetSocketAddress(port), this);
        serverWebSocket.start();
        joinCodeLabel.setText("Join code: " + port);

        generateRandomWord(8);

        resetGame();

        frame.pack();
        frame.setVisible(true);
    }

    private void generateRandomWord(int wordLength) {
        // Pick word
        List<String> words = null;
        try {
            words = FileUtils.readLines(new File("src/main/assets/words.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        do {
            Collections.shuffle(words);
            word = words.get(0).toLowerCase();
        } while (word.length() != wordLength); // Words only == specified characters
        System.out.println("word = " + word);
    }

    private void resetGame() {
        // Set 2D array with blanks
        resetWordAndSolving();
        // Set our title to the blanks
        updateWordGuessUI(frame);
        correctGuesses = 0;
        wrongGuesses = 0;
        setHangedManImage(0);
        gameRunning = false;
    }

    private void setHangedManImage(int phase) {
        // Set image
        BufferedImage image;
        try {
            image = Scalr.resize(ImageIO.read(new File("assets/Phase " + phase + ".png")), 200);
            ImageIcon imageIcon = new ImageIcon(image);
            hangedManImageLabel.setIcon(imageIcon);
            hangedManImageLabel.setSize(100, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetWordAndSolving() {
        wordAndSolving = new char[2][word.length()]; // 2 slots, n letters
        for (int i = 0; i < word.length(); i++) {
            wordAndSolving[0][i] = word.charAt(i);
            wordAndSolving[1][i] = '_';
        }
    }

    /**
     * Put all the blanks together from <code>wordAndSolving[1]</code> and set that to the title.
     */
    private void updateWordGuessUI(JFrame frame) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            stringBuilder
                    .append(wordAndSolving[1][i])
                    .append(' ');
        }
        wordGuess.setText(stringBuilder.toString());
//        frame.pack();
    }
    static class ServerWebSocket extends WebSocketServer {
        Server server;

        public ServerWebSocket(InetSocketAddress address, Server server) {
            super(address);
            this.server = server;
        }

        private void updatePlayersConnected() {
            server.playersConnectedLabel.setText(playersConnected + (playersConnected == 1 ? " player connected." : " players connected."));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            server.logTextArea.setText(server.logTextArea.getText() + conn.getRemoteSocketAddress().getHostName() + " joined the game.\n");
            playersConnected++;
            updatePlayersConnected();
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            server.logTextArea.setText(server.logTextArea.getText() + conn.getRemoteSocketAddress().getHostName() + " left the game.\n");
            playersConnected--;
            updatePlayersConnected();
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            System.out.println("Message from " + conn.getRemoteSocketAddress() + ": " + message);
            // Message examples:
            // cmd start 4dm1n
            // cmd custom 4dm1n turtle
            // gss E
            String[] messageComponents = message.split(" ");
            String action = messageComponents[0];
            switch (action) {
                case "cmd" -> {
                    String command = messageComponents[1];
                    switch (command) {
                        case "help" -> conn.send("Commands: help, start, pause, reset, gen_new, custom, bc");
                        case "start" -> {
                            if (messageComponents[2].equals("4dm1n")) {
                                gameRunning = true;
                                conn.send("Started the game.");
                            } else {
                                conn.send("Wrong password lol");
                            }
                        }
                        case "pause" -> {
                            if (messageComponents[2].equals("4dm1n")) {
                                gameRunning = false;
                                conn.send("Paused the game.");
                            } else {
                                conn.send("Wrong password lol");
                            }
                        }
                        case "reset" -> {
                            if (messageComponents[2].equals("4dm1n")) {
                                gameRunning = false;
                                server.resetGame();
                                conn.send("Reset game.");
                            } else {
                                conn.send("Wrong password lol");
                            }
                        }
                        case "gen_new" -> {
                            if (messageComponents[2].equals("4dm1n")) {
                                gameRunning = false;
                                server.generateRandomWord(Integer.parseInt(messageComponents[3]));
                                server.resetGame();
                                conn.send("Reset game to new word of " + messageComponents[3] + " characters.");
                            } else {
                                conn.send("Wrong password lol");
                            }
                        }
                        case "custom" -> {
                            if (messageComponents[2].equals("4dm1n")) {
                                gameRunning = false;
                                String custom = messageComponents[3];
                                word = custom.toLowerCase();
                                server.resetGame();
                                conn.send("Set custom word.");
                            } else {
                                conn.send("Wrong password lol");
                            }
                        }
                        case "bc" -> {
                            if (messageComponents[2].equals("4dm1n")) {
                                broadcast(messageComponents[3]);
                                conn.send("Broadcast message.");
                            } else {
                                conn.send("Wrong password lol");
                            }
                        }
                    }
                }
                case "gss" -> {
                    if (!gameRunning) {
                        conn.send("The game is currently paused.");
                        return;
                    }
                    String guess = messageComponents[1];
                    if (guess.length() > 1) {
                        conn.send("Guess only a single letter.");
                        return;
                    }
                    char guessChar = guess.toLowerCase().charAt(0);

                    int correctLetterCounter = 0;
                    // Iterate through every character in the word
                    for (int i = 0; i < wordAndSolving[0].length; i++) {
                        if (wordAndSolving[0][i] == guessChar) {
                            // Set the blanks to the letter
                            wordAndSolving[1][i] = wordAndSolving[0][i];
                            correctLetterCounter++;
                            correctGuesses++;
                        }
                    }
                    if (correctLetterCounter > 0) {
                        server.updateWordGuessUI(server.frame);
                        conn.send("There are " + correctLetterCounter + " of the letter " + String.valueOf(guessChar).toUpperCase() + " in the word.");
                        server.logTextArea.append(
                                conn.getRemoteSocketAddress().getHostName() + " found " + correctLetterCounter + " of the letter " + String.valueOf(guessChar).toUpperCase() + "!\n");
                        if (correctGuesses >= wordAndSolving[0].length) {
                            gameRunning = false;
                            broadcast("You win!");
                            server.logTextArea.append("You win! The word was " + word + ".");
                        }
                    } else {
                        // After the loop, if the character was not in the array, next phase of hangman
                        wrongGuesses++;
                        server.setHangedManImage(wrongGuesses);
                        conn.send("There are no occurrences of the letter " + String.valueOf(guessChar).toUpperCase() + " in the word.");
                        server.logTextArea.append(
                                conn.getRemoteSocketAddress().getHostName() + " did not find the letter " + String.valueOf(guessChar).toUpperCase() + " in this word.\n");
                        // Check for loss
                        if (wrongGuesses >= 6) {
                            gameRunning = false;
                            broadcast("You lose.");
                            server.logTextArea.append("You lose. The word was " + word + ".\n");
                        }
                    }
                }
            }
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            System.err.print("ERROR: ");
            ex.printStackTrace();
        }

        @Override
        public void onStart() {
            System.out.println("Server started on port " + this.getPort());
        }
    }
}
