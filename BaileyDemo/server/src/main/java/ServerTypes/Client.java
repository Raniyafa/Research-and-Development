/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerTypes;

import org.java_websocket.WebSocket;

/**
 *
 * @author Venus
 */
public class Client {
    
    public WebSocket socket;
    public String name;
    
    public Client(WebSocket socket, String name){
        this.socket = socket;
        this.name = name;
    }
}
