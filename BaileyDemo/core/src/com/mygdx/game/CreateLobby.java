package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class CreateLobby extends ScreenAdapter {

    //CreateLobby class which allows the player to create a lobby in the game

    private MultipleScenes game;
    private Stage stage;
    private BitmapFont font;
    private SelectBox<String> lobbyType;
    private SelectBox<String> time;
    private SelectBox<String> round;
    private SelectBox<String> topicType;
    private boolean moveToLobby = false;
    private ShapeRenderer shapeRenderer;

    private Texture tex;
    private Image image;
    private TextureRegion region;

    private TextureRegionDrawable up;
    private TextureRegionDrawable down;
    private TextureRegion buttonUp;
    private TextureRegion buttonDown;
    private Texture tex2;
    private ImageButton button;

    public CreateLobby(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/dbfont.fnt"),
                Gdx.files.internal("font/dbfont.png"), false);

        game.setListener(getListener());
        shapeRenderer = new ShapeRenderer();

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        //Add CreateRoom.png as the background
        tex = new Texture(Gdx.files.internal("image/CreateRoom.png"));
        region = new TextureRegion(tex,0,0,750,1334);
        image = new Image(region);
        image.setPosition(0,0);
        image.setSize(360 * (Gdx.graphics.getWidth() / 360),750 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(image);

        //Add Back Button, click it will move back to Lobby (Home Screen)
        tex2 = new Texture(Gdx.files.internal("button/BackButton.png"));
        TextureRegion[][] temp_0 = TextureRegion.split(tex2,210,60);
        buttonUp = temp_0[0][0];
        buttonDown = temp_0[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 0.5f, (Gdx.graphics.getHeight() / 20) * 18);
        button.setSize(105 * (Gdx.graphics.getWidth() / 360),30 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.getSocket().isOpen()) {
                    game.setScreen(new HomeScreen(game));
                }
                SoundManager.button.play();
            }
        });

        //Adding Create Button, click it will create a Game Room/Lobby as User required
        tex2 = new Texture(Gdx.files.internal("button/CreateButton.png"));
        TextureRegion[][] temp_2 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_2[0][0];
        buttonDown = temp_2[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 1.8f, (Gdx.graphics.getHeight() / 20) * 3);
        button.setSize(240 * (Gdx.graphics.getWidth() / 360),70 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                if(game.getSocket().isOpen() && lobbyType.getSelected() != null && lobbyType.getSelected() != "Lobby Type:") {

                    game.getSocket().send("LobbyMessage/CreateLobby/"+lobbyType.getSelected()+"/"+game.getAuthCode()+"/"+round.getSelected()+"/"+time.getSelected()+"/"+game.getPlayerName()+"/"+topicType.getSelected());
                }
            }
        });

        //Add Mode Intro Button, takes the player to the game mode info screen
        tex2 = new Texture(Gdx.files.internal("button/infoButton.png"));
        TextureRegion[][] temp_3 = TextureRegion.split(tex2,50,50);
        buttonUp = temp_3[0][0];
        buttonDown = temp_3[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 7, (Gdx.graphics.getHeight() / 20) * 16);
        button.setSize(30 * (Gdx.graphics.getWidth() / 360),30 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                game.setScreen(new modeIntroScreen(game));
            }
        });

        //Select Box for Game Mode
        lobbyType = new SelectBox<String>(mySkin);
        lobbyType.setItems("Regular", "One Line");
        //lobbyType.setName("Lobby Type:");
        lobbyType.setBounds((Gdx.graphics.getWidth() / 10) * 4, (Gdx.graphics.getHeight() / 20) * 15.6f, 100, 60);
        lobbyType.setColor(0.0f, 0.0f, 0.0f, 1.0f);
        lobbyType.setSelected("5");
        stage.addActor(lobbyType);

        ArrayList<String> data = new ArrayList<>() ;
        data.add("Random");
        FileHandle handle = Gdx.files.internal("textfiles/topicwords.txt");
        String text = handle.readString();
        String wordsArray[] = text.split("\\r?\\n");
        for(String word : wordsArray) {
            data.add(word);
        }
        String[] simpleArray = data.toArray(new String[]{});

        //Select Box for Topic
        topicType = new SelectBox<String>(mySkin);
        topicType.setItems(simpleArray);
        topicType.setBounds((Gdx.graphics.getWidth() / 10) * 4, (Gdx.graphics.getHeight() / 20) * 12.2f, 100, 60);
        topicType.setColor(0.0f, 0.0f, 0.0f, 1.0f);
        topicType.setSelected("5");
        stage.addActor(topicType);

        //Select Box for Time
        time = new SelectBox<String>(mySkin);
        time.setItems("10 sec", "15 sec", "20 sec", "30 sec", "1 min");
        time.setBounds((Gdx.graphics.getWidth() / 10) * 4, (Gdx.graphics.getHeight() / 20) * 8.9f, 100, 60);
        time.setColor(0.0f, 0.0f, 0.0f, 1.0f);
        time.setSelected("5");
        stage.addActor(time);

        //Select Box for Round
        round = new SelectBox<String>(mySkin);
        round.setItems("2", "4", "6", "8", "10");
        round.setBounds((Gdx.graphics.getWidth() / 10) * 4, (Gdx.graphics.getHeight() / 20) * 5.7f, 100, 60);
        round.setColor(0.0f, 0.0f, 0.0f, 1.0f);
        round.setSelected("5");
        stage.addActor(round);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        //If moveToLobby variable is true then move to the LobbyScreen
        if(moveToLobby){
            game.setScreen(new LobbyScreen(game));
        }

        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        shapeRenderer.rect((Gdx.graphics.getWidth() / 2 - 70), Gdx.graphics.getHeight() / 2 + 10, 125, 35);
        shapeRenderer.end();
        stage.act();

        stage.draw();
        game.getBatch().begin();

        //Check if client is disconnected, if so then display error message and attempt to reconnect
        if(game.getSocket().isClosed() && !game.getSocket().isConnecting()){
            game.getSocket().connect();
            font.draw( game.getBatch(), "CONNECTION LOST TO SERVER\n", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2);
        }

        game.getBatch().end();
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                String temp = packet;
                Gdx.app.log("WS", "Got message: " + packet);
                String[] serverMessage = packet.split("/");

                //If LobbyInfo packet is received, then set the lobby info and set moveToLobby as true
                if (serverMessage[0].matches("LobbyInfo")) {
                    CreateLobby(Integer.valueOf(serverMessage[1]), serverMessage[2]);
                    game.getGameLobby().setWordTopic(serverMessage[3]);
                    game.getGameLobby().setGameMode(serverMessage[4]);
                    game.getGameLobby().setTurnAmount(serverMessage[5]);
                    game.getGameLobby().setTurnTimer(serverMessage[6]);
                    moveToLobby = true;
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
        font.dispose();
        tex.dispose();
        tex2.dispose();
    }
}