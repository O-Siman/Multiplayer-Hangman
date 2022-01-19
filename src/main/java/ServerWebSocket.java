import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class ServerWebSocket extends WebSocketServer {
    public ServerWebSocket(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Connection opened");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.print("Message: ");
        System.out.println("message = " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.print("ERROR: ");
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started.");
    }
}
