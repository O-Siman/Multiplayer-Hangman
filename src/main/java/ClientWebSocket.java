import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Collections;

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
        System.out.println("message = " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("code = " + code);
        System.out.println("reason = " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}