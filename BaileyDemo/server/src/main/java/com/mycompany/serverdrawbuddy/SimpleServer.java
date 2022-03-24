package com.mycompany.serverdrawbuddy;

import ServerTypes.Client;
import ServerTypes.GameLobby;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
      
    public SimpleServer(InetSocketAddress address) {
        super(address);
        SSLContext sslContext = getSSLContextFromLetsEncrypt();
        setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("/Welcome to the server!"); //This method sends a message to the new client
       // broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        
        for(GameLobby e : gameLobbies){
            if(e.player1.equals(conn) || e.player2.equals(conn)){
                System.out.println("removing game lobby "+gameLobbies.indexOf(e));
                if(e.player1.isOpen()){
                e.player1.send("GameFinished");
                }
                if(e.player2.isOpen()){
                e.player2.send("GameFinished");
                }
                gameLobbies.remove(e);
                System.out.println("size of gamelobby array = "+gameLobbies.size());
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
            if(clientMessage[1].matches("UpdateCanvas")){
                //called by either play in the lobby, updates the shared canvas that is accessed by both players
                gameLobbies.get(Integer.valueOf(clientMessage[2])).stringToShapeList(clientMessage[3]);
            }
            else if(clientMessage[1].matches("RequestCanvas")){
                String canvasInfo = gameLobbies.get(Integer.valueOf(clientMessage[2])).shapeListToString(Integer.valueOf(clientMessage[3]));
                conn.send(canvasInfo);
            }
            else if(clientMessage[1].matches("EmoteUsed")){
                //checking which player used the emote
                if(conn == gameLobbies.get(Integer.valueOf(clientMessage[2])).player1){
                    //sending emote message as string to player 2
                    gameLobbies.get(Integer.valueOf(clientMessage[2])).player2.send(clientMessage[3]);
                }
                else{
                    gameLobbies.get(Integer.valueOf(clientMessage[2])).player1.send(clientMessage[3]);
                }
            }
        }
        else if(clientMessage[0].matches("LobbyMessage")){
            if(clientMessage[1].matches("CreateLobby")){
                //creates a lobby and send the lobby info to client
                
                conn.send(createLobby(conn, clientMessage[2]));
            }
            else if(clientMessage[1].matches("JoinLobby")){
                conn.send(lobbyInfoToString(clientMessage[2], conn));
                gameLobbies.get(Integer.valueOf(clientMessage[2])).p2Name = clientMessage[3];
            }
            else if(clientMessage[1].matches("TerminateLobby")){
                //need to create a way on client side that checks every once in awhile if the lobby is still alive
                gameLobbies.remove(gameLobbies.get(Integer.valueOf(clientMessage[2])));
            }
        }
        else if(clientMessage[0].matches("FindMatch")){
            matchCreator(conn, clientMessage[1]);
        }
        else if(clientMessage[0].matches("Ready")){
           gameLobbies.get(Integer.valueOf(clientMessage[1])).readyCount++;
           if( gameLobbies.get(Integer.valueOf(clientMessage[1])).readyCount == 2){
               gameLobbies.get(Integer.valueOf(clientMessage[1])).player1.send("YourTurn");
               gameLobbies.get(Integer.valueOf(clientMessage[1])).player2.send("PartnerTurn");
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
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress());
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
    
    public String createLobby(WebSocket conn, String clientname){
        String[] lobbyCodes = new String[gameLobbies.size()];     
        for(int i = 0; i <= gameLobbies.size() - 1; i++){
            lobbyCodes[i] += gameLobbies.get(i)+"\n";
        }
        
        GameLobby newLobby = new GameLobby(lobbyCodes);
        newLobby.p1Name = clientname;
        newLobby.player1 = conn;
        gameLobbies.add(newLobby);
        newLobby.lobbyIndex = gameLobbies.indexOf(newLobby);
        System.out.println("New lobby created at index:"+newLobby.lobbyIndex);
        return "LobbyInfo/"+newLobby.lobbyToString();
    }
    
    public String lobbyInfoToString(String lobbyCode, WebSocket Conn){
        for(int i = 0; i <= gameLobbies.size() - 1; i++){
            if(gameLobbies.get(i).lobbyCode.matches(lobbyCode)){
                gameLobbies.get(i).player2 = Conn;
                gameLobbies.get(i).player1.send("Ready");
                return "LobbyInfo/"+gameLobbies.get(i).lobbyToString();            
            }
        }
        return "Error";  
    }
    
    public void matchCreator(WebSocket conn, String clientname){
        for(int i = 0; i <= gameQueue.size() - 1; i++){
            String lobbyInfo = createLobby(conn, clientname);
            conn.send(lobbyInfo);         
            String[] lobbyString = lobbyInfo.split("/");    
            gameLobbies.get(Integer.valueOf(lobbyString[1])).player2 = gameQueue.get(i).socket;
            gameLobbies.get(Integer.valueOf(lobbyString[1])).p2Name = gameQueue.get(i).name;
            gameQueue.get(i).socket.send(lobbyInfo);
            gameQueue.remove(i);
            return;
        }
        gameQueue.add(new Client(conn, clientname));
    }
    
    //In match server functions
    
    public String shapeDataToString(){
        return "lol";
    }
    

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
      
        
        String host = "172.31.45.56";
        int port = 8080;
        
        SimpleServer server = new SimpleServer(new InetSocketAddress(host, port));
    
        server.start();   
        
        while(true){
            
        }
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
}

