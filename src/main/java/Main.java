import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GameType gameType;
        if (args.length > 0) {
            if (args[0].equals("server"))
                gameType = GameType.SERVER;
            else if (args[0].equals("client"))
                gameType = GameType.CLIENT;
            else
                return;
        } else
            return;

        switch (gameType) {
            case SERVER -> new Server().main();
            case CLIENT -> {
                try {
                    runClient();
                } catch (URISyntaxException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void runClient() throws URISyntaxException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input the join code: ");
        int port = scanner.nextInt();
        scanner.nextLine();
        URI serverAddressUri = new URI("ws://10.2.6.70:" + port);
        ClientWebSocket client = new ClientWebSocket(serverAddressUri);
        System.out.println("Connecting...");
        client.connectBlocking();
        if (!client.isOpen()) {
            System.err.println("Didn't work. Rerun the program and try again.");
            System.exit(1);
        }
        while (true) {
            String input = scanner.nextLine();
            if (input.startsWith("/")) {
                client.send("cmd " + input.substring(1));
            } else {
                client.send("gss " + input);
            }
        }
    }
}

enum GameType {
    SERVER, CLIENT
}