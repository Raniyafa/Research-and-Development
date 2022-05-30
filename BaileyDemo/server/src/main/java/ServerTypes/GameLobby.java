package ServerTypes;

import java.io.File;
import java.io.FileNotFoundException;
import org.java_websocket.WebSocket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GameLobby {

    //Reference to the players websocket object, can be used to send packets to the clients
    private WebSocket player1;
    private WebSocket player2;
    private String p1Name;
    private String p2Name;
    //Auth codes are assigned to the client when they connect to the server and are used to verify packets
    private String p1AuthCode;
    private String p2AuthCode;
    //Each game lobby will have an ArrayList of shapes which holds the drawing canvas information
    private ArrayList<Shape> shapeList;
    //Each lobby will have a unique lobby code as a "password" or identify for the lobby, it is used by clients to join a lobby
    private String lobbyCode;
    //Reference to the index value of the lobby in the list of lobbies used by server
    private int lobbyIndex;
    //Booleans to check if clients have been recently pinged for disconnection detection
    private boolean client1pinged, client2pinged;
    //The ready count for the lobby, once a player loads into the game they send a ready message, once this value hits 2 the match will start
    private int readyCount;
    //The amount of turns that have been completed within the match
    private int turnCount;
    //The maximum amount of turns that a lobby will have before the match is over
    private int maxTurns;
    //How long each turn is in the match
    private String turnTime;
    //The drawing topic for the match
    private String wordTopic;
    //The type of lobby eg, regular, one line mode
    private String lobbyType;

    public GameLobby(String[] lobbyCodes, String gameMode) throws FileNotFoundException{
       //Generate lobby code
       lobbyCode = generateLobbyCode(lobbyCodes);
       //Generate a word topic
       wordTopic = generateWord();
       shapeList = new ArrayList<>();
       client1pinged = true;
       client2pinged = false;
       readyCount = 0;
       maxTurns = 6;
       lobbyType = gameMode;
    }

    public String shapeListToString(int index){
        //Convert a shape list into a string which can be sent to the client and then added to the drawing canvas
        String listString = "CanvasInfo/"+shapeList.size()+"/";
        for(int i = index; i <= shapeList.size() - 1; i++){
                listString += shapeList.get(i).getType()+"/"+shapeList.get(i).getColour()+"/"+shapeList.get(i).getX()+"/"+shapeList.get(i).getY()+"/";
        }
        return listString;
    }
    
    public String lobbyToString(){
        //Convert lobby info into string
        return String.valueOf(lobbyIndex)+"/"+lobbyCode+"/"+wordTopic;
    }

    //Generate a lobby code, has an array of lobbyCodes as an input variable which is iterated through to check if the generated code already exists
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
    
    //Generate a random word topic from the list of words topic.txt
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
      
    //Getters and setters for the class variables

    public void increaseTurnCount(){
        this.turnCount++;
    }
    
    public void increaseReadyCount(){
        this.readyCount++;
    }
    
    public WebSocket getPlayer1() {
        return player1;
    }
        
    public void setPlayer1(WebSocket player1) {
        this.player1 = player1;
    }

    public String getP1Name() {
        return p1Name;
    }

    public void setP1Name(String p1Name) {
        this.p1Name = p1Name;
    }

    public String getP1AuthCode() {
        return p1AuthCode;
    }

    public void setP1AuthCode(String p1AuthCode) {
        this.p1AuthCode = p1AuthCode;
    }

    public String getP2Name() {
        return p2Name;
    }

    public void setP2Name(String p2Name) {
        this.p2Name = p2Name;
    }

    public String getP2AuthCode() {
        return p2AuthCode;
    }

    public void setP2AuthCode(String p2AuthCode) {
        this.p2AuthCode = p2AuthCode;
    }

    public WebSocket getPlayer2() {
        return player2;
    }

    public void setPlayer2(WebSocket player2) {
        this.player2 = player2;
    }

    public ArrayList<Shape> getShapeList() {
        return shapeList;
    }

    public void setShapeList(ArrayList<Shape> shapeList) {
        this.shapeList = shapeList;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public int getLobbyIndex() {
        return lobbyIndex;
    }

    public void setLobbyIndex(int lobbyIndex) {
        this.lobbyIndex = lobbyIndex;
    }

    public boolean isClient1pinged() {
        return client1pinged;
    }

    public void setClient1pinged(boolean client1pinged) {
        this.client1pinged = client1pinged;
    }

    public boolean isClient2pinged() {
        return client2pinged;
    }

    public void setClient2pinged(boolean client2pinged) {
        this.client2pinged = client2pinged;
    }

    public int getReadyCount() {
        return readyCount;
    }

    public void setReadyCount(int readyCount) {
        this.readyCount = readyCount;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }

    public String getTurnTime() {
        return turnTime;
    }

    public void setTurnTime(String turnTime) {
        this.turnTime = turnTime;
    }

    public String getWordTopic() {
        return wordTopic;
    }

    public void setWordTopic(String wordTopic) {
        this.wordTopic = wordTopic;
    }

    public String getLobbyType() {
        return lobbyType;
    }

    public void setLobbyType(String lobbyType) {
        this.lobbyType = lobbyType;
    }
}