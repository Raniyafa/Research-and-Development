package Handlers;

public class MatchHandler implements Runnable{

    private boolean isRunning;
    
    public MatchHandler(){
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