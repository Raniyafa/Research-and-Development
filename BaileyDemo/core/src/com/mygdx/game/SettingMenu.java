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









    //slider = new Slider(0, 100, 1, false, skin);
        ////slider.setValue(1);
        //Container<Slider> sliderContainer = new Container<Slider>(slider);
        //sliderContainer.setTransform(true);

        ////sliderContainer.setOrigin(slider.getWidth()/2,slider.getHeight()/2);
       // sliderContainer.setSize(slider.getWidth(),slider.getHeight());
       // sliderContainer.setScale(2);

//        slider.setSize(30, 100);
//        slider.setPosition(100, 100);
//        slider.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//
//            }
//        });
        //stage.addActor(slider);

        tex = new Texture(Gdx.files.internal("image/setting.png"));
        region = new TextureRegion(tex,0,0,750,1334);
        image = new Image(region);
        image.setPosition(0,0);
        image.setSize(360 * (Gdx.graphics.getWidth() / 360),750 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(image);

        tex2 = new Texture(Gdx.files.internal("button/BackButton.png"));
        TextureRegion[][] temp_0 = TextureRegion.split(tex2,210,60);
        buttonUp = temp_0[0][0];
        buttonDown = temp_0[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth()/2 - 180,Gdx.graphics.getHeight() / 2 + 270);
        button.setSize(105,30);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //back to last activity
                SoundManager.button.play();
                game.setScreen(new HomeScreen(game));
            }
        });

        //Game Rules Button
        tex2 = new Texture(Gdx.files.internal("button/RulesButton.png"));
        TextureRegion[][] temp_3 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_3[0][0];
        buttonDown = temp_3[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth() / 2 - 60,Gdx.graphics.getHeight()/2 - 220);
        button.setSize(120,35);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                game.setScreen(new gameRuleScreen(game));
            }
        });

        //Background Music ON
        tex2 = new Texture(Gdx.files.internal("button/OnButton.png"));
        TextureRegion[][] temp_4 = TextureRegion.split(tex2,200,140);
        buttonUp = temp_4[0][0];
        buttonDown = temp_4[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        backgroundMusicButton_On = new ImageButton(up,down);
        backgroundMusicButton_On.setPosition(Gdx.graphics.getWidth() / 2 + 10,Gdx.graphics.getHeight()/2 + 5);
        backgroundMusicButton_On.setSize(100,70);
        stage.addActor(backgroundMusicButton_On);
        backgroundMusicButton_On.setVisible(true);
        backgroundMusicButton_On.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.background.setVolume(0.03f);
                SoundManager.button.play();
                backgroundMusicButton_On.setVisible(false);
                backgroundMusicButton_Off.setVisible(true);
            }
        });

        //Background Music OFF
        tex2 = new Texture(Gdx.files.internal("button/OffButton.png"));
        TextureRegion[][] temp_5 = TextureRegion.split(tex2,200,140);
        buttonUp = temp_5[0][0];
        buttonDown = temp_5[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        backgroundMusicButton_Off = new ImageButton(up,down);
        backgroundMusicButton_Off.setPosition(Gdx.graphics.getWidth() / 2 + 10,Gdx.graphics.getHeight()/2 + 5);
        backgroundMusicButton_Off.setSize(100,70);
        stage.addActor(backgroundMusicButton_Off);
        backgroundMusicButton_Off.setVisible(false);
        backgroundMusicButton_Off.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.background.setVolume(0.0f);
                SoundManager.button.play();
                backgroundMusicButton_On.setVisible(true);
                backgroundMusicButton_Off.setVisible(false);
            }
        });

        //Sound Effect ON
        tex2 = new Texture(Gdx.files.internal("button/OnButton.png"));
        TextureRegion[][] temp_6 = TextureRegion.split(tex2,200,140);
        buttonUp = temp_6[0][0];
        buttonDown = temp_6[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth() / 2 + -120,Gdx.graphics.getHeight()/2 + -150);
        button.setSize(100,70);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.resume();
            }
        });

        //Sound Effect OFF
        tex2 = new Texture(Gdx.files.internal("button/OffButton.png"));
        TextureRegion[][] temp_7 = TextureRegion.split(tex2,200,140);
        buttonUp = temp_7[0][0];
        buttonDown = temp_7[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth() / 2 + 10,Gdx.graphics.getHeight()/2 + -150);
        button.setSize(100,70);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
               SoundManager.button.dispose();
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
