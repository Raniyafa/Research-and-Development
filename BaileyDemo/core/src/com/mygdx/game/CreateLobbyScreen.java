package com.mygdx.game;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CreateLobbyScreen extends ScreenAdapter {

    private MultipleScenes game;
    private Stage stage;
    private Texture tex;
    private Image image;
    private TextureRegion region;
    private TextureRegionDrawable up;
    private TextureRegionDrawable down;
    private TextureRegion buttonUp;
    private TextureRegion buttonDown;
    private Texture tex2;
    private ImageButton imgButton;

    private boolean moveToLobby = false;
    private boolean moveToMatchmaking = false;

    public CreateLobbyScreen(MultipleScenes game) {this.game = game;}

    @Override
    public void show(){

        //Isaac-add img button for test
        tex2 = new Texture(Gdx.files.internal("button/button240_QuickPlay.png"));
        TextureRegion[][] temp_1 = TextureRegion.split(tex2,240,240);
        buttonUp = temp_1[0][0];
        buttonDown = temp_1[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        imgButton = new ImageButton(up,down);
        imgButton.setPosition(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 - 25);
        //button.setSize(480,480);
        stage.addActor(imgButton);
        imgButton.addListener(new InputListener(){

        });

        tex2 = new Texture(Gdx.files.internal("button/button240_CreateLobby.png"));
        TextureRegion[][] temp_2 = TextureRegion.split(tex2,240,240);
        buttonUp = temp_2[0][0];
        buttonDown = temp_2[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        imgButton = new ImageButton(up,down);
        imgButton.setPosition(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 + 50);
        //button.setSize(480,480);
        stage.addActor(imgButton);
        imgButton.addListener(new InputListener(){

        });

        tex2 = new Texture(Gdx.files.internal("button/button240_GamePIN.png"));
        TextureRegion[][] temp_3 = TextureRegion.split(tex2,240,240);
        buttonUp = temp_3[0][0];
        buttonDown = temp_3[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        imgButton = new ImageButton(up,down);
        imgButton.setPosition(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 + 125);
        //button.setSize(480,480);
        stage.addActor(imgButton);
        imgButton.addListener(new InputListener(){

        });


        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        game.getBatch().begin();
        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();


    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose(){
        game.dispose();
        stage.dispose();
    }
}

