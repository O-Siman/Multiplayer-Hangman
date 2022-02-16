import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

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
            case SERVER -> {
                ServerWebSocket server = new ServerWebSocket(new InetSocketAddress(8080));
                server.start();
                System.out.println(server.getAddress());
            }
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
        URI serverAddressUri = new URI("wss://0.0.0.0:8080");
        ClientWebSocket client = new ClientWebSocket(serverAddressUri);
        System.out.println("Connecting...");
        client.connectBlocking();
        System.out.println("Sending message");
        client.send("Hello there");
        System.out.println("Closing");
        client.close();
    }
}

enum GameType {
    SERVER, CLIENT
}