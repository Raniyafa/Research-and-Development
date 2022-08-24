package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import java.awt.Button;
import java.awt.event.ActionListener;
import sun.awt.ExtendedKeyCodes;

    public class LoadingScreen extends ScreenAdapter {

        //Artificial loading screen class because it looks awkward when the game instantly switches between panels
        //can by removed changing all calls to switch to this class into calls for switching to GameScreen using : game.setScreen(new GameScreen(game));

        private MultipleScenes game;
        private float timer = 0.0f;

        private Stage stage;
        private Texture tex;
        private Image image;
        private TextureRegion region;

//        private Viewport viewport;
//        private Camera camera;

        public LoadingScreen(MultipleScenes game) {
            this.game = game;
        }

        @Override
        public void show(){

            //Scale the UI size
//            camera = new PerspectiveCamera();
//            viewport = new FitViewport(360, 640);

            stage = new Stage(new ScreenViewport());
//            stage = new Stage(new StretchViewport(360, 640));

            //Add loading.png as the background
            tex = new Texture(Gdx.files.internal("image/loading.png"));
//            region = new TextureRegion(tex,0,0,750,1334);
            region = new TextureRegion(tex);
            image = new Image(region);
            image.setPosition(0,0);
//            image.setSize(360 * (Gdx.graphics.getWidth() / 360),750 * (Gdx.graphics.getHeight() / 640));
            image.setSize(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
            stage.addActor(image);
        }

        @Override
        public void render(float delta) {

            //Scale the UI size
//            stage.getViewport().update(360, 640, true);

            if(!game.getSocket().isOpen()){
                game.setScreen(new HomeScreen(game));
            }

            timer+=delta;
            if(timer >= 0.5f){
                game.setScreen(new GameScreen(game));
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
            tex.dispose();
        }
    }

