package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;

public class FindingMatch extends ScreenAdapter {

    private TextButton exitLobby;
    private MultipleScenes game;
    private BitmapFont font;
    private Stage stage;
    private boolean matchFound = false;
    private float disconnectionTimer = 0.0f;
    private SpriteBatch batch;

    private Texture tex;
    private Image image;
    private TextureRegion region;

    private TextureRegionDrawable up;
    private TextureRegionDrawable down;
    private TextureRegion buttonUp;
    private TextureRegion buttonDown;
    private Texture tex2;
    private ImageButton button;

    private boolean standardMode;

    public FindingMatch(MultipleScenes game, boolean standard) {
        this.game = game;
        standardMode = standard;
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                Gdx.app.log("WS Finding match", "Got message: " + packet);

                //String received from server split by each '/'
                String[] clientMessage = packet.split("/");

                //If LobbyInfo string is found then it will be assigned to the client as the lobby
                if(clientMessage[0].matches("LobbyInfo")) {
                    System.out.println("joining match");
                    game.setGameLobby(new GameLobby(clientMessage[2], Integer.valueOf(clientMessage[1])));
                    game.getGameLobby().setWordTopic(clientMessage[3]);
                    game.getGameLobby().setGameMode(clientMessage[4]);
                    game.getGameLobby().setTurnAmount(clientMessage[5]);
                    game.getGameLobby().setTurnTimer(clientMessage[6]);
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
        font = new BitmapFont(Gdx.files.internal("font/dbfont.fnt"),
        Gdx.files.internal("font/dbfont.png"), false);

        if(standardMode){
            game.getSocket().send("FindMatch/"+game.getPlayerName()+"/"+game.getAuthCode()+"/Regular");
        }
        else {
            game.getSocket().send("FindMatch/" + game.getPlayerName() + "/" + game.getAuthCode() + "/One Line");
        }

        //Adding WebSocket listener for this class
        game.setListener(getListener());
        //game.getSocket().send("FindMatch/"+game.getPlayerName()+"/"+game.getAuthCode());

        //Creating stage and setting the skin for the UI
        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        //Adding Background Img
        tex = new Texture(Gdx.files.internal("image/waiting2.png"));
        region = new TextureRegion(tex,0,0,750,1334);
        image = new Image(region);
        image.setPosition(0,0);
        image.setSize(360 * (Gdx.graphics.getWidth() / 360),750 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(image);

        //Adding Cancel Button to replace ExitButton below
        tex2 = new Texture(Gdx.files.internal("button/CancelButton.png"));
        TextureRegion[][] temp = TextureRegion.split(tex2,480,140);
        buttonUp = temp[0][0];
        buttonDown = temp[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth() / 2 - 120,Gdx.graphics.getHeight()/2 - 200);
        button.setSize(240,70);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.getGameLobby().getLobbyIndex() != -1) {
                    game.getSocket().send("LobbyMessage/TerminateLobby/" + game.getGameLobby().getLobbyIndex());
                }
                else{
                    game.getSocket().send("ReturnToMain");
                }
                SoundManager.button.play();
                game.setScreen(new HomeScreen(game));
            }
        });
        stage.addActor(button);

        //Create the exit to main screen button, also adding the listener which controls what happens when you interact with the button
//        exitLobby = new TextButton("Stop searching", mySkin, "toggle");
//        exitLobby.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 + 200, 150, 50);
//        exitLobby.getLabel().setFontScale(0.6f, 0.6f);
//        exitLobby.addListener(new InputListener(){
//
//            @Override
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                exitLobby.setText("Join Lobby");
//            }
//            @Override
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                if(game.getGameLobby().getLobbyIndex() != -1) {
//                    game.getSocket().send("LobbyMessage/TerminateLobby/" + game.getGameLobby().getLobbyIndex());
//                }
//                else{
//                    game.getSocket().send("ReturnToMain");
//                }
//
//                game.setScreen(new HomeScreen(game));
//                return true;
//            }
//        });
//        stage.addActor(exitLobby);
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
            //font.draw( game.getBatch(), "Searching for another player..\n", 0, Gdx.graphics.getHeight() / 2);
        }
        else if(!game.getSocket().isConnecting()){
            game.setScreen(new HomeScreen(game));
            //add server code that remove person from queue if dc
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
