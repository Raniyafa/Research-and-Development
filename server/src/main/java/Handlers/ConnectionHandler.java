package Handlers;

import java.util.ArrayList;
import org.java_websocket.WebSocket;

public class ConnectionHandler implements Runnable{

    private boolean isRunning;
    private ArrayList<WebSocket> clientList;
    private ArrayList<String> requestList;
    
    public ConnectionHandler(){
        isRunning = true;
        clientList = new ArrayList<>();
        requestList = new ArrayList<>();
    }
    
    @Override
    public void run() {
        while(isRunning){
            try{
                if(!requestList.isEmpty()){
                    
                }
                
                

            }
            catch(Exception e){
                throw new UnsupportedOperationException("Not supported yet."); 
            }
        }
    }
    
}
