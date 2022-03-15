package Handlers;

public class LobbyHandler implements Runnable{

    private boolean isRunning;
    
    public LobbyHandler(){
        isRunning = true;
        
    }
    
    @Override
    public void run() {
        while(isRunning){
            try{


            }
            catch(Exception e){
                throw new UnsupportedOperationException("Not supported yet."); 
            }
        }
    }
    
}
