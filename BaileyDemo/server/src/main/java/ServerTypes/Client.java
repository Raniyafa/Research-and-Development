package ServerTypes;

import org.java_websocket.WebSocket;

//Simple class to represent a client on the server, contains references to a players WebSocket, name and authcode
public class Client {

    private WebSocket socket;
    private String name;
    private String authcode;
    
    public Client(WebSocket socket, String name, String code){
        this.socket = socket;
        this.name = name;
        authcode = code;
    }
    
        public WebSocket getSocket() {
        return socket;
    }

    public void setSocket(WebSocket socket) {
        this.socket = socket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }
}
