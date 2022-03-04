package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;

public class FindingMatch extends ScreenAdapter {

    public TextButton exitLobby;
    MultipleScenes game;
    BitmapFont font;
    Stage stage;
    public boolean matchFound = false;

    public FindingMatch(MultipleScenes game) {
        this.game = game;
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                Gdx.app.log("WS", "Got message: " + packet);

                String[] clientMessage = packet.split("/");
                if(clientMessage[0].matches("LobbyInfo")) {
                    System.out.println("joining match");
                    game.gameLobby = new GameLobby(clientMessage[2], Integer.valueOf(clientMessage[1]));
                    matchFound = true;
                    game.getSocket().send("joining match");
                }

                return FULLY_HANDLED;
            }
        };
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/font.fnt"),
        Gdx.files.internal("font/font.png"), false);

        game.getSocket().removeListener(game.listener);
        game.listener = getListener();
        game.getSocket().addListener(game.listener);
        game.getSocket().send("FindMatch/"+game.playerName);

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.graphics.setWindowedMode(360, 640);

        exitLobby = new TextButton("Stop searching", mySkin, "toggle");
        exitLobby.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 + 200, 150, 50);
        exitLobby.getLabel().setFontScale(0.6f, 0.6f);
        exitLobby.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                exitLobby.setText("Join Lobby");
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new HomeScreen(game));
                game.getSocket().send("LobbyMessage/TerminateLobby/"+game.gameLobby.lobbyIndex);
                return true;
            }
        });
        stage.addActor(exitLobby);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if(matchFound){
            game.setScreen(new LoadingScreen(game));
        }


        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        game.batch.begin();
        game.font.draw(game.batch, "Searching for another player..\n", Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() / 2);
        game.batch.end();

    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
}



