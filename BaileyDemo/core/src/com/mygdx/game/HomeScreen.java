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

    //HomeScreen class which is the Home page or main menu for the game

    private MultipleScenes game;
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

                //The client can receive lobby info while in the home screen if they successfully enter a valid lobby code
                //the listener will then set the lobby info and switch to the lobby screen
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

        //Add Lobby.png as the background
        tex = new Texture(Gdx.files.internal("image/Lobby.png"));
        region = new TextureRegion(tex,0,0,750,1334);
        image = new Image(region);
        image.setPosition(0,0);
        image.setSize(360 * (Gdx.graphics.getWidth() / 360),750 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(image);

        //Add Text Field for the Game PIN function
       // final TextField textField = new TextField("Lobby Code:", mySkin);
        textField.setX((Gdx.graphics.getWidth() / 10) * 2.5f);
//        textField.setY(Gdx.graphics.getHeight() / 2 - 100);
        textField.setY((Gdx.graphics.getHeight() / 20) * 3);
        textField.setWidth(200 * (Gdx.graphics.getWidth() / 360));
        textField.setText("");
        textField.setHeight(50 * (Gdx.graphics.getHeight() / 640));
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

        //Add Setting Button, click it will move to Setting Menu Scene
        tex2 = new Texture(Gdx.files.internal("button/SettingButton.png"));
        TextureRegion[][] temp = TextureRegion.split(tex2,85,85);
        buttonUp = temp[0][0];
        buttonDown = temp[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 8f, (Gdx.graphics.getHeight() / 20) * 17.5f);
        button.setSize(30 * (Gdx.graphics.getWidth() / 360),30 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingMenu(game));
                SoundManager.button.play();
//                SoundManager.background.dispose();
            }
        });

        //Add Quick Play Button, click it will move to Quick Play Scene
        tex2 = new Texture(Gdx.files.internal("button/QuickPlayButton.png"));
        TextureRegion[][] temp_1 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_1[0][0];
        buttonDown = temp_1[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 1.8f, (Gdx.graphics.getHeight() / 20) * 10);
        stage.addActor(button);
        button.setSize(240 * (Gdx.graphics.getWidth() / 360),140 * (Gdx.graphics.getHeight() / 640));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                moveToMatchmaking = true;
                SoundManager.button.play();
//                game.setScreen(new QuickPlayMenu(game));
            }
        });


        //Add Create Lobby Button, click it will move to Create Room Scene
        tex2 = new Texture(Gdx.files.internal("button/CreateRoomButton.png"));
        TextureRegion[][] temp_2 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_2[0][0];
        buttonDown = temp_2[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 1.8f, (Gdx.graphics.getHeight() / 20) * 8);
        button.setSize(240 * (Gdx.graphics.getWidth() / 360),70 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                moveToCreateLobby = true;
                     SoundManager.button.play();
            }
        });


        //Add Game PIN Button, click it will open the Text Field
        tex2 = new Texture(Gdx.files.internal("button/GamePINButton.png"));
        TextureRegion[][] temp_3 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_3[0][0];
        buttonDown = temp_3[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 1.8f, (Gdx.graphics.getHeight() / 20) * 5);
        button.setSize(240 * (Gdx.graphics.getWidth() / 360),70 * (Gdx.graphics.getHeight() / 640));
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
        //Move to create lobby screen if moveToCreateLobby is true
        if(moveToCreateLobby){
            game.setScreen(new CreateLobby(game));
        }
        //Move to lobby if moveToLobby is true
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
            font.draw(game.getBatch(), "Hello "+game.getPlayerName()+"!", (Gdx.graphics.getWidth() / 10) * 1.5f, (Gdx.graphics.getHeight() / 20) * 16);
        }
        else {
            game.getSocket().connect();
            font.draw(game.getBatch(), "CONNECTION LOST TO SERVER\nATTEMPTING TO RECONNECT..", (Gdx.graphics.getWidth() / 10) * 0.5f, (Gdx.graphics.getHeight() / 20) * 2);
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
        tex.dispose();
        tex2.dispose();
    }
}