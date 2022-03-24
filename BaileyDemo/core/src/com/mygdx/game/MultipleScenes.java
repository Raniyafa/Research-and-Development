package com.mygdx.game;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;

import java.net.URI;
import java.net.URISyntaxException;

public class MultipleScenes extends Game {

    private BitmapFont font;

    private WebSocket socket;
    private WebSocketListener listener;

    private GameLobby gameLobby;

    private String playerName;
    private SpriteBatch batch;

    public WebSocket getSocket() {
        return socket;
    }

    public void setSocket(WebSocket socket) {
        this.socket = socket;
    }

    //need to add server connection + socket variables to this class so it can be accessed all the time, also other shit

    @Override
    public void create () {


        // socket = WebSockets.newSocket(WebSockets.toWebSocketUrl("drawbuddygame.co.vu", 3000));

        socket = WebSockets.newSocket(WebSockets.toSecureWebSocketUrl("drawbuddygame.co.vu", 8080));

        socket.setSendGracefully(true);


            try {
                socket.connect();
            } catch (Exception e) {
                System.out.println("Exception: " + e);
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
    }

    public WebSocketListener getListener() {
        return listener;
    }

    public void setListener(WebSocketListener listener) {
        this.listener = listener;
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

}