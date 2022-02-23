import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ClientWebSocket extends WebSocketClient {
    public ClientWebSocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected.");
    }

    @Override
    public void onMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.err.println("Disconnected.");
        System.err.println("Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
