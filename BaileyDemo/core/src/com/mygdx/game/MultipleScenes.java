package com.mygdx.game;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;

public class MultipleScenes extends Game {

    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    protected WebSocket socket;
    WebSocketListener listener;
    public GameLobby gameLobby;

    public WebSocket getSocket() {
        return socket;
    }

    public void setSocket(WebSocket socket) {
        this.socket = socket;
    }

    //need to add server connection + socket variabels to this class so it can be accessed all the time, also other shit

    @Override
    public void create () {
        socket = WebSockets.newSocket(WebSockets.toWebSocketUrl("192.168.1.71", 8080));
        socket.setSendGracefully(true);
        socket.connect();
        gameLobby = new GameLobby();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        setScreen(new TitleScreen(this));
    }

    @Override
    public void dispose () {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}