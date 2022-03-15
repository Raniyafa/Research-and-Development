package ServerTypes;

import org.java_websocket.WebSocket;

public class Client {
    
    public WebSocket socket;
    public String name;
    
    public Client(WebSocket socket, String name){
        this.socket = socket;
        this.name = name;
    }
}
