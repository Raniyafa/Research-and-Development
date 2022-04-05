package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;

public class FindingMatch extends ScreenAdapter {

    private TextButton exitLobby;
    private MultipleScenes game;
    private BitmapFont font;
    private Stage stage;
    private boolean matchFound = false;

    private SpriteBatch batch;

    public FindingMatch(MultipleScenes game) {
        this.game = game;
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                Gdx.app.log("WS", "Got message: " + packet);

                //String received from server split by each '/'
                String[] clientMessage = packet.split("/");

                //If LobbyInfo string is found then it will be assigned to the client as the lobby
                if(clientMessage[0].matches("LobbyInfo")) {
                    System.out.println("joining match");
                    game.setGameLobby(new GameLobby(clientMessage[2], Integer.valueOf(clientMessage[1])));
                    game.getGameLobby().setWordTopic(clientMessage[3]);
                    game.getSocket().send("joining match");
                    matchFound = true;
                }
                return FULLY_HANDLED;
            }
        };
    }

    @Override
    public void show(){
        //Creating the font
        font = new BitmapFont(Gdx.files.internal("font/font.fnt"),
        Gdx.files.internal("font/font.png"), false);

        //Adding WebSocket listener for this class
        game.getSocket().removeListener(game.getListener());
        game.setListener(getListener());
        game.getSocket().addListener(game.getListener());
        game.getSocket().send("FindMatch/"+game.getPlayerName());

        //Creating stage and setting the skin for the UI
        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        //Create the exit to main screen button, also adding the listener which controls what happens when you interact with the button
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
                if(game.getGameLobby().getLobbyIndex() != -1) {
                    game.getSocket().send("LobbyMessage/TerminateLobby/" + game.getGameLobby().getLobbyIndex());
                }
                else{
                    game.getSocket().send("ReturnToMain");
                }

                game.setScreen(new HomeScreen(game));
                return true;
            }
        });
        stage.addActor(exitLobby);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        //If a match is found then switch to the loading screen
        if(matchFound){
            game.setScreen(new LoadingScreen(game));
        }

        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        game.getBatch().begin();

        //Disconnection handler
        if(game.getSocket().isOpen()) {
            font.draw( game.getBatch(), "Searching for another player..\n", 0, Gdx.graphics.getHeight() / 2);
        }
        else if(!game.getSocket().isConnecting()){
            game.getSocket().connect();
            font.draw(batch, "CONNECTION LOST TO SERVER\n", 0,  Gdx.graphics.getHeight() / 2);
        }
        game.getBatch().end();

    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose(){
        game.dispose();
        stage.dispose();
        font.dispose();
    }
}
