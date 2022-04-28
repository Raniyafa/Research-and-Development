package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;

public class PostGame extends ScreenAdapter {

    private MultipleScenes game;

    private TextButton facebookPost;
    private TextButton twitterPost;
    private TextButton instagramPost;
    private TextButton exitToMenu;

    private Stage stage;
    private Skin mySkin;
    private BitmapFont font;
    private boolean moveToMain = false;
    private ShapeRenderer shapeRenderer;

    public PostGame(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/font.fnt"),
                Gdx.files.internal("font/font.png"), false);

        game.setListener(getListener());
        shapeRenderer = new ShapeRenderer();

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        Gdx.graphics.setWindowedMode(360, 640);

        facebookPost = new TextButton("Share to FaceBook", mySkin, "toggle");
        facebookPost.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 - 200, 150, 50);
        facebookPost.getLabel().setFontScale(0.6f, 0.6f);
        facebookPost.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.net.openURI("https://www.facebook.com/sharer/sharer.php?u=https://static-cdn.jtvnw.net/user-default-pictures-uv/215b7342-def9-11e9-9a66-784f43822e80-profile_image-70x70.png&quote=TEST DRAWING FROM DRAW BUDDY TEST");
                return true;
            }
        });
        stage.addActor(facebookPost);

        twitterPost = new TextButton("Share to Twitter", mySkin, "toggle");
        twitterPost.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 - 100, 150, 50);
        twitterPost.getLabel().setFontScale(0.6f, 0.6f);
        twitterPost.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.net.openURI("https://twitter.com/share?ref_src=https://static-cdn.jtvnw.net/user-default-pictures-uv/215b7342-def9-11e9-9a66-784f43822e80-profile_image-70x70.png");
                return true;
            }
        });
        stage.addActor(twitterPost);

        exitToMenu = new TextButton("Return to Home", mySkin, "toggle");
        exitToMenu.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 + 100, 150, 50);
        exitToMenu.getLabel().setFontScale(0.6f, 0.6f);
        exitToMenu.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                moveToMain = true;
                return true;
            }
        });
        stage.addActor(exitToMenu);

        instagramPost = new TextButton("Share to Instagram", mySkin, "toggle");
        instagramPost.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2, 150, 50);
        instagramPost.getLabel().setFontScale(0.6f, 0.6f);
        instagramPost.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.net.openURI("https://www.example.com");
                return true;
            }
        });
        stage.addActor(instagramPost);



        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if(moveToMain){
            game.setScreen(new HomeScreen(game));
        }

        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
        game.getBatch().begin();

        game.getBatch().end();
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                String temp = packet;
                Gdx.app.log("WS", "Got message: " + packet);
                String[] serverMessage = packet.split("/");

                if (serverMessage[0].matches("LobbyInfo")) {

                }
                return FULLY_HANDLED;
            }
        };
    }


    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose(){
        game.dispose();
        stage.dispose();
        mySkin.dispose();
        font.dispose();
    }
}

