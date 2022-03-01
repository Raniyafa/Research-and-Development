package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;

import java.awt.Button;

public class LobbyScreen extends ScreenAdapter {

    MultipleScenes game;
    TextButton exitLobby;
    Stage stage;
    Skin mySkin;

    public LobbyScreen(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show(){
        game.getSocket().removeListener(game.listener);
        game.listener = getListener();
        game.getSocket().addListener(game.listener);
        game.getSocket().send("LobbyMessage/CreateLobby");

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        Gdx.graphics.setWindowedMode(360, 640);

        exitLobby = new TextButton("Return to Main Menu", mySkin, "toggle");
        exitLobby.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2, 150, 50);
        exitLobby.getLabel().setFontScale(0.6f, 0.6f);


        exitLobby.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                exitLobby.setText("Join Lobby");
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new TitleScreen(game));
                return true;
            }
        });
        stage.addActor(exitLobby);
        Gdx.input.setInputProcessor(stage);
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Draw Buddy\n", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 100);
        game.batch.end();

    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                String temp = packet;
                Gdx.app.log("WS", "Got message: " + packet);

                if(temp.contains("Ready")){
                    System.out.println("Matches ready");
                    game.setScreen(new LoadingScreen(game));

                }
                if (packet.contains("LobbyInfo")) {
                    String[] serverMessage = packet.split("/");
                    CreateLobby(serverMessage[1], Integer.valueOf(serverMessage[2]));
                }

                return FULLY_HANDLED;
            }
        };
    }

    public void CreateLobby(String code, int id){
        game.gameLobby = new GameLobby(code, id);
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
}