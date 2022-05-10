package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
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

import java.util.ArrayList;

public class PostGame extends ScreenAdapter {

    private MultipleScenes game;

    private TextButton facebookPost;
    private TextButton twitterPost;
    private TextButton instagramPost;
    private TextButton exitToMenu;

    private Stage stage;
    private Skin mySkin;
    private BitmapFont font;
    private BitmapFont fontLarge;
    private boolean moveToMain = false;
    private ShapeRenderer shapeRenderer;
    private ArrayList<Shape> shapeArr;

    public PostGame(MultipleScenes game, ArrayList<Shape> shapeArray) {
        shapeArr = shapeArray;
        this.game = game;
    }

    @Override
    public void show(){

        font = new BitmapFont(Gdx.files.internal("font/dbfont.fnt"),
                Gdx.files.internal("font/dbfont.png"), false);
        font.setColor(Color.BLACK);

        game.setListener(getListener());
        shapeRenderer = new ShapeRenderer();

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        float widthSlice = Gdx.graphics.getWidth() / 20;


        facebookPost = new TextButton("Facebook", mySkin, "toggle");
        facebookPost.setBounds(widthSlice, Gdx.graphics.getHeight() - 40, 80, 40);
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

        twitterPost = new TextButton("Twitter", mySkin, "toggle");
        twitterPost.setBounds(widthSlice * 6, Gdx.graphics.getHeight() - 40, 80, 40);
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

        exitToMenu = new TextButton("Home", mySkin, "toggle");
        exitToMenu.setBounds(widthSlice * 16, Gdx.graphics.getHeight() - 40, 80, 40);
        exitToMenu.getLabel().setFontScale(0.6f, 0.6f);
        exitToMenu.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                game.setGameLobby(new GameLobby());
                moveToMain = true;
                return true;
            }
        });
        stage.addActor(exitToMenu);

        instagramPost = new TextButton("Instagram", mySkin, "toggle");
        instagramPost.setBounds(widthSlice * 11, Gdx.graphics.getHeight() - 40, 80, 40);
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
        game.getBatch().begin();
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        String postGameMsg = "Artwork created by:\n"+game.getPlayerName()+" & "+game.getGameLobby().getPartnerName();
        font.draw(game.getBatch(), postGameMsg, Gdx.graphics.getWidth() / 2 - 165, 75);

        game.getBatch().end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Shape tempShape = new Shape();
        //For loop which iterates through the shape array and draws each shape individually
        for (int i = 0; i <= shapeArr.size() - 1; i++) {
            try {
                Shape drawShape = shapeArr.get(i);
                shapeRenderer.setColor(drawShape.rgb[0], drawShape.rgb[1], drawShape.rgb[2], 1);
                String temp = drawShape.type;

                if (drawShape.lineNo == tempShape.lineNo) {
                    if ((!(drawShape.x >= tempShape.x - 5 && drawShape.x <= tempShape.x + 5)) || (!(drawShape.y >= tempShape.y - 5 && drawShape.y <= tempShape.y + 5))) {
                        shapeRenderer.rectLine(tempShape.x, tempShape.y, drawShape.x, drawShape.y, 20);
                    }
                }
                if (temp.matches("circle")) {
                    shapeRenderer.circle(drawShape.x, drawShape.y, 10);

                } else if (temp.matches("square")) {
                    shapeRenderer.rect(drawShape.x, drawShape.y, 10, 10);

                } else {
                    shapeRenderer.triangle(drawShape.x - 30.0f, drawShape.y, drawShape.x + 30.0f, drawShape.y, drawShape.x, drawShape.y + 45.0f);
                }

                tempShape = drawShape;
            } catch (Exception e) {
                System.out.println("Null error drawing shapeArr[" + i + "]");
            }
        }


        shapeRenderer.end();
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

