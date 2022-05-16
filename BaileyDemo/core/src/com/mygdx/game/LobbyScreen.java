package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

public class LobbyScreen extends ScreenAdapter {

    private MultipleScenes game;
    private TextButton exitLobby;
    private Stage stage;
    private Skin mySkin;
    private BitmapFont font;

    private Texture tex;
    private Image image;
    private TextureRegion region;

    private TextureRegionDrawable up;
    private TextureRegionDrawable down;
    private TextureRegion buttonUp;
    private TextureRegion buttonDown;
    private Texture tex2;
    private ImageButton button;

    private boolean moveToGame = false;

    public LobbyScreen(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/dbfont.fnt"), Gdx.files.internal("font/dbfont.png"), false);

        game.setListener(getListener());
       // game.getSocket().send("LobbyMessage/CreateLobby/"+game.getPlayerName());

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        Gdx.graphics.setWindowedMode(360, 640);

        //Adding Background Img
        tex = new Texture(Gdx.files.internal("image/waiting.png"));
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
        button.setPosition(Gdx.graphics.getWidth() / 2 - 120,Gdx.graphics.getHeight()/2 - 230);
        button.setSize(240,70);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.getSocket().isOpen()) {
                    game.setScreen(new HomeScreen(game));
                    game.getSocket().send("LobbyMessage/TerminateLobby/" + game.getGameLobby().getLobbyIndex());
                }
                SoundManager.button.play();
            }
        });
        stage.addActor(button);

//        exitLobby = new TextButton("Return to Main Menu", mySkin, "toggle");
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
//                if(game.getSocket().isOpen()) {
//                    game.setScreen(new HomeScreen(game));
//                    game.getSocket().send("LobbyMessage/TerminateLobby/" + game.getGameLobby().getLobbyIndex());
//                }
//                return true;
//            }
//        });
//        stage.addActor(exitLobby);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (moveToGame) {
            game.setScreen(new LoadingScreen(game));
        }

        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        game.getBatch().begin();
        if(game.getSocket().isOpen()) {
            font.draw( game.getBatch(), game.getGameLobby().getLobbyCode(), Gdx.graphics.getWidth()/2 - 50, Gdx.graphics.getHeight() / 2 - 90);
            //font.draw( game.getBatch(), "Waiting for other player..\n", 0, Gdx.graphics.getHeight() / 2 - 50);
        }
        else{
            game.setScreen(new HomeScreen(game));
        }
        game.getBatch().end();
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                String temp = packet;
                Gdx.app.log("WS Lobbyscreen", "Got message: " + packet);

                if(temp.contains("Ready")){
                    System.out.println("Match ready");
                    moveToGame = true;
                }
                if (packet.contains("LobbyInfo")) {
                    String[] serverMessage = packet.split("/");
                    CreateLobby(Integer.valueOf(serverMessage[1]), serverMessage[2]);
                }
                return FULLY_HANDLED;
            }
        };
    }

    public void CreateLobby(int id, String code){
        game.setGameLobby(new GameLobby(code, id));
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose(){
        game.dispose();
        stage.dispose();
        mySkin.dispose();
        font.dispose();
    }
}