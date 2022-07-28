package com.mycompany.serverdrawbuddy;

import ServerTypes.Client;
import ServerTypes.GameLobby;
import ServerTypes.Shape;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.security.cert.Certificate;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class SimpleServer extends WebSocketServer {

    public ArrayList<GameLobby> gameLobbies = new ArrayList<>();
    public ArrayList<Client> gameQueue = new ArrayList<>();
    //public Shape shapeModel = new Shape();
    public ArrayList<Client> clientList = new ArrayList<>();

    //Constructor for the server, uncomment line the below lines if using SSL and you have changed the path for the certificates accordingly
    public SimpleServer(InetSocketAddress address) {
        super(address);
        // SSLContext sslContext = getSSLContextFromLetsEncrypt();
        // setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
    }

    //This function is called whenever a new client connects to the server
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Connection to Draw Buddy server successful.");
        String clientCode[] = handshake.toString().split("@");
        System.out.println("New client connection from IP: " + conn.getRemoteSocketAddress() + ", Handshake: "+ clientCode[1]);
        //Adding new client to list of clients
        clientList.add(new Client(conn, "temp", clientCode[1]));
        //Sending the client their auth code which is used for packet verification
        conn.send("AuthCode/"+clientCode[1]);
    }

    //Function is called whenever a client disconnects from the server
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);

        //Checking if any client is inside of a gamelobby after they have disconnected, if they are they will be removed and any other
        //clients still inside the lobby will be sent back to the main menu.
        for(GameLobby e : gameLobbies){
            if(e.getPlayer1().equals(conn) || e.getPlayer2().equals(conn)){
                System.out.println("removing game " +gameLobbies.indexOf(e) +"lobby due to");
                if(e.getPlayer1().isOpen()){
                    System.out.println("player 2 disconnecting");
                    e.getPlayer1().send("GameFinished");
                }
                if(e.getPlayer2().isOpen()){
                    System.out.println("player 1 disconnecting");
                    e.getPlayer2().send("GameFinished");
                }
                gameLobbies.remove(e);
                System.out.println("size of gamelobby array = "+gameLobbies.size());
            }
        }

        //Remove disconnect client from gameQueue if present
        for(Client e : gameQueue){
            if(e.getSocket().equals(conn)){
                System.out.println("removing player from queue due to disconnect:"+conn.toString());
                gameQueue.remove(e);
            }
        }
        //Remove disconnected client from clientList if present
        for(Client e : clientList){
            if(e.getSocket().equals(conn)){
                System.out.println("removing player from client list due to disconnect:"+conn.toString());
                clientList.remove(e);
            }
        }
    }

    //This function is called everytime the server receives a message from a client, it contains various if/else and switch
    //statements that handle all incoming packet data from all clients
    @Override
    public void onMessage(WebSocket conn, String message) {
        if(!message.contains("GameMessage/RequestCanvas/")){
            System.out.println("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
        }

        //Each packet is coming in the form of a string with each value seperated by a '/' symbol
        //so each value is split into an array of strings named clientMessage
        String[] clientMessage = message.split("/");

        //Switch statement which systematically checks what category the incoming packet is for, eg Gameplay, Lobby creation, Error messages
        //it will then execute the corresponding logic using the information provided
        switch(clientMessage[0]){
            case "GameMessage":
                //Function is called when a client has lost connection to the server and then reconnects
                //this statement will then send the shapeList information to the client so they can update their canvas
                //and continue to play
                if(clientMessage[1].matches("RequestCanvas")){
                    GameLobby lobby = gameLobbies.get(Integer.valueOf(clientMessage[2]));
                    if(clientMessage[3].matches(lobby.getP1AuthCode()) || clientMessage[3].matches(lobby.getP2AuthCode())){
                        String canvasInfo = lobby.shapeListToString(Integer.valueOf(clientMessage[4]));
                        conn.send(canvasInfo);
                    }

                }
                //This statement is called whenever the clients attempt to draw to the canvas when it is their turn
                else if(clientMessage[1].matches("UpdateCanvas")){

                    String[] clientMessage2 = clientMessage[4].split(":");
                    GameLobby lobby = gameLobbies.get(Integer.valueOf(clientMessage[2]));
//              shapeModel = new Shape(Integer.valueOf(clientMessage2[2]), Integer.valueOf(clientMessage2[3]), clientMessage2[1], clientMessage2[0], Integer.valueOf(clientMessage2[4]));
                    //If the client sends in an Auth code that matches the authcode for player 1 or player 2
                    //then the new drawing information is added to the shapeList for the lobby and is sent to both players
                    if(clientMessage[3].matches(lobby.getP1AuthCode()) || clientMessage[3].matches(lobby.getP2AuthCode())){
                        shapeModel.setX(Integer.valueOf(clientMessage2[2]));
                        shapeModel.setY(Integer.valueOf(clientMessage2[3]));
                        shapeModel.setType(clientMessage2[1]);
                        shapeModel.setColour(clientMessage2[0]);
                        shapeModel.setLineNo(Integer.valueOf(clientMessage2[4]));
                        lobby.getShapeList().add(shapeModel);
//                  lobby.getShapeList().add(new Shape(Integer.valueOf(clientMessage2[2]), Integer.valueOf(clientMessage2[3]), clientMessage2[1], clientMessage2[0], Integer.valueOf(clientMessage2[4])));
                        lobby.getPlayer1().send("NewShapeInfo/"+clientMessage2[2]+"/"+clientMessage2[3]+"/"+clientMessage2[0]+"/"+clientMessage2[1]+"/"+clientMessage2[4]);
                        lobby.getPlayer2().send("NewShapeInfo/"+clientMessage2[2]+"/"+clientMessage2[3]+"/"+clientMessage2[0]+"/"+clientMessage2[1]+"/"+clientMessage2[4]);
                    }
                }
                //Statement to handle Emote use by players
                else if(clientMessage[1].matches("Emote")){
                    gameLobbies.get(Integer.valueOf(clientMessage[2])).getPlayer1().send("Emote/"+clientMessage[3]);
                    gameLobbies.get(Integer.valueOf(clientMessage[2])).getPlayer2().send("Emote/"+clientMessage[3]);
                }
                break;
            //Handler for lobby turn counter, keeps track of the amount of turns that have happened in the match
            //and tells the players when it is their turn and when the match is over
            case "TurnFinished":
                gameLobbies.get(Integer.valueOf(clientMessage[1])).increaseTurnCount();

                if(gameLobbies.get(Integer.valueOf(clientMessage[1])).getTurnCount() < gameLobbies.get(Integer.valueOf(clientMessage[1])).getMaxTurns()){
                    if( gameLobbies.get(Integer.valueOf(clientMessage[1])).getPlayer1().equals(conn)){
                        gameLobbies.get(Integer.valueOf(clientMessage[1])).getPlayer2().send("YourTurn");
                    }
                    else{
                        gameLobbies.get(Integer.valueOf(clientMessage[1])).getPlayer1().send("YourTurn");
                    }
                }
                else{
                    gameLobbies.get(Integer.valueOf(clientMessage[1])).getPlayer1().send("GameFinished");
                    gameLobbies.get(Integer.valueOf(clientMessage[1])).getPlayer2().send("GameFinished");
                    gameLobbies.remove(gameLobbies.get(Integer.valueOf(clientMessage[1])));
                }
                break;
            //Handler for lobby messages, a client can send a packet with the lobby handler value when they
            //want to create a lobby, join a lobby or exit a lobby
            case "LobbyMessage":
                if(clientMessage[1].matches("CreateLobby")){
                    try {
                        //Creates a lobby and send the lobby info to client after checking that the word is valid

                        if(checkCustomWord(clientMessage[7])){
                            conn.send(createLobby(conn, clientMessage[6], clientMessage[2], clientMessage[3], clientMessage[4], clientMessage[5], clientMessage[7]));
                        }

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(SimpleServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if(clientMessage[1].matches("JoinLobby")){
                    //Get index of lobby in lobbyList from lobby code that is sent by client
                    int lobbyIndex = getLobbyIndexFromCode(clientMessage[2]);
                    //Send the client the lobby info
                    conn.send(lobbyInfoToString(clientMessage[2], conn, clientMessage[4]));
                    if(lobbyIndex != -1){
                        gameLobbies.get(lobbyIndex).setP2Name(clientMessage[3]);
                        gameLobbies.get(lobbyIndex).setPlayer2(conn);
                    }
                }
                //If a client leaves a lobby the lobby will be terminated
                else if(clientMessage[1].matches("TerminateLobby")){
                    //need to create a way on client side that checks every once in awhile if the lobby is still alive
                    gameLobbies.remove(gameLobbies.get(Integer.valueOf(clientMessage[2])));
                    for(Client e : gameQueue){
                        if(e.getSocket().equals(conn)){
                            gameQueue.remove(e);
                        }
                    }
                }
                break;
            //Adds the player to the matchmaking queue
            case "FindMatch":
                try {
                    matchCreator(conn, clientMessage[1], clientMessage[2], clientMessage[3]);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SimpleServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            //A client will send a packet saying "Ready" after they have received the information for the lobby and have loaded into the game
            //this statement will tell each player that the match is ready to start once the ready counter hits 2
            case "Ready":
                if(conn.equals(gameLobbies.get(Integer.valueOf(clientMessage[1])).getPlayer1())){
                    gameLobbies.get(Integer.valueOf(clientMessage[1])).getPlayer2().send("ResolutionRatio/"+clientMessage[2]+"/"+clientMessage[3]);
                }
                else{
                    gameLobbies.get(Integer.valueOf(clientMessage[1])).getPlayer1().send("ResolutionRatio/"+clientMessage[2]+"/"+clientMessage[3]);
                }

                gameLobbies.get(Integer.valueOf(clientMessage[1])).increaseReadyCount();
                if( gameLobbies.get(Integer.valueOf(clientMessage[1])).getReadyCount() == 2){
                    gameLobbies.get(Integer.valueOf(clientMessage[1])).getPlayer1().send("YourTurn/"+ gameLobbies.get(Integer.valueOf(clientMessage[1])).getP2Name());
                    gameLobbies.get(Integer.valueOf(clientMessage[1])).getPlayer2().send("PartnerTurn/"+ gameLobbies.get(Integer.valueOf(clientMessage[1])).getP1Name());
                }
                break;
            //The client will send a CheckName packet when they attempt to set their user name when first opening the game
            case "CheckName":
                try {
                    if(checkName(clientMessage[1]) == true){
                        //Go through the list of clients and set the name of the client
                        for(Client e : clientList){
                            if(e.getSocket().equals(conn)){
                                e.setName(clientMessage[1]);
                                clientList.remove(e);
                            }
                        }
                        conn.send("Pass");
                        System.out.println("sending back pass");
                    }
                    else{
                        conn.send("Fail");
                        System.out.println("sending back fail");
                    }
                } catch (FileNotFoundException ex) {
                    System.out.println("exception found in checkname "+ex);
                }
                break;
            //ReturnToMain is used when a client chooses to return to the main menu moving to the find match screen
            //they will be removed from the gameQueue list
            case "ReturnToMain":
                for(int i = 0; i <= gameQueue.size() - 1; i++){
                    System.out.println("inside for loop");
                    if(conn.equals(gameQueue.get(i).getSocket())){
                        gameQueue.remove(i);
                    }
                }
                System.out.println("gamequeue size after removing "+gameQueue.size());
                break;
        }
    }

    //Handler for ByteBuffer packets
    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress() + "message = " + message);
    }

    //Function called when client encounters an error, eg Null error, concurrent modification ect
    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    //Function called when server is started
    @Override
    public void onStart() {
        System.out.println("server started successfully");
        setConnectionLostTimeout(5);
    }

    //Function to create a game lobby
    public String createLobby(WebSocket conn, String clientname, String lobbyType, String p1Auth, String roundAmount, String roundTime, String drawTopic) throws FileNotFoundException{
        //Obtain a list of all the current lobby codes, it is used in the constructor for a lobby to ensure
        //that duplicate lobby codes are not being created
        String[] lobbyCodes = new String[gameLobbies.size()];
        for(int i = 0; i <= gameLobbies.size() - 1; i++){
            lobbyCodes[i] += gameLobbies.get(i)+"\n";
        }

        //Creat the new lobby object, set the lobby info according to the input variables
        GameLobby newLobby = new GameLobby(lobbyCodes, lobbyType);
        newLobby.setP1AuthCode(p1Auth);
        newLobby.setP1Name(clientname);
        newLobby.setPlayer1(conn);
        newLobby.setMaxTurns(Integer.valueOf(roundAmount));

        if(!drawTopic.matches("Random")){
            newLobby.setWordTopic(drawTopic);
        }

        switch(roundTime){
            case("10 sec"):
                newLobby.setTurnTime("10");
                break;
            case("15 sec"):
                newLobby.setTurnTime("15");
                break;
            case("20 sec"):
                newLobby.setTurnTime("20");
                break;
            case("30 sec"):
                newLobby.setTurnTime("30");
                break;
            case("1 min"):
                newLobby.setTurnTime("60");
                break;
        }
        //Add the new lobby to the lobby list
        gameLobbies.add(newLobby);
        newLobby.setLobbyIndex(gameLobbies.indexOf(newLobby));
        System.out.println("New lobby created at index:"+newLobby.getLobbyIndex());
        return "LobbyInfo/"+newLobby.lobbyToString()+"/"+lobbyType+"/"+newLobby.getMaxTurns()+"/"+newLobby.getTurnTime();
    }

    //Function to convert a lobby object into a string which is used by the client to save the lobby info
    public String lobbyInfoToString(String lobbyCode, WebSocket Conn, String p2Auth){
        for(int i = 0; i <= gameLobbies.size() - 1; i++){
            if(gameLobbies.get(i).getLobbyCode().matches(lobbyCode)){
                gameLobbies.get(i).setPlayer2(Conn);
                gameLobbies.get(i).setP2AuthCode(p2Auth);
                gameLobbies.get(i).getPlayer1().send("Ready");
                return "LobbyInfo/"+gameLobbies.get(i).lobbyToString()+"/"+gameLobbies.get(i).getLobbyType()+"/"+gameLobbies.get(i).getMaxTurns()+"/"+gameLobbies.get(i).getTurnTime();
            }
        }
        return "Error";
    }

    //Function to get the Array index of the lobby from the lobby code string
    public int getLobbyIndexFromCode(String lobbyCode){
        for(int i = 0; i <= gameLobbies.size() - 1; i++){
            if(gameLobbies.get(i).getLobbyCode().matches(lobbyCode)){
                return i;
            }
        }
        return -1;
    }

    public void matchCreator(WebSocket conn, String clientname, String authCode, String gameMode) throws FileNotFoundException{
        for(int i = 0; i <= gameQueue.size() - 1; i++){
            //If there are people in the queue then this function is called, it will then create the lobby
            //and send the data to the first person found in the queue and to the person who called the function

            //Creating lobby with basic settings
            String lobbyInfo = createLobby(conn, clientname, gameMode, authCode, "6", "10 sec", "Random");
            //Sending lobby info to player who called the function
            conn.send(lobbyInfo);
            String[] lobbyString = lobbyInfo.split("/");
            //Setting the values for player 2 in the lobby, player 2 in this case is the person who was waiting in the queue
            gameLobbies.get(Integer.valueOf(lobbyString[1])).setPlayer2(gameQueue.get(i).getSocket());
            gameLobbies.get(Integer.valueOf(lobbyString[1])).setP2Name(gameQueue.get(i).getName());
            gameLobbies.get(Integer.valueOf(lobbyString[1])).setP2AuthCode(gameQueue.get(i).getAuthcode());
            //Sending the lobby info to player 2
            gameQueue.get(i).getSocket().send(lobbyInfo);
            //Removing player 2 from the queue
            gameQueue.remove(i);
            return;
        }
        //If there are no players in the queue then add the player who called this function to the queue
        gameQueue.add(new Client(conn, clientname, authCode));
    }

    //Main method which creates the server object and starts it
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        //Amazon EC-2 Machine IP (Only use when server is being run on the EC2 or add a new IP for any new server being used)
        //String host = "172.31.45.56";

        //Use localhost when running server from local environment
        String host = "localhost";
        int port = 8080;
        SimpleServer server = new SimpleServer(new InetSocketAddress(host, port));
        System.out.println("Attemping to run server on IP: "+host+", using Port: "+port);
        server.start();
    }

    //Function to check if player may have modified the game to send custom drawing topic, this could create problems therefore we check if the
    //topic sent by the client is apart of the approved list
    public boolean checkCustomWord(String drawTopic) throws FileNotFoundException{
        String temp = "";

        if(drawTopic.matches("Random")){
            return true;
        }

        File txt = new File(System.getProperty("user.dir")+"\\src\\main\\java\\Files\\topicwords.txt");
        //File txt = new File("/home/ec2-user/topicwords.txt");
        Scanner scan = new Scanner(txt);
        ArrayList<String> data = new ArrayList<>() ;
        while(scan.hasNextLine()){
            data.add(scan.nextLine());
        }

        String[] simpleArray = data.toArray(new String[]{});

        for(String e : simpleArray){
            if(e.matches(drawTopic)){
                return true;
            }
        }
        return false;
    }

    //Function to create the SSL socket using a LetsEncrypt certificate
    private static SSLContext getSSLContextFromLetsEncrypt() {
        SSLContext context;
        String pathTo = "/etc/letsencrypt/live/drawbuddygame.co.vu";
        String keyPassword = "";
        try {
            context = SSLContext.getInstance("TLS");

            byte[] certBytes = parseDERFromPEM(Files.readAllBytes(new File(pathTo + File.separator + "cert.pem").toPath()), "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----");
            byte[] keyBytes = parseDERFromPEM(Files.readAllBytes(new File(pathTo + File.separator + "privkey.pem").toPath()), "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");

            X509Certificate cert = generateCertificateFromDER(certBytes);
            RSAPrivateKey key = generatePrivateKeyFromDER(keyBytes);

            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(null);
            keystore.setCertificateEntry("cert-alias", cert);
            keystore.setKeyEntry("key-alias", key, keyPassword.toCharArray(), new Certificate[]{cert});

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, keyPassword.toCharArray());

            KeyManager[] km = kmf.getKeyManagers();

            context.init(km, null, null);
        } catch (IOException | KeyManagementException | KeyStoreException | InvalidKeySpecException | UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException e) {
            System.out.println("caught "+e);
            throw new IllegalArgumentException();
        }
        return context;
    }

    //getSSLContextFromLetsEncrypt helper function
    protected static byte[] parseDERFromPEM(byte[] pem, String beginDelimiter, String endDelimiter) {
        String data = new String(pem);
        String[] tokens = data.split(beginDelimiter);
        tokens = tokens[1].split(endDelimiter);
        tokens[0] = tokens[0].replace("\n","");
        return Base64.getDecoder().decode(tokens[0]);
    }
    //getSSLContextFromLetsEncrypt helper function
    protected static RSAPrivateKey generatePrivateKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) factory.generatePrivate(spec);
    }
    //getSSLContextFromLetsEncrypt helper function
    protected static X509Certificate generateCertificateFromDER(byte[] certBytes) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
    }

    //Function that checks if the name the client is attempting to use is on the list of badwords
    private boolean checkName(String name) throws FileNotFoundException{

        //Document file path for EC2 Amazon machine
        //File txt = new File("/home/ec2-user/badwords.txt");

        //File path if running the server locally
        File txt = new File(System.getProperty("user.dir")+"\\src\\main\\java\\Files\\badwords.txt");

        Scanner scan = new Scanner(txt);
        ArrayList<String> data = new ArrayList<>() ;
        while(scan.hasNextLine()){
            data.add(scan.nextLine());
        }

        String nameLower = name.toLowerCase();
        String[] simpleArray = data.toArray(new String[]{});
        String temp = "";

        for(int i = 0; i <= simpleArray.length - 1; i++){
            temp = simpleArray[i].toLowerCase();
            if(nameLower.contains(temp) || temp.matches(nameLower)){
                return false;
            }
        }
        return true;
    }
}