package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;

import java.awt.Button;

import java.awt.event.ActionListener;

import sun.awt.ExtendedKeyCodes;

public class HomeScreen extends ScreenAdapter {

    MultipleScenes game;
    TextButton joinLobby;
    TextButton findMatch;
    TextButton createLobby;
    Stage stage;
    Skin mySkin;
    public boolean moveToGame = false;
    float timer = 0.0f;
    boolean moveToLobby = false;
    boolean moveToMatchmaking = false;
    BitmapFont font;

    public HomeScreen(MultipleScenes game) {
        this.game = game;
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                Gdx.app.log("WS", "Got message: " + packet);
                System.out.println("called from homescreen");
                if(!packet.matches("Error")){
                    String[] clientMessage = packet.split("/");
                    if(clientMessage[0].matches("LobbyInfo")) {
                        game.gameLobby.lobbyCode = clientMessage[0];
                        game.gameLobby.lobbyIndex = Integer.valueOf(clientMessage[1]);
                        moveToLobby = true;
                        return FULLY_HANDLED;
                   }
                }
                return FULLY_HANDLED;
            }
        };
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/font.fnt"),
        Gdx.files.internal("font/font.png"), false);

        moveToGame = false;
        game.getSocket().removeListener(game.listener);
        game.listener = getListener();
        game.getSocket().addListener(game.listener);
        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.graphics.setWindowedMode(360, 640);

        final TextField textField = new TextField("Text field", mySkin);
        textField.setX(Gdx.graphics.getWidth() / 2 - 100);
        textField.setY(Gdx.graphics.getHeight() / 2 - 100);
        textField.setWidth(200);
        textField.setText("");
        textField.setHeight(25);
        textField.setVisible(false);

        textField.addListener(new InputListener(){

            @Override
            public boolean keyDown (InputEvent event, int keycode) {
                if(keycode == Input.Keys.ENTER){
                    textField.setVisible(false);
                    game.socket.send("LobbyMessage/JoinLobby/"+textField.getText()+"/"+game.playerName);
                }
                return false;
            }
        });

        joinLobby = new TextButton("Join Lobby", mySkin, "toggle");
        joinLobby.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 - 25, 150, 50);
        joinLobby.getLabel().setFontScale(0.6f, 0.6f);


        joinLobby.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                joinLobby.setText("Join Lobby");
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                textField.setVisible(true);
                return true;
            }
        });

        createLobby = new TextButton("Create Lobby", mySkin, "toggle");
        createLobby.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 + 50, 150, 50);
        createLobby.getLabel().setFontScale(0.6f, 0.6f);


        createLobby.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                createLobby.setText("Join Lobby");
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new LobbyScreen(game));
                return true;
            }
        });

        findMatch = new TextButton("Find match", mySkin, "toggle");
        findMatch.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 + 125, 150, 50);
        findMatch.getLabel().setFontScale(0.6f, 0.6f);


        findMatch.addListener(new InputListener(){

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                moveToMatchmaking = true;
                return true;
            }
        });

        stage.addActor(findMatch);
        stage.addActor(textField);
        stage.addActor(createLobby);
        stage.addActor(joinLobby);
        Gdx.input.setInputProcessor(stage);
    }



    @Override
    public void render(float delta) {
        if(moveToLobby){
            game.setScreen(new LoadingScreen(game));
        }
        if(moveToMatchmaking){
            game.setScreen(new FindingMatch(game));
        }
        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        game.batch.begin();
        font.draw(game.batch, "Welcome to Draw Buddy\n", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2 + 150);
        game.batch.end();
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
}