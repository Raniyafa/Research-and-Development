package ServerTypes;

import java.io.File;
import java.io.FileNotFoundException;
import org.java_websocket.WebSocket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GameLobby {
    
    public WebSocket player1;
    public String p1Name;
    public String p2Name;
    public WebSocket player2;
    public ArrayList<Shape> shapeList;
    public String lobbyCode;
    public int lobbyIndex;
    public boolean client1pinged, client2pinged;
    public int readyCount;
    public int turnCount;
    public int maxTurns;
    public float turnTime;
    public String wordTopic;
    public String lobbyType;

    public GameLobby(String[] lobbyCodes, String gameMode) throws FileNotFoundException{
       lobbyCode = generateLobbyCode(lobbyCodes);
       wordTopic = generateWord();
       shapeList = new ArrayList<>();
       client1pinged = true;
       client2pinged = false;
       readyCount = 0;
       maxTurns = 6;
       lobbyType = gameMode;
    }
    
    public void stringToShapeList(String shape){
        String[] clientMessage = shape.split(":");
        shapeList.add(new Shape(Integer.valueOf(clientMessage[2]), Integer.valueOf(clientMessage[3]), clientMessage[0], clientMessage[1]));
    }
    
    public String shapeListToString(int index){
        String listString = "CanvasInfo/"+shapeList.size()+"/";
        for(int i = index; i <= shapeList.size() - 1; i++){
                listString += shapeList.get(i).type+"/"+shapeList.get(i).colour+"/"+shapeList.get(i).x+"/"+shapeList.get(i).y+"/";
        }
        return listString;
    }
    
    public String lobbyToString(){
        return String.valueOf(lobbyIndex)+"/"+lobbyCode+"/"+wordTopic;
    }

    public String generateLobbyCode(String[] lobbyCodes){
        String code = "";
        
        Random rand = new Random();
        
        for(int i = 0; i <= 5; i++){
            if(rand.nextInt(2) > 1){
                code += (char) (rand.nextInt(9) + 47);
            }
            else{
                code += (char) (rand.nextInt(25) + 97);
            }
        }
        
        for(int i = 0; i <= lobbyCodes.length - 1; i++){
            if(code.matches(lobbyCodes[i])){
                code = generateLobbyCode(lobbyCodes);
            }
        }
        
        return code;
    }
    
    public String generateWord() throws FileNotFoundException{
        String temp = "";
        
            File txt = new File(System.getProperty("user.dir")+"\\src\\main\\java\\Files\\topicwords.txt");
           //File txt = new File("/home/ec2-user/topicwords.txt");
           Scanner scan = new Scanner(txt);
            ArrayList<String> data = new ArrayList<>() ;
            while(scan.hasNextLine()){
                data.add(scan.nextLine());
            }
            
            String[] simpleArray = data.toArray(new String[]{});    
            Random rand = new Random();      
            return simpleArray[rand.nextInt(simpleArray.length)];
    }
}

