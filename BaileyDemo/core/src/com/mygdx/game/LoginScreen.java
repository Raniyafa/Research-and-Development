package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class LoginScreen extends ScreenAdapter {
    private MultipleScenes game;
    private TextButton exitLobby;
    private Stage stage;
    private Skin mySkin;
    private BitmapFont font;
    private boolean moveToHome;
    private TextField.OnscreenKeyboard keyboard;
    private String name;
    private float passTimer = 0.0f;
    private TextButton randomName;
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

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                Gdx.app.log("WS Loginscreen", "Got message: " + packet);

                String[] serverMessage = packet.split("/");
                if (serverMessage[0].matches("AuthCode")) {
                    game.setAuthCode(serverMessage[1]);
                }

                if(packet.matches("Pass")){
                      game.setPlayerName(name);
                      moveToHome = true;
                }
                else if(packet.matches("Fail")){
                    passTimer = 5.0f;
                }
                return FULLY_HANDLED;
            }
        };
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/dbfont.fnt"),
        Gdx.files.internal("font/dbfont.png"), false);
        game.setListener(getListener());

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());
        //Gdx.graphics.setWindowedMode(360, 640);

        tex = new Texture(Gdx.files.internal("image/HomePage.png"));
        region = new TextureRegion(tex,0,0, 750, 1334);
        image = new Image(region);
        image.setPosition(0, 0);
        image.setSize(360 * (Gdx.graphics.getWidth() / 360),640 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(image);

        //Background music
        SoundManager.create();
        SoundManager.background.setLooping(true);
        SoundManager.background.setVolume(0.1f);
        SoundManager.background.play();

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
                    String field = textField.getText();
                    game.getSocket().send("CheckName/"+field);
                    name = field;
                }
                return false;


            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //Gdx.app.log("", "touchdown");
                //Gdx.input.setOnscreenKeyboardVisible(true);
                //textField.getOnscreenKeyboard().show(true);

                //prompt(textField);


                return false;
            }
        });

//        randomName = new TextButton("Random Name", mySkin, "toggle");
//        randomName.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 - 200, 150, 50);
//        randomName.getLabel().setFontScale(0.6f, 0.6f);
//        randomName.addListener(new InputListener(){
//
//            @Override
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                game.getSocket().send("CheckName/Bob");
//                name = "Bob";
//                game.setPlayerName("Bob");
//                moveToHome = true;
//                return true;
//            }
//        });
//
//        stage.addActor(randomName);

        //Start Button
        tex2 = new Texture(Gdx.files.internal("button/StartButton.png"));
        TextureRegion[][] temp_1 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_1[0][0];
        buttonDown = temp_1[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth() / 2 - 115, Gdx.graphics.getHeight() / 2 - 200);
        button.setSize(240,70);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getSocket().send("CheckName/Bob");
                name = "Bob";
                game.setPlayerName("Bob");
                moveToHome = true;
                //return true;
                SoundManager.button.play();
            }
        });

        //Setting Icon
//        tex2 = new Texture(Gdx.files.internal("button/SettingButton.png"));
//        TextureRegion[][] temp = TextureRegion.split(tex2,85,85);
//        buttonUp = temp[0][0];
//        buttonDown = temp[0][1];
//        up = new TextureRegionDrawable(buttonUp);
//        down = new TextureRegionDrawable(buttonDown);
//        button = new ImageButton(up,down);
//        button.setPosition(Gdx.graphics.getWidth() / 2 + 100,Gdx.graphics.getHeight() / 2 + 250);
//        button.setSize(30,30);
//        stage.addActor(button);
//        button.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(new SettingMenu(game));
//                SoundManager.button.play();
//            }
//        });

        stage.addActor(textField);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if(moveToHome){
            game.setScreen(new HomeScreen(game));
        }

        if(game.getSocket().isOpen()) {
            Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        }
        else{
            Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        game.getBatch().begin();

        //font.draw(game.getBatch(), "If playing on phone,\nuse random name button", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2 - 220);

        if(passTimer > 0.0f){
            font.draw(game.getBatch(), "Inappropriate name detected\n please try again.\n", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2 + 200);
            passTimer -= delta;
        }

        if(game.getSocket().isOpen()) {
            stage.act();
            stage.draw();
            //font.draw(game.getBatch(), "Enter your name:\n", Gdx.graphics.getWidth() / 2 - 110, Gdx.graphics.getHeight() / 2 + 100);
        }
        else {
            if(!game.getSocket().isConnecting()) {
                game.getSocket().connect();
            }
            font.draw(game.getBatch(), "No Connection\nURL: "+game.getSocket().getUrl().toString()+"\n"+"State: "+game.getSocket().getState().toString()+"\nAttemping to reconnect", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2);
            //font.draw(game.getBatch(), "CONNECTION LOST TO SERVER\n", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2 - 200);
            font.draw(game.getBatch(), game.socketException, Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2);
        }
        game.getBatch().end();
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);

        //comment for setting button test
//        if(!moveToHome)
//        dispose();
    }

    @Override
    public void dispose(){
        stage.dispose();
        mySkin.dispose();
        font.dispose();
        game.dispose();
        Gdx.app.exit();
        SoundManager.dispose();
    }
}