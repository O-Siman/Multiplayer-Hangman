import java.io.IOException;
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
                try {
                    new Server().main();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        URI serverAddressUri = new URI("ws://localhost:8082");
        ClientWebSocket client = new ClientWebSocket(serverAddressUri);
        client.connectBlocking();
    }
}

enum GameType {
    SERVER, CLIENT
}