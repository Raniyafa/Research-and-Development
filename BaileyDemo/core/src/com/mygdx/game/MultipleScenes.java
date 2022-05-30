package com.mygdx.game;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;

import java.net.URI;
import java.net.URISyntaxException;

public class MultipleScenes extends Game {

    //Initial class which implements the Game object and holds information which needs to be accessed by multiple classes
    //such as a reference to the server, gamelobby, playername and authcode

    private BitmapFont font;

    private WebSocket socket;
    private WebSocketListener listener;

    private GameLobby gameLobby;

    private String playerName;
    private SpriteBatch batch;

    private String authCode = "";

    public String socketException = "";

    ShapeRenderer shapeRenderer;

    public WebSocket getSocket() {
        return socket;
    }

    public void setSocket(WebSocket socket) {
        this.socket = socket;
    }

    @Override
    public void create () {

        //non secure wss testing
        // socket = WebSockets.newSocket(WebSockets.toWebSocketUrl("drawbuddygame.co.vu", 3000));

        //aws online testing html
        //socket = WebSockets.newSocket(WebSockets.toSecureWebSocketUrl("drawbuddygame.co.vu", 8080));

        //aws online testing desktop
        //socket = WebSockets.newSocket(WebSockets.toWebSocketUrl("52.62.8.106", 8080));

        //android testing
        //socket = WebSockets.newSocket(WebSockets.toWebSocketUrl("127.0.0.1", 8080));

        //pc localhost testing
        socket = WebSockets.newSocket(WebSockets.toWebSocketUrl("localhost", 8080));

        socket.setSendGracefully(true);

        try {
            socket.connect();
        } catch (Exception e) {
            socketException = e.toString();
            System.out.println("Error connecting to the server.\nException Message: " + e);
        }

        gameLobby = new GameLobby();

        batch = new SpriteBatch();
        font = new BitmapFont();

        setScreen(new LoginScreen(this));
    }

    @Override
    public void dispose () {
        font.dispose();
        batch.dispose();
        Gdx.app.exit();
        font.dispose();
        socket.close();
    }

    public void setListener(WebSocketListener listener) {
        socket.removeListener(this.listener);
        this.listener = listener;
        socket.addListener(listener);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public GameLobby getGameLobby() {
        return gameLobby;
    }

    public void setGameLobby(GameLobby gameLobby) {
        this.gameLobby = gameLobby;
    }


    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                String temp = packet;
                Gdx.app.log("WS", "Got message: " + packet);
                String[] serverMessage = packet.split("/");

                if (serverMessage[0].matches("AuthCode")) {
                    setAuthCode(serverMessage[1]);
                }
                return FULLY_HANDLED;
            }
        };
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
