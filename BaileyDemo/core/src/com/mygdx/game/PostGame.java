package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;

import java.util.ArrayList;

public class PostGame extends ScreenAdapter {

    //PostGame screen which is shown after users complete a match

    private MultipleScenes game;

//    private TextButton facebookPost;
//    private TextButton twitterPost;
//    private TextButton instagramPost;
//    private TextButton exitToMenu;

    private Texture tex;
    private Image image;
    private TextureRegion region;
    private SpriteBatch batch;

    private Stage stage;
    private Skin mySkin;
    private BitmapFont font;
    private BitmapFont smallFont;
    private BitmapFont fontLarge;
    private boolean moveToMain = false;
    private ShapeRenderer shapeRenderer;
    private ArrayList<Shape> shapeArr;

    private TextureRegionDrawable up;
    private TextureRegionDrawable down;
    private TextureRegion buttonUp;
    private TextureRegion buttonDown;
    private Texture tex2;
    private ImageButton button;
    private ImageButton FaceBook;
    private ImageButton Twitter;
    private ImageButton Instagram;
    private ImageButton Share;

    private float heightRatio;
    private float widthRatio;

    public PostGame(MultipleScenes game, ArrayList<Shape> shapeArray) {
        shapeArr = shapeArray;
        this.game = game;
    }

    @Override
    public void show(){

        heightRatio = Gdx.graphics.getHeight() / 640;
        widthRatio = Gdx.graphics.getHeight() / 360;

        font = new BitmapFont(Gdx.files.internal("font/dbfont.fnt"),
                Gdx.files.internal("font/dbfont.png"), false);
        font.setColor(Color.BLACK);

        smallFont = new BitmapFont(Gdx.files.internal("font/dbSmallFont.fnt"),
                Gdx.files.internal("font/dbSmallFont.png"), false);
        smallFont.setColor(Color.BLACK);

        //Adding Background (new)
        tex = new Texture(Gdx.files.internal("image/gameFinish.png"));
        batch = new SpriteBatch();

        game.setListener(getListener());
        shapeRenderer = new ShapeRenderer();

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        float widthSlice = Gdx.graphics.getWidth() / 20;

//        facebookPost = new TextButton("Facebook", mySkin, "toggle");
//        facebookPost.setBounds(widthSlice, Gdx.graphics.getHeight()/2 - 310, 80, 40);
//        facebookPost.getLabel().setFontScale(0.6f, 0.6f);
//        facebookPost.addListener(new InputListener(){
//
//            @Override
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//
//            }
//            @Override
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.net.openURI("https://www.facebook.com/sharer/sharer.php?u=https://static-cdn.jtvnw.net/user-default-pictures-uv/215b7342-def9-11e9-9a66-784f43822e80-profile_image-70x70.png&quote=TEST DRAWING FROM DRAW BUDDY TEST");
//                return true;
//            }
//        });
//        facebookPost.setVisible(false);
//        stage.addActor(facebookPost);

        //FaceBook share button
        tex2 = new Texture(Gdx.files.internal("button/FaceBookButton.png"));
        TextureRegion[][] temp_f = TextureRegion.split(tex2,512,512);
        buttonUp = temp_f[0][0];
        buttonDown = temp_f[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        FaceBook = new ImageButton(up,down);
        FaceBook.setPosition(Gdx.graphics.getWidth()/2 + 10,Gdx.graphics.getHeight() / 2 - 210);
        FaceBook.setSize(45,45);
        stage.addActor(FaceBook);
        FaceBook.setVisible(false);
        FaceBook.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                Gdx.net.openURI("https://www.facebook.com/sharer/sharer.php?u=https://static-cdn.jtvnw.net/user-default-pictures-uv/215b7342-def9-11e9-9a66-784f43822e80-profile_image-70x70.png&quote=TEST DRAWING FROM DRAW BUDDY TEST");
            }
        });

//        twitterPost = new TextButton("Twitter", mySkin, "toggle");
//        twitterPost.setBounds(widthSlice * 6, Gdx.graphics.getHeight()/2 - 310, 80, 40);
//        twitterPost.getLabel().setFontScale(0.6f, 0.6f);
//        twitterPost.addListener(new InputListener(){
//
//            @Override
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//
//            }
//            @Override
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.net.openURI("https://twitter.com/share?ref_src=https://static-cdn.jtvnw.net/user-default-pictures-uv/215b7342-def9-11e9-9a66-784f43822e80-profile_image-70x70.png");
//                return true;
//            }
//        });
//        twitterPost.setVisible(false);
//        stage.addActor(twitterPost);

        //Twitter share button
        tex2 = new Texture(Gdx.files.internal("button/TwitterButton.png"));
        TextureRegion[][] temp_t = TextureRegion.split(tex2,512,512);
        buttonUp = temp_t[0][0];
        buttonDown = temp_t[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        Twitter = new ImageButton(up,down);
        Twitter.setPosition(Gdx.graphics.getWidth()/2 + 65,Gdx.graphics.getHeight() / 2 - 210);
        Twitter.setSize(45,45);
        stage.addActor(Twitter);
        Twitter.setVisible(false);
        Twitter.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                Gdx.net.openURI("https://twitter.com/share?ref_src=https://static-cdn.jtvnw.net/user-default-pictures-uv/215b7342-def9-11e9-9a66-784f43822e80-profile_image-70x70.png");
            }
        });

        //Instagram share button
        tex2 = new Texture(Gdx.files.internal("button/Instagram.png"));
        TextureRegion[][] temp_i = TextureRegion.split(tex2,512,512);
        buttonUp = temp_i[0][0];
        buttonDown = temp_i[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        Instagram = new ImageButton(up,down);
        Instagram.setPosition(Gdx.graphics.getWidth()/2 + 120,Gdx.graphics.getHeight() / 2 - 210);
        Instagram.setSize(45,45);
        stage.addActor(Instagram);
        Instagram.setVisible(false);
        Instagram.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                Gdx.net.openURI("https://www.example.com");
            }
        });

        //back to lobby button
        tex2 = new Texture(Gdx.files.internal("button/BackToLobbyButton.png"));
        TextureRegion[][] temp_0 = TextureRegion.split(tex2,400,200);
        buttonUp = temp_0[0][0];
        buttonDown = temp_0[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth()/2 + 10,Gdx.graphics.getHeight() / 2 - 280);
        button.setSize(133,66);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                game.setScreen(new HomeScreen(game));
            }
        });

        //Save button
