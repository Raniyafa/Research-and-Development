package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class LoginScreen extends ScreenAdapter {

    //add TAG for img_button test
    private static final String TAG = HomeScreen.class.getSimpleName();

    private MultipleScenes game;
    private TextButton exitLobby;
    private Stage stage;
    private Skin mySkin;
    private BitmapFont font;
    private boolean moveToHome;

    //add IMG Background
    private Texture tex;
    private Image image;
    private TextureRegion region;

    //add image_button
    private TextureRegionDrawable up;
    private TextureRegionDrawable down;
    private TextureRegion buttonUp;
    private TextureRegion buttonDown;
    private Texture tex2;
    private ImageButton button;

    public LoginScreen(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/title3.fnt"),
        Gdx.files.internal("font/title3.png"), false);
        game.getSocket().addListener(game.getListener());
        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.graphics.setWindowedMode(360, 640);

        tex = new Texture(Gdx.files.internal("image/titleDraft2.png"));
        region = new TextureRegion(tex,0,0,360,640);
        image = new Image(region);
        image.setPosition(0,0);
        //image.setSize(360,640);
        stage.addActor(image);

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
                    String name = textField.getText();
                    if(name.contains("nigg") || name.contains("fuck") || name.contains("bitch") || name.contains("shit") || name.contains("cunt")
                            || name.contains("Pussy") || name.contains("bitch"))

                    textField.setVisible(false);
                    game.setPlayerName(textField.getText());
                    moveToHome = true;
                }
                return false;
            }
        });

        //img_button for Setting
        tex2 = new Texture(Gdx.files.internal("button/button240_settingGear.png"));
        TextureRegion[][] temp = TextureRegion.split(tex2,240,240);
        buttonUp = temp[0][0];
        buttonDown = temp[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(200,470);
        //button.setSize(480,480);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //test
                Gdx.app.log(TAG, "CLICK!!");
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
        game.getBatch().begin();


        if(game.getSocket().isOpen()) {
            //text position changed by Isaac
            font.draw(game.getBatch(), "Enter your name:\n", Gdx.graphics.getWidth() / 2 - 120, Gdx.graphics.getHeight() / 2 + 80);
        }
        else if(!game.getSocket().isConnecting()){
            game.getSocket().connect();
            font.draw(game.getBatch(), "CONNECTION LOST TO SERVER\n", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2);
        }
        game.getBatch().end();
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
        if(!moveToHome)
        dispose();
    }

    @Override
    public void dispose(){
        stage.dispose();
        mySkin.dispose();
        font.dispose();
        game.dispose();
        Gdx.app.exit();
    }
}