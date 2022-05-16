package ServerTypes;

import org.java_websocket.WebSocket;

public class Client {
    
    public WebSocket socket;
    public String name;
    public String authcode;
    
    public Client(WebSocket socket, String name, String code){
        this.socket = socket;
        this.name = name;
        authcode = code;
    }
}