//        tex2 = new Texture(Gdx.files.internal("button/SaveButton.png"));
//        TextureRegion[][] temp_2 = TextureRegion.split(tex2,220,400);
//        buttonUp = temp_2[0][0];
//        buttonDown = temp_2[0][1];
//        up = new TextureRegionDrawable(buttonUp);
//        down = new TextureRegionDrawable(buttonDown);
//        button = new ImageButton(up,down);
//        button.setPosition(Gdx.graphics.getWidth()/2 - 75,Gdx.graphics.getHeight() / 2 - 280);
//        button.setSize(73,133);
//        stage.addActor(button);
//        button.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                SoundManager.button.play();
//                // code here ~~
//            }
//        });

//        exitToMenu = new TextButton("Home", mySkin, "toggle");
//        exitToMenu.setBounds(widthSlice * 16, Gdx.graphics.getHeight() - 40, 80, 40);
//        exitToMenu.getLabel().setFontScale(0.6f, 0.6f);
//        exitToMenu.addListener(new InputListener(){
//
//            @Override
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//
//            }
//            @Override
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                game.setGameLobby(new GameLobby());
//                moveToMain = true;
//                return true;
//            }
//        });
//        stage.addActor(exitToMenu);
//
//        instagramPost = new TextButton("Instagram", mySkin, "toggle");
//        instagramPost.setBounds(widthSlice * 11, Gdx.graphics.getHeight()/2 - 310, 80, 40);
//        instagramPost.getLabel().setFontScale(0.6f, 0.6f);
//        instagramPost.addListener(new InputListener(){
//
//            @Override
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//
//            }
//            @Override
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.net.openURI("https://www.example.com");
//                return true;
//            }
//        });
//        instagramPost.setVisible(false);
//        stage.addActor(instagramPost);

        //share button
        tex2 = new Texture(Gdx.files.internal("button/ShareButton.png"));
        TextureRegion[][] temp_1 = TextureRegion.split(tex2,400,190);
        buttonUp = temp_1[0][0];
        buttonDown = temp_1[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        Share = new ImageButton(up,down);
        Share.setPosition(Gdx.graphics.getWidth()/2 + 10,Gdx.graphics.getHeight() / 2 - 210);
        Share.setSize(133,63);
        stage.addActor(Share);
        Share.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                if(!FaceBook.isVisible() && !Twitter.isVisible() && !Instagram.isVisible()){
                    FaceBook.setVisible(true);
                    Twitter.setVisible(true);
                    Instagram.setVisible(true);
                    Share.setVisible(false);
                }
                else if(FaceBook.isVisible() && Twitter.isVisible() && Instagram.isVisible()){
                    FaceBook.setVisible(false);
                    Twitter.setVisible(false);
                    Instagram.setVisible(false);
                }
            }
        });

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
        batch.begin();
        batch.draw(tex,0,0,360,750);
        batch.end();

        stage.act();
        stage.draw();

        String postGameMsg = "Artwork created by: "+game.getPlayerName()+" & "+game.getGameLobby().getPartnerName();
        smallFont.draw(game.getBatch(), postGameMsg, Gdx.graphics.getWidth()/2 - 150, Gdx.graphics.getHeight() - 60);

        game.getBatch().end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Shape tempShape = new Shape();
        //For loop which iterates through the shape array and draws each shape individually
        for (int i = 0; i <= shapeArr.size() - 1; i++) {
            try {
                //Get reference to shape object then draw the shape to screen
                Shape drawShape = shapeArr.get(i);
                shapeRenderer.setColor(drawShape.getRgb()[0], drawShape.getRgb()[1], drawShape.getRgb()[2], 1);
                String temp = drawShape.getType();
                //Scale the drawing for different resolutions
                float drawShapeX = drawShape.getX() * widthRatio;
                float drawShapeY = drawShape.getY() * heightRatio;
                float tempShapeX = tempShape.getX() * widthRatio;
                float tempShapeY = tempShape.getY() * heightRatio;

                //Check if current shape is on the same line as the previous shape, if so then connect them with a line
                if (drawShape.getLineNo() == tempShape.getLineNo()) {
                    if ((!(drawShapeX >= tempShapeX - 5 && drawShapeX <= tempShapeX + 5)) || (!(drawShapeY >= tempShapeY - 5 && drawShapeY <= tempShapeY + 5))) {

                        shapeRenderer.rectLine(tempShapeX, tempShapeY, drawShapeX, drawShapeY, 20);
                    }
                }
                //Draw the current shape as a circle
                shapeRenderer.circle(drawShapeX, drawShapeY, 10);
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
        tex.dispose();
        batch.dispose();
        font.dispose();
        fontLarge.dispose();
    }
}

