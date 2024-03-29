package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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

public class modeIntroScreen extends ScreenAdapter {
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
    private ImageButton button;

//    private Viewport viewport;
//    private Camera camera;

    public modeIntroScreen(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show() {
        //Scale the UI size
//        camera = new PerspectiveCamera();
//        viewport = new FitViewport(360, 640);

        stage = new Stage(new ScreenViewport());
//        stage = new Stage(new StretchViewport(360, 640));

        //Add modeIntro.png as the background
        tex = new Texture(Gdx.files.internal("image/modeIntro.png"));
//        region = new TextureRegion(tex,0,0,750,1334);
        region = new TextureRegion(tex);
        image = new Image(region);
        image.setPosition(0, 0);
//        image.setSize(360 * (Gdx.graphics.getWidth() / 360), 750 * (Gdx.graphics.getHeight() / 640));
        image.setSize(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        stage.addActor(image);

        //Add Back Button, click it will move back to Lobby (Home Screen)
        tex2 = new Texture(Gdx.files.internal("button/BackButton.png"));
        TextureRegion[][] temp_0 = TextureRegion.split(tex2, 210, 60);
        buttonUp = temp_0[0][0];
        buttonDown = temp_0[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up, down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 0.5f, (Gdx.graphics.getHeight() / 20) * 18);
        button.setSize(105 * (Gdx.graphics.getWidth() / 360), 30 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                game.setScreen(new HomeScreen(game));
            }
        });
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        //Scale the UI size
//        stage.getViewport().update(360, 640, true);

        game.getBatch().begin();
        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        game.getBatch().end();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        game.dispose();
        stage.dispose();
        tex.dispose();
        tex2.dispose();
    }
}
