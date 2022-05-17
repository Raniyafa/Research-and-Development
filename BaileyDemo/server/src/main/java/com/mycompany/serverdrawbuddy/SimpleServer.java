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
    
    public ArrayList<Client> clientList = new ArrayList<>();
      
    public SimpleServer(InetSocketAddress address) {
        super(address);
       // SSLContext sslContext = getSSLContextFromLetsEncrypt();
      // setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("/Welcome to the server!"); //This method sends a message to the new client
       // broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        String clientCode[] = handshake.toString().split("@");
        System.out.println("new connection to " + conn.getRemoteSocketAddress() + "handshake: "+ clientCode[1]);
        clientList.add(new Client(conn, "temp", clientCode[1]));
        conn.send("AuthCode/"+clientCode[1]);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        
        for(GameLobby e : gameLobbies){
            if(e.player1.equals(conn) || e.player2.equals(conn)){
                System.out.println("removing game " +gameLobbies.indexOf(e) +"lobby due to");
                if(e.player1.isOpen()){
                System.out.println("player 2 disconnecting");
                e.player1.send("GameFinished");
                }
                if(e.player2.isOpen()){
                System.out.println("player 1 disconnecting");
                e.player2.send("GameFinished");
                }
                gameLobbies.remove(e);
                System.out.println("size of gamelobby array = "+gameLobbies.size());
            }
        }
        
        for(Client e : gameQueue){
            if(e.socket.equals(conn)){
                System.out.println("removing player from queue due to disconnect:"+conn.toString());
                gameQueue.remove(e);
            }
        }
        
        for(Client e : clientList){
            if(e.socket.equals(conn)){
                System.out.println("removing player from client list due to disconnect:"+conn.toString());
                clientList.remove(e);
            }
        }
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        if(!message.contains("GameMessage/RequestCanvas/")){
        System.out.println("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
        }

        String[] clientMessage = message.split("/");
        
        if(clientMessage[0].matches("GameMessage")){
            if(clientMessage[1].matches("RequestCanvas")){
                GameLobby lobby = gameLobbies.get(Integer.valueOf(clientMessage[2]));
                if(clientMessage[3].matches(lobby.p1AuthCode) || clientMessage[3].matches(lobby.p2AuthCode)){
                String canvasInfo = lobby.shapeListToString(Integer.valueOf(clientMessage[4]));
                conn.send(canvasInfo);
                }
         
            }
            else if(clientMessage[1].matches("UpdateCanvas")){
                //called by either play in the lobby, updates the shared canvas that is accessed by both players
               // gameLobbies.get(Integer.valueOf(clientMessage[2])).stringToShapeList(clientMessage[3]);
               
               String[] clientMessage2 = clientMessage[4].split(":");
               GameLobby lobby = gameLobbies.get(Integer.valueOf(clientMessage[2]));
               if(clientMessage[3].matches(lobby.p1AuthCode) || clientMessage[3].matches(lobby.p2AuthCode)){
                    lobby.shapeList.add(new Shape(Integer.valueOf(clientMessage2[2]), Integer.valueOf(clientMessage2[3]), clientMessage2[1], clientMessage2[0], Integer.valueOf(clientMessage2[4])));
                 //   if(gameLobbies.get(Integer.valueOf(clientMessage[2])).player2.equals(conn)){
                    lobby.player1.send("GameMessage2/"+clientMessage2[2]+"/"+clientMessage2[3]+"/"+clientMessage2[0]+"/"+clientMessage2[1]+"/"+clientMessage2[4]);
                //    }
                 //   else{
                    lobby.player2.send("GameMessage2/"+clientMessage2[2]+"/"+clientMessage2[3]+"/"+clientMessage2[0]+"/"+clientMessage2[1]+"/"+clientMessage2[4]);
                  //  }   
                     }
            }
            else if(clientMessage[1].matches("Emote")){
                    gameLobbies.get(Integer.valueOf(clientMessage[2])).player1.send("Emote/"+clientMessage[3]); 
                    gameLobbies.get(Integer.valueOf(clientMessage[2])).player2.send("Emote/"+clientMessage[3]); 
            }
        }
                else if(clientMessage[0].matches("TurnFinished")){     
           gameLobbies.get(Integer.valueOf(clientMessage[1])).turnCount++;
           
           if(gameLobbies.get(Integer.valueOf(clientMessage[1])).turnCount < gameLobbies.get(Integer.valueOf(clientMessage[1])).maxTurns){
                if( gameLobbies.get(Integer.valueOf(clientMessage[1])).player1.equals(conn)){
                    gameLobbies.get(Integer.valueOf(clientMessage[1])).player2.send("YourTurn");
                }
                else{
                    gameLobbies.get(Integer.valueOf(clientMessage[1])).player1.send("YourTurn");
                }
           }
           else{
               gameLobbies.get(Integer.valueOf(clientMessage[1])).player1.send("GameFinished");
               gameLobbies.get(Integer.valueOf(clientMessage[1])).player2.send("GameFinished");
               gameLobbies.remove(gameLobbies.get(Integer.valueOf(clientMessage[1])));
           }
        }
        else if(clientMessage[0].matches("LobbyMessage")){
            if(clientMessage[1].matches("CreateLobby")){
                try {
                    //creates a lobby and send the lobby info to client

                    conn.send(createLobby(conn, "temp_name", clientMessage[2], clientMessage[3], clientMessage[4], clientMessage[5]));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SimpleServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if(clientMessage[1].matches("JoinLobby")){
                conn.send(lobbyInfoToString(clientMessage[2], conn, clientMessage[4]));
                gameLobbies.get(Integer.valueOf(clientMessage[2])).p2Name = clientMessage[3];
                gameLobbies.get(Integer.valueOf(clientMessage[2])).player2 = conn;
              //  gameLobbies.get(Integer.valueOf(clientMessage[2])).p2AuthCode = clientMessage[4];
                
               
            }
            else if(clientMessage[1].matches("TerminateLobby")){
                //need to create a way on client side that checks every once in awhile if the lobby is still alive
                gameLobbies.remove(gameLobbies.get(Integer.valueOf(clientMessage[2])));
                for(Client e : gameQueue){
                    if(e.socket.equals(conn)){
                        gameQueue.remove(e);
                    }
                }
            }
        }
        else if(clientMessage[0].matches("FindMatch")){
            try {
                matchCreator(conn, clientMessage[1], clientMessage[2], clientMessage[3]);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SimpleServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(clientMessage[0].matches("Ready")){
           gameLobbies.get(Integer.valueOf(clientMessage[1])).readyCount++;
           if( gameLobbies.get(Integer.valueOf(clientMessage[1])).readyCount == 2){
               gameLobbies.get(Integer.valueOf(clientMessage[1])).player1.send("YourTurn/"+ gameLobbies.get(Integer.valueOf(clientMessage[1])).p2Name);
               gameLobbies.get(Integer.valueOf(clientMessage[1])).player2.send("PartnerTurn/"+ gameLobbies.get(Integer.valueOf(clientMessage[1])).p1Name);
           }
        }
        else if(clientMessage[0].matches("CheckName")){    
            try {
                if(checkName(clientMessage[1]) == true){
                    for(Client e : clientList){
                        if(e.socket.equals(conn)){
                            e.name = clientMessage[1];
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
        }
         else if(clientMessage[0].matches("ReturnToMain")){    
             for(int i = 0; i <= gameQueue.size() - 1; i++){
                 System.out.println("inside for loop");
                 if(conn.equals(gameQueue.get(i).socket)){
                     gameQueue.remove(i);
                 }
             }
             System.out.println("gamequeue size after removing "+gameQueue.size());
         }
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress() + "message = " + message);
    }
    

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("server started successfully");
        setConnectionLostTimeout(5);
    }

    //Lobby server functions
    
    public String createLobby(WebSocket conn, String clientname, String lobbyType, String p1Auth, String roundAmount, String roundTime) throws FileNotFoundException{
        String[] lobbyCodes = new String[gameLobbies.size()];     
        for(int i = 0; i <= gameLobbies.size() - 1; i++){
            lobbyCodes[i] += gameLobbies.get(i)+"\n";
        }
        
        GameLobby newLobby = new GameLobby(lobbyCodes, lobbyType);
        newLobby.p1AuthCode = p1Auth;
        newLobby.p1Name = clientname;
        newLobby.player1 = conn;
        newLobby.maxTurns = Integer.valueOf(roundAmount);
        
        switch(roundTime){
            case("10 sec"):
                newLobby.turnTime = "10";
            break;
            case("15 sec"):
                newLobby.turnTime = "15";
            break;
            case("20 sec"):
                newLobby.turnTime = "20";
            break;
            case("30 sec"):
                newLobby.turnTime = "30";
            break;
            case("1 min"):
                newLobby.turnTime = "60";
            break;        
        }
        
        gameLobbies.add(newLobby);
        newLobby.lobbyIndex = gameLobbies.indexOf(newLobby);
        System.out.println("New lobby created at index:"+newLobby.lobbyIndex);
        return "LobbyInfo/"+newLobby.lobbyToString()+"/"+lobbyType+"/"+newLobby.maxTurns+"/"+newLobby.turnTime;    
    }
    
    public String lobbyInfoToString(String lobbyCode, WebSocket Conn, String p2Auth){
        for(int i = 0; i <= gameLobbies.size() - 1; i++){
            if(gameLobbies.get(i).lobbyCode.matches(lobbyCode)){
                gameLobbies.get(i).player2 = Conn;
                gameLobbies.get(i).p2AuthCode = p2Auth;
                gameLobbies.get(i).player1.send("Ready");
                return "LobbyInfo/"+gameLobbies.get(i).lobbyToString()+"/"+gameLobbies.get(i).lobbyType+"/"+gameLobbies.get(i).maxTurns+"/"+gameLobbies.get(i).turnTime;            
            }
        }
        return "Error";  
    }
    
    public void matchCreator(WebSocket conn, String clientname, String authCode, String gameMode) throws FileNotFoundException{
        for(int i = 0; i <= gameQueue.size() - 1; i++){
            
            String lobbyInfo = createLobby(conn, clientname, gameMode, authCode, "6", "10 sec");
            
            conn.send(lobbyInfo);         
            String[] lobbyString = lobbyInfo.split("/");   
            
            //set the player 2 info from the gameQueue(i) position
            gameLobbies.get(Integer.valueOf(lobbyString[1])).player2 = gameQueue.get(i).socket;
            gameLobbies.get(Integer.valueOf(lobbyString[1])).p2Name = gameQueue.get(i).name;
            gameLobbies.get(Integer.valueOf(lobbyString[1])).p2AuthCode = gameQueue.get(i).authcode;
            gameQueue.get(i).socket.send(lobbyInfo);
            gameQueue.remove(i);
            return;
        }
        gameQueue.add(new Client(conn, clientname, authCode));
    }
    
    //In match server functions
    
    public String shapeDataToString(){
        return "lol";
    }
    

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
      
        
        //String host = "172.31.45.56";
        String host = "localhost";
        int port = 8080;
        
        SimpleServer server = new SimpleServer(new InetSocketAddress(host, port));
    
        System.out.println("Attemping to run server with "+host+" on port "+port);
                
        
        server.start();   
        
     
    }
    
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

        protected static byte[] parseDERFromPEM(byte[] pem, String beginDelimiter, String endDelimiter) {
            String data = new String(pem);               
            String[] tokens = data.split(beginDelimiter);
            tokens = tokens[1].split(endDelimiter);
            tokens[0] = tokens[0].replace("\n","");
            return Base64.getDecoder().decode(tokens[0]);
        }

        protected static RSAPrivateKey generatePrivateKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) factory.generatePrivate(spec);
        }

        protected static X509Certificate generateCertificateFromDER(byte[] certBytes) throws CertificateException {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");

            return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
        }
       
        private boolean checkName(String name) throws FileNotFoundException{
         //  File txt = new File("/home/ec2-user/badwords.txt");
           // System.out.println("badword dir = "+System.getProperty("user.dir"));
         
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

