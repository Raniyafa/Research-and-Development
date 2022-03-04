package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;

public class LoginScreen extends ScreenAdapter {


    MultipleScenes game;
    TextButton exitLobby;
    Stage stage;
    Skin mySkin;
    BitmapFont font;
    boolean moveToHome;

    public LoginScreen(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/font.fnt"),
        Gdx.files.internal("font/font.png"), false);
        game.getSocket().addListener(game.listener);
        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.graphics.setWindowedMode(360, 640);

        final TextField textField = new TextField("Text field", mySkin);
        textField.setX(Gdx.graphics.getWidth() / 2 - 125);
        textField.setY(Gdx.graphics.getHeight() / 2 - 50);
        textField.setWidth(250);
        textField.setText("");
        textField.setHeight(70);
        textField.setVisible(true);

        textField.addListener(new InputListener(){
            @Override
            public boolean keyDown (InputEvent event, int keycode) {
                if(keycode == Input.Keys.ENTER){
                    textField.setVisible(false);
                    game.playerName = textField.getText();
                    moveToHome = true;
                }
                return false;
            }
        });

        stage.addActor(textField);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if(moveToHome){
            game.setScreen(new HomeScreen(game));
        }
        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        game.batch.begin();
        font.draw(game.batch, "Enter your name:\n", Gdx.graphics.getWidth() / 2 - 110, Gdx.graphics.getHeight() / 2 + 100);
        game.batch.end();
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
}