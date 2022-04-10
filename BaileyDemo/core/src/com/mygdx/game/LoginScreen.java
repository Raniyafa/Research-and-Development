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

    public LoginScreen(MultipleScenes game) {
        this.game = game;
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                Gdx.app.log("WS", "Got message: " + packet);

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

        font = new BitmapFont(Gdx.files.internal("font/font.fnt"),
        Gdx.files.internal("font/font.png"), false);
        game.setListener(getListener());
        game.getSocket().addListener(game.getListener());
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

        randomName = new TextButton("Random Name", mySkin, "toggle");
        randomName.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 - 200, 150, 50);
        randomName.getLabel().setFontScale(0.6f, 0.6f);
        randomName.addListener(new InputListener(){

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                game.getSocket().send("CheckName/Bob");
                name = "Bob";
                game.setPlayerName("Bob");
                moveToHome = true;
                return true;
            }
        });

        stage.addActor(randomName);
        stage.addActor(textField);
        Gdx.input.setInputProcessor(stage);
    }

    public static native void prompt(TextField field) /*-{
                $document.contact_form.field.focus();
                $document.contact_form.textField.focus();
                return;
    }-*/;

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

        font.draw(game.getBatch(), "If playing on phone,\nuse random name button", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2 - 220);

        if(passTimer > 0.0f){
            font.draw(game.getBatch(), "Inappropriate name detected,\n please try again.\n", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2 + 200);
            passTimer -= delta;
        }

        if(game.getSocket().isOpen()) {
            font.draw(game.getBatch(), "Enter your name:\n", Gdx.graphics.getWidth() / 2 - 110, Gdx.graphics.getHeight() / 2 + 100);
        }
        else if(!game.getSocket().isOpen()){
            if(!game.getSocket().isConnecting()) {
                game.getSocket().connect();
                font.draw(game.getBatch(), "No Connection\nURL: "+game.getSocket().getUrl().toString()+"\n"+"State: "+game.getSocket().getState().toString(), Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2 - 200);

            }
            //font.draw(game.getBatch(), "CONNECTION LOST TO SERVER\n", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2 - 200);
            font.draw(game.getBatch(), game.socketException, Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2 + 200);
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