package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.awt.Button;

public class TitleScreen extends ScreenAdapter {

    MultipleScenes game;
    TextButton startButton;
    Stage stage;
    Skin mySkin;

    public TitleScreen(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show(){
        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());


        startButton = new TextButton("Join Lobby", mySkin, "toggle");
        startButton.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2, 150, 50);
        startButton.getLabel().setFontScale(0.6f, 0.6f);


        startButton.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                startButton.setText("Join Lobby");
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game));
                return true;
            }
        });

        stage.addActor(startButton);
        Gdx.input.setInputProcessor(stage);
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Draw Buddy\n", Gdx.graphics.getWidth() / 2 - 83, Gdx.graphics.getHeight() * .70f);
        game.font.draw(game.batch, "Click the button to join a lobby.", Gdx.graphics.getWidth() / 2 - 95, Gdx.graphics.getHeight() * .42f);
        game.batch.end();

    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
}