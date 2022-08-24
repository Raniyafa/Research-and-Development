package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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

    private float partnerHeightRatio;
    private float partnerWidthRatio;

//    private Viewport viewport;
//    private Camera camera;

    public PostGame(MultipleScenes game, ArrayList<Shape> shapeArray, float[] scaleInfo) {
        shapeArr = shapeArray;
        partnerWidthRatio = scaleInfo[0];
        partnerHeightRatio = scaleInfo[1];
        this.game = game;
    }

    @Override
    public void show(){

        //Scale the UI size
//        camera = new PerspectiveCamera();
//        viewport = new FitViewport(360, 640);

        heightRatio = Gdx.graphics.getHeight() / 640;
        widthRatio = Gdx.graphics.getHeight() / 360;

        font = new BitmapFont(Gdx.files.internal("font/dbFontM.fnt"),
                Gdx.files.internal("font/dbFontM.png"), false);
        font.setColor(Color.BLACK);

        smallFont = new BitmapFont(Gdx.files.internal("font/dbSmallFontM.fnt"),
                Gdx.files.internal("font/dbSmallFontM.png"), false);
        smallFont.setColor(Color.BLACK);

        //Adding Background (new) - Add gameFinish.png as the background
        tex = new Texture(Gdx.files.internal("image/gameFinish.png"));
        batch = new SpriteBatch();

        game.setListener(getListener());
        shapeRenderer = new ShapeRenderer();

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());
//        stage = new Stage(new StretchViewport(360, 640));

        float widthSlice = Gdx.graphics.getWidth() / 20;

        //FaceBook Share Button(new) to exchange the Text Button (FaceBook) below
        tex2 = new Texture(Gdx.files.internal("button/FaceBookButton.png"));
        TextureRegion[][] temp_f = TextureRegion.split(tex2,512,512);
        buttonUp = temp_f[0][0];
        buttonDown = temp_f[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        FaceBook = new ImageButton(up,down);
        FaceBook.setPosition((Gdx.graphics.getWidth() / 10) * 1, (Gdx.graphics.getHeight() / 20) * 1.8f);
        FaceBook.setSize(45 * (Gdx.graphics.getWidth() / 360),45 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(FaceBook);
        FaceBook.setVisible(false);
        FaceBook.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                Gdx.net.openURI("https://www.facebook.com/sharer/sharer.php?u=https://static-cdn.jtvnw.net/user-default-pictures-uv/215b7342-def9-11e9-9a66-784f43822e80-profile_image-70x70.png&quote=TEST DRAWING FROM DRAW BUDDY TEST");
            }
        });

        //Twitter Share Button(new) to exchange the Text Button(Twitter) below
        tex2 = new Texture(Gdx.files.internal("button/TwitterButton.png"));
        TextureRegion[][] temp_t = TextureRegion.split(tex2,512,512);
        buttonUp = temp_t[0][0];
        buttonDown = temp_t[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        Twitter = new ImageButton(up,down);
        Twitter.setPosition((Gdx.graphics.getWidth() / 10) * 2.7f, (Gdx.graphics.getHeight() / 20) * 1.8f);
        Twitter.setSize(45 * (Gdx.graphics.getWidth() / 360),45 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(Twitter);
        Twitter.setVisible(false);
        Twitter.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                Gdx.net.openURI("https://twitter.com/share?ref_src=https://static-cdn.jtvnw.net/user-default-pictures-uv/215b7342-def9-11e9-9a66-784f43822e80-profile_image-70x70.png");
            }
        });

        //Instagram Share Button(new) to exchange the Text Button(Instagram) below
        tex2 = new Texture(Gdx.files.internal("button/Instagram.png"));
        TextureRegion[][] temp_i = TextureRegion.split(tex2,512,512);
        buttonUp = temp_i[0][0];
        buttonDown = temp_i[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        Instagram = new ImageButton(up,down);
        Instagram.setPosition((Gdx.graphics.getWidth() / 10) * 4.4f, (Gdx.graphics.getHeight() / 20) * 1.8f);
        Instagram.setSize(45 * (Gdx.graphics.getWidth() / 360),45 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(Instagram);
        Instagram.setVisible(false);
        Instagram.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                Gdx.net.openURI("https://www.example.com");
            }
        });

        //Back to Lobby Button(new) to exchange the Text Button(Exit to Lobby) below
        tex2 = new Texture(Gdx.files.internal("button/BackToLobbyButton.png"));
        TextureRegion[][] temp_0 = TextureRegion.split(tex2,400,200);
        buttonUp = temp_0[0][0];
        buttonDown = temp_0[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition((Gdx.graphics.getWidth() / 10) * 5.7f, (Gdx.graphics.getHeight() / 20) * 1.5f);
        button.setSize(133 * (Gdx.graphics.getWidth() / 360),64 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                game.setScreen(new HomeScreen(game));
            }
        });

        //Share Button, click it the 3 Share Buttons(FaceBook, Twitter, Instagram) will show up
        tex2 = new Texture(Gdx.files.internal("button/ShareButton.png"));
        TextureRegion[][] temp_1 = TextureRegion.split(tex2,400,190);
        buttonUp = temp_1[0][0];
        buttonDown = temp_1[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        Share = new ImageButton(up,down);
        Share.setPosition((Gdx.graphics.getWidth() / 10) * 1, (Gdx.graphics.getHeight() / 20) * 1.5f);
        Share.setSize(133 * (Gdx.graphics.getWidth() / 360),65 * (Gdx.graphics.getHeight() / 640));
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

        //Scale the UI size
//        stage.getViewport().update(360, 640, true);

        if(moveToMain){
            game.setScreen(new HomeScreen(game));
        }
        game.getBatch().begin();
        Gdx.gl.glClearColor(244/255.0f, 188/255.0f, 65/255.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(tex,0,0,360 * (Gdx.graphics.getWidth() / 360),750 * (Gdx.graphics.getHeight() / 640));
        batch.end();

        stage.act();
        stage.draw();

        String postGameMsg = "Artwork created by: "+game.getPlayerName()+" & "+game.getGameLobby().getPartnerName();
        smallFont.draw(game.getBatch(), postGameMsg, (Gdx.graphics.getWidth() / 10) * 1.5f, (Gdx.graphics.getHeight() / 20) * 18);

        game.getBatch().end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Shape tempShape = new Shape();
        for (int i = 0; i <= shapeArr.size() - 1; i++) {
            try {
                //Get reference to shape object then draw the shape to screen
                Shape drawShape = shapeArr.get(i);
                shapeRenderer.setColor(drawShape.getRgb()[0], drawShape.getRgb()[1], drawShape.getRgb()[2], 1);
                String temp = drawShape.getType();

                float drawShapeX;
                float drawShapeY;
                //incoming shapes from phone are being scaled again
                //if its my turn then dont scale the stuff, if its not my turn then scale the stuff
                if(drawShape.isMyTurn()) {
                    drawShapeX = drawShape.getX();
                    drawShapeY = drawShape.getY();
                }
                else{
                    if(widthRatio > partnerWidthRatio){
                        drawShapeX = drawShape.getX() / (partnerWidthRatio / widthRatio);

                    }
                    else {
                        drawShapeX = drawShape.getX() * widthRatio / partnerWidthRatio;
                    }
                    if(heightRatio > partnerHeightRatio){
                        drawShapeY = drawShape.getY() / (partnerHeightRatio / heightRatio);
                    }
                    else {
                        drawShapeY = drawShape.getY() * (heightRatio / partnerHeightRatio);
                    }
                }
                float tempShapeX;
                float tempShapeY;

                if(tempShape.isMyTurn()) {
                    tempShapeX = tempShape.getX();
                    tempShapeY = tempShape.getY();
                }
                else{
                    if(widthRatio > partnerWidthRatio){
                        tempShapeX = tempShape.getX() / (partnerWidthRatio / widthRatio);

                    }
                    else {
                        tempShapeX = tempShape.getX() * widthRatio / partnerWidthRatio;
                    }
                    if(heightRatio > partnerHeightRatio){
                        tempShapeY = tempShape.getY() / (partnerHeightRatio / heightRatio);
                    }
                    else {
                        tempShapeY = tempShape.getY() * (heightRatio / partnerHeightRatio);
                    }
                }

                //Check if current shape is on the same line as the previous shape, if so then connect them with a line
                if (drawShape.getLineNo() == tempShape.getLineNo()) {
                    if ((!(drawShapeX >= tempShapeX - 5 && drawShapeX <= tempShapeX + 5)) || (!(drawShapeY >= tempShapeY - 5 && drawShapeY <= tempShapeY + 5))) {
                        //Here the 12 value is the line thickness, scaled by the screen res
                        shapeRenderer.rectLine(tempShapeX, tempShapeY, drawShapeX, drawShapeY, 12 * (widthRatio + heightRatio / 2));
                    }
                }
                //Draw the current shape as a circle

                //here the 6 value is the radius size, scaled by resolution
                shapeRenderer.circle(drawShapeX, drawShapeY, 6 * (widthRatio + heightRatio / 2));
                tempShape = drawShape;
            } catch (Exception e) {
                System.out.println("Null error drawing shapeArr[" + i + "]");
            }
        }

         if(game.getSocket().isClosed()){
                    if(game.getSocket().isConnecting()){
                        game.getSocket().connect();
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

