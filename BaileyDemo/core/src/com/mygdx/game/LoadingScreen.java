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
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import java.awt.Button;
import java.awt.event.ActionListener;
import sun.awt.ExtendedKeyCodes;

    public class LoadingScreen extends ScreenAdapter {

        private MultipleScenes game;
        private float timer = 0.0f;

        public LoadingScreen(MultipleScenes game) {
            this.game = game;
        }

        @Override
        public void show(){

        }

        @Override
        public void render(float delta) {
            if(!game.getSocket().isOpen()){
                game.setScreen(new HomeScreen(game));
            }

            timer+=delta;
            if(timer >= 0.5f){
                game.setScreen(new GameScreen(game));
            }
            Gdx.gl.glClearColor(0, 0, 0.25f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            game.getBatch().begin();
            game.getFont().draw(game.getBatch(), "Loading Match!\nWord Topic: "+game.getGameLobby().getWordTopic(), Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2);
            game.getBatch().end();


        }

        @Override
        public void hide(){
            Gdx.input.setInputProcessor(null);
        }

        @Override
        public void dispose(){
            game.dispose();
        }
    }

