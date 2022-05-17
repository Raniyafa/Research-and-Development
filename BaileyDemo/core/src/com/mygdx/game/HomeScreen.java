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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.awt.Button;

import java.awt.event.ActionListener;

import sun.awt.ExtendedKeyCodes;

public class HomeScreen extends ScreenAdapter {

    private MultipleScenes game;
//    private TextButton joinLobby;
//    private TextButton findMatch;
//    private TextButton createLobby;
    private Stage stage;
    private Skin mySkin;
    private boolean moveToGame = false;
    private float timer = 0.0f;
    public boolean moveToLobby = false;
    private boolean moveToMatchmaking = false;
    private BitmapFont font;
    private boolean moveToCreateLobby = false;

    //add IMG Background
    private Texture tex;
    private Image image;
    private TextureRegion region;

    //add image_button
    private TextureRegionDrawable up;
    private TextureRegionDrawable down;
    private TextureRegion buttonUp;
    private TextureRegion buttonDown;
    private Texture tex2;
    private ImageButton button;

    public HomeScreen(MultipleScenes game) {
        this.game = game;
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                Gdx.app.log("WS Homescreen", "Got message: " + packet);

                    String[] clientMessage = packet.split("/");
                    if(clientMessage[0].matches("LobbyInfo")) {
                        game.setGameLobby(new GameLobby(clientMessage[2], Integer.valueOf(clientMessage[1])));
                        game.getGameLobby().setWordTopic(clientMessage[3]);
                        game.getGameLobby().setGameMode(clientMessage[4]);
                        game.getGameLobby().setTurnAmount(clientMessage[5]);
                        game.getGameLobby().setTurnTimer(clientMessage[6]);
                        moveToLobby = true;
                   }

                return FULLY_HANDLED;
            }
        };
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/dbfont.fnt"),
        Gdx.files.internal("font/dbfont.png"), false);

        moveToGame = false;
        game.setListener(getListener());
        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        final TextField textField = new TextField("Lobby Code:", mySkin);
        textField.setX(Gdx.graphics.getWidth() / 2 - 100);
        textField.setY(Gdx.graphics.getHeight() / 2 - 100);
        //Isaac add-img_background
        tex = new Texture(Gdx.files.internal("image/Lobby.png"));
        region = new TextureRegion(tex,0,0,750,1334);
        image = new Image(region);
        image.setPosition(0,0);
        image.setSize(360 * (Gdx.graphics.getWidth() / 360),750 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(image);

       // final TextField textField = new TextField("Lobby Code:", mySkin);
        textField.setX(Gdx.graphics.getWidth() / 2 - 100);
//        textField.setY(Gdx.graphics.getHeight() / 2 - 100);
        textField.setY(100);
        textField.setWidth(200);
        textField.setText("");
        textField.setHeight(50);
        textField.setVisible(false);
        textField.setText("");

        textField.addListener(new InputListener(){

            @Override
            public boolean keyDown (InputEvent event, int keycode) {
                if(keycode == Input.Keys.ENTER){
                    textField.setVisible(false);
                    game.getSocket().send("LobbyMessage/JoinLobby/"+textField.getText()+"/"+game.getPlayerName()+"/"+game.getAuthCode());
                }
                return false;
            }
        });

//        joinLobby = new TextButton("Join Lobby", mySkin, "toggle");
//        joinLobby.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 - 25, 150, 50);
//        joinLobby.getLabel().setFontScale(0.6f, 0.6f);
//        joinLobby.addListener(new InputListener(){
//
//            @Override
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                joinLobby.setText("Join Lobby");
//            }
//            @Override
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                if(!textField.isVisible()){
//                    textField.setVisible(true);
//                }
//                else{
//                    textField.setVisible(false);
//                    textField.setText("");
//                }
//                return true;
//            }
//        });
//
//        createLobby = new TextButton("Create Lobby", mySkin, "toggle");
//        createLobby.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 + 50, 150, 50);
//        createLobby.getLabel().setFontScale(0.6f, 0.6f);
//        createLobby.addListener(new InputListener(){
//
//            @Override
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                createLobby.setText("Join Lobby");
//            }
//            @Override
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                moveToCreateLobby = true;
//                return true;
//            }
//        });
//
//        findMatch = new TextButton("Find match", mySkin, "toggle");
//        findMatch.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 + 125, 150, 50);
//        findMatch.getLabel().setFontScale(0.6f, 0.6f);
//
//
//        findMatch.addListener(new InputListener(){
//
//            @Override
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                moveToMatchmaking = true;
//                return true;
//            }
//        });
//
//        stage.addActor(textField);


        //Setting Icon
        tex2 = new Texture(Gdx.files.internal("button/SettingButton.png"));
        TextureRegion[][] temp = TextureRegion.split(tex2,85,85);
        buttonUp = temp[0][0];
        buttonDown = temp[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth() / 2 + 115,Gdx.graphics.getHeight() / 2 + 250);
        button.setSize(30,30);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingMenu(game));
                SoundManager.button.play();
                //SoundManager.background.dispose();
            }
        });

        //Back Icon
