package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
import com.badlogic.gdx.Preferences;

public class SettingMenu extends ScreenAdapter {

    //SettingMenu class which allows the player to change the game options

    private MultipleScenes game;
    private Stage stage;
    private Skin skin;


    private Texture tex;
    private Image image;
    private TextureRegion region;

    private TextureRegionDrawable up;
    private TextureRegionDrawable down;
    private TextureRegion buttonUp;
    private TextureRegion buttonDown;
    private Texture tex2;
    private ImageButton button;
    private ImageButton backgroundMusicButton_On;
    private ImageButton backgroundMusicButton_Off;
    private ImageButton soundEffectButton_On;
    private ImageButton soundEffectButton_Off;



    public SettingMenu(MultipleScenes game) {
        this.game = game;


    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));

        //Add setting.png as the background
        tex = new Texture(Gdx.files.internal("image/setting.png"));
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
                //back to last activity
                SoundManager.button.play();
                game.setScreen(new HomeScreen(game));
            }
        });

        //Add Game Rules Button, click it the Rule Intro will show up
        tex2 = new Texture(Gdx.files.internal("button/RulesButton.png"));
        TextureRegion[][] temp_3 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_3[0][0];
        buttonDown = temp_3[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 3.4f, (Gdx.graphics.getHeight() / 20) * 3.5f);
        button.setSize(120 * (Gdx.graphics.getWidth() / 360),35 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                game.setScreen(new gameRuleScreen(game));
            }
        });

        //Add Volume Button(Off) show the Background Music is ON, click it the Background Music will turn OFF
        tex2 = new Texture(Gdx.files.internal("button/VolumeButton_Off.png"));
        TextureRegion[][] temp_4 = TextureRegion.split(tex2,50,50);
        buttonUp = temp_4[0][0];
        buttonDown = temp_4[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        backgroundMusicButton_On = new ImageButton(up,down);
        backgroundMusicButton_On.setPosition((Gdx.graphics.getWidth() / 10) * 5, (Gdx.graphics.getHeight() / 20) * 10);
        backgroundMusicButton_On.setSize(100 * (Gdx.graphics.getWidth() / 360),100 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(backgroundMusicButton_On);
        backgroundMusicButton_On.setVisible(false);
        backgroundMusicButton_On.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.background.setVolume(0.5f);
                SoundManager.button.play();
                backgroundMusicButton_On.setVisible(false);
                backgroundMusicButton_Off.setVisible(true);
            }
        });

        //Add Volume Button(On) show the Background Music is OFF, click it the Background Music will turn ON
        tex2 = new Texture(Gdx.files.internal("button/VolumeButton_On.png"));
        TextureRegion[][] temp_5 = TextureRegion.split(tex2,50,50);
        buttonUp = temp_5[0][0];
        buttonDown = temp_5[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        backgroundMusicButton_Off = new ImageButton(up,down);
        backgroundMusicButton_Off.setPosition((Gdx.graphics.getWidth() / 10) * 5, (Gdx.graphics.getHeight() / 20) * 10);
        backgroundMusicButton_Off.setSize(100 * (Gdx.graphics.getWidth() / 360),100 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(backgroundMusicButton_Off);
        backgroundMusicButton_Off.setVisible(true);
        backgroundMusicButton_Off.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.background.setVolume(0.0f);
                SoundManager.button.play();
                backgroundMusicButton_On.setVisible(true);
                backgroundMusicButton_Off.setVisible(false);
            }
        });

        //Add Volume Button(On) show the Sound Effect is ON, click it the Sound Effect will turn OFF
        tex2 = new Texture(Gdx.files.internal("button/VolumeButton_Off.png"));
        TextureRegion[][] temp_6 = TextureRegion.split(tex2,50,50);
        buttonUp = temp_6[0][0];
        buttonDown = temp_6[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        soundEffectButton_On = new ImageButton(up,down);
        soundEffectButton_On.setPosition((Gdx.graphics.getWidth() / 10) * 5, (Gdx.graphics.getHeight() / 20) * 5);
        soundEffectButton_On.setSize(100 * (Gdx.graphics.getWidth() / 360),100 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(soundEffectButton_On);
        soundEffectButton_On.setVisible(false);
        soundEffectButton_On.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.resume();
                soundEffectButton_On.setVisible(false);
                soundEffectButton_Off.setVisible(true);
            }
        });

        //Add Volume Button(On) show the Sound Effect is OFF, click it the Sound Effect will turn ON
        tex2 = new Texture(Gdx.files.internal("button/VolumeButton_On.png"));
        TextureRegion[][] temp_7 = TextureRegion.split(tex2,50,50);
        buttonUp = temp_7[0][0];
        buttonDown = temp_7[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        soundEffectButton_Off = new ImageButton(up,down);
        soundEffectButton_Off.setPosition((Gdx.graphics.getWidth() / 10) * 5, (Gdx.graphics.getHeight() / 20) * 5);
        soundEffectButton_Off.setSize(100 * (Gdx.graphics.getWidth() / 360),100 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(soundEffectButton_Off);
        soundEffectButton_Off.setVisible(true);
        soundEffectButton_Off.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
               SoundManager.button.dispose();
               soundEffectButton_On.setVisible(true);
               soundEffectButton_Off.setVisible(false);
            }
        });


        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta){
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
        tex.dispose();
        tex2.dispose();
    }
}
