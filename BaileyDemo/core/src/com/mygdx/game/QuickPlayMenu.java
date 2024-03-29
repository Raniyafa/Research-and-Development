package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.Preferences;

public class QuickPlayMenu extends ScreenAdapter{

    //QuickPlayMenu class which allows the player to choose which game mode they want to queue up for

    private MultipleScenes game;
    private Stage stage;
    private Skin mySkin;

    public boolean moveToLobby = false;
    private boolean moveToMatchmaking = false;
    private boolean matchFound = false;
    private boolean moveToLoading = false;
    private boolean standardMode = false;
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

//    private Viewport viewport;
//    private Camera camera;

    public QuickPlayMenu(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show(){

        //Scale the UI size
//        camera = new PerspectiveCamera();
//        viewport = new FitViewport(360, 640);

        stage = new Stage(new ScreenViewport());
//        stage = new Stage(new StretchViewport(360, 640));

        //Add Lobby.png as the background
        tex = new Texture(Gdx.files.internal("image/Lobby.png"));
//        region = new TextureRegion(tex,0,0,750,1334);
        region = new TextureRegion(tex);
        image = new Image(region);
        image.setPosition(0,0);
//        image.setSize(360 * (Gdx.graphics.getWidth() / 360),750 * (Gdx.graphics.getHeight() / 640));
        image.setSize(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        stage.addActor(image);

        //Add Mode Intro Button, takes the player to the game mode info screen
        tex2 = new Texture(Gdx.files.internal("button/infoButton.png"));
        TextureRegion[][] temp_3 = TextureRegion.split(tex2,50,50);
        buttonUp = temp_3[0][0];
        buttonDown = temp_3[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 4.5f,(Gdx.graphics.getHeight()/ 20) * 7);
        button.setSize(30 * (Gdx.graphics.getWidth() / 360),30 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                game.setScreen(new modeIntroScreen(game));
            }
        });

        //Add Setting Icon, takes the client to the setting menu
        tex2 = new Texture(Gdx.files.internal("button/SettingButton.png"));
        TextureRegion[][] temp = TextureRegion.split(tex2,85,85);
        buttonUp = temp[0][0];
        buttonDown = temp[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 8f, (Gdx.graphics.getHeight() / 20) * 17.5f);
        button.setSize(30 * (Gdx.graphics.getWidth() / 360),30 * (Gdx.graphics.getHeight() / 640));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingMenu(game));
                SoundManager.button.play();
            }
        });

        stage.addActor(button);

        //Back Icon, tells server to destroy gamelobby and returns client to main menu
        tex2 = new Texture(Gdx.files.internal("button/BackButton.png"));
        TextureRegion[][] temp_0 = TextureRegion.split(tex2,210,60);
        buttonUp = temp_0[0][0];
        buttonDown = temp_0[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 0.5f, (Gdx.graphics.getHeight() / 20) * 18);
        button.setSize(105 * (Gdx.graphics.getWidth() / 360),30 * (Gdx.graphics.getHeight() / 640));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.getGameLobby().getLobbyIndex() != -1) {
                    game.getSocket().send("LobbyMessage/TerminateLobby/" + game.getGameLobby().getLobbyIndex());
                }
                else{
                    game.getSocket().send("ReturnToMain");
                }
                game.setScreen(new HomeScreen(game));
                SoundManager.button.play();
            }
        });

        stage.addActor(button);

        //Add Standard Mode Button, click it the game room(Standard Mode) will be create, move to Loading Scene
        tex2 = new Texture(Gdx.files.internal("button/StandardButton.png"));
        TextureRegion[][] temp_1 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_1[0][0];
        buttonDown = temp_1[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 1.8f, (Gdx.graphics.getHeight() / 20) * 12);
        button.setSize(240 * (Gdx.graphics.getWidth() / 360),70 * (Gdx.graphics.getHeight() / 640));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                standardMode = true;
                moveToLoading = true;
                SoundManager.button.play();
            }
        });

        stage.addActor(button);

        //Add Single Line Mode Button, click it the game room(Single Line Mode) will be create, move to Loading Scene
        tex2 = new Texture(Gdx.files.internal("button/slModeButton.png"));
        TextureRegion[][] temp_2 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_2[0][0];
        buttonDown = temp_2[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 1.8f, (Gdx.graphics.getHeight() / 20) * 9);
        button.setSize(240 * (Gdx.graphics.getWidth() / 360),70 * (Gdx.graphics.getHeight() / 640));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                standardMode = false;
                moveToLoading = true;
                SoundManager.button.play();
            }
        });

        stage.addActor(button);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        //Scale the UI size
//        stage.getViewport().update(360, 640, true);

        if(moveToLoading){
            game.setScreen(new FindingMatch(game, standardMode));
        }

        game.getBatch().begin();
        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
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
        tex.dispose();
        tex2.dispose();
    }
}