//        tex2 = new Texture(Gdx.files.internal("button/BackButton.png"));
//        TextureRegion[][] temp_0 = TextureRegion.split(tex2,210,60);
//        buttonUp = temp_0[0][0];
//        buttonDown = temp_0[0][1];
//        up = new TextureRegionDrawable(buttonUp);
//        down = new TextureRegionDrawable(buttonDown);
//        button = new ImageButton(up,down);
//        button.setPosition(Gdx.graphics.getWidth()/2 - 180,Gdx.graphics.getHeight() / 2 + 270);
//        button.setSize(105,30);
//        stage.addActor(button);
//        button.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(new LoginScreen(game));
//                SoundManager.button.play();
//                SoundManager.background.dispose();
//            }
//        });

        //Quick Play Button
        tex2 = new Texture(Gdx.files.internal("button/QuickPlayButton.png"));
        TextureRegion[][] temp_1 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_1[0][0];
        buttonDown = temp_1[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth() / 2 - 120,Gdx.graphics.getHeight()/2 + 50);
        stage.addActor(button);
        button.setSize(240,70);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                moveToMatchmaking = true;
                SoundManager.button.play();
//                game.setScreen(new QuickPlayMenu(game));
            }
        });


        //Create Lobby Button
        tex2 = new Texture(Gdx.files.internal("button/CreateRoomButton.png"));
        TextureRegion[][] temp_2 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_2[0][0];
        buttonDown = temp_2[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth() / 2 - 120,Gdx.graphics.getHeight()/2 - 50);
        button.setSize(240,70);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                moveToCreateLobby = true;
                SoundManager.button.play();
            }
        });


        //Game PIN Button
        tex2 = new Texture(Gdx.files.internal("button/GamePINButton.png"));
        TextureRegion[][] temp_3 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_3[0][0];
        buttonDown = temp_3[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth() / 2 - 120,Gdx.graphics.getHeight()/2 - 150);
        button.setSize(240,70);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                if(!textField.isVisible()) {
                    textField.setVisible(true);
                }
                else{
                    textField.setVisible(false);
                    textField.setText("");
                }
            }
        });

//        stage.addActor(findMatch);
        stage.addActor(textField);
//        stage.addActor(createLobby);
//        stage.addActor(joinLobby);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if(moveToCreateLobby){
            game.setScreen(new CreateLobby(game));
        }
        if (moveToLobby) {
            System.out.println("Changing to loading screen");
            game.setScreen(new LoadingScreen(game));
        }
        game.getBatch().begin();
        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

        if(game.getSocket().isOpen()) {
            if (moveToLobby) {
                game.setScreen(new LoadingScreen(game));
            }
            if (moveToMatchmaking) {

             //   game.setScreen(new FindingMatch(game));
                game.setScreen(new QuickPlayMenu(game));
            }
            font.draw(game.getBatch(), "Hello "+game.getPlayerName()+"!", Gdx.graphics.getWidth() / 2 - 130, Gdx.graphics.getHeight() / 2 + 200);
        }
        else {
            game.getSocket().connect();
            font.draw(game.getBatch(), "CONNECTION LOST TO SERVER\nATTEMPTING TO RECONNECT..", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2);
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
        mySkin.dispose();
        font.dispose();
    }
}