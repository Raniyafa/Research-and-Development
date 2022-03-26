package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
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

import java.awt.Button;

import java.awt.event.ActionListener;

import sun.awt.ExtendedKeyCodes;

public class HomeScreen extends ScreenAdapter {

    //add TAG for img_button test
    private static final String TAG = HomeScreen.class.getSimpleName();

    private MultipleScenes game;
    private TextButton joinLobby;
    private TextButton findMatch;
    private TextButton createLobby;
    private Stage stage;
    private Skin mySkin;
    private boolean moveToGame = false;
    private float timer = 0.0f;
    private boolean moveToLobby = false;
    private boolean moveToMatchmaking = false;
    private BitmapFont font;

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

    public HomeScreen(MultipleScenes game) {
        this.game = game;
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                Gdx.app.log("WS", "Got message: " + packet);
                System.out.println("called from homescreen");
                if(!packet.matches("Error")){
                    String[] clientMessage = packet.split("/");
                    if(clientMessage[0].matches("LobbyInfo")) {
                        game.getGameLobby().setLobbyCode(clientMessage[0]);
                        game.getGameLobby().setLobbyIndex(Integer.valueOf(clientMessage[1]));
                        moveToLobby = true;
                        return FULLY_HANDLED;
                   }
                }
                return FULLY_HANDLED;
            }
        };
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/font.fnt"),
        Gdx.files.internal("font/font.png"), false);

        moveToGame = false;
        game.getSocket().removeListener(game.getListener());
        game.setListener(getListener());
        game.getSocket().addListener(game.getListener());
        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        //Isaac add-img_background
        tex = new Texture(Gdx.files.internal("image/lobbyDraft.png"));
        region = new TextureRegion(tex,0,0,512,512);
        image = new Image(region);
        image.setPosition(0,0);
        image.setSize(360,640);
        stage.addActor(image);

        final TextField textField = new TextField("Lobby Code:", mySkin);
        textField.setX(Gdx.graphics.getWidth() / 2 - 100);
        textField.setY(Gdx.graphics.getHeight() / 2 - 100);
        textField.setWidth(200);
        textField.setText("");
        textField.setHeight(50);
        textField.setVisible(false);
        textField.setText("Enter Lobby Code :)");

        textField.addListener(new InputListener(){

            @Override
            public boolean keyDown (InputEvent event, int keycode) {
                if(keycode == Input.Keys.ENTER){
                    textField.setVisible(false);
                    game.getSocket().send("LobbyMessage/JoinLobby/"+textField.getText()+"/"+game.getPlayerName());
                }
                return false;
            }
        });

        joinLobby = new TextButton("Join Lobby", mySkin, "toggle");
        joinLobby.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 - 25, 150, 50);
        joinLobby.getLabel().setFontScale(0.6f, 0.6f);
        joinLobby.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                joinLobby.setText("Join Lobby");
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                textField.setVisible(true);
                return true;
            }
        });

        createLobby = new TextButton("Create Lobby", mySkin, "toggle");
        createLobby.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 + 50, 150, 50);
        createLobby.getLabel().setFontScale(0.6f, 0.6f);
        createLobby.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                createLobby.setText("Join Lobby");
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new LobbyScreen(game));
                return true;
            }
        });

        findMatch = new TextButton("Find match", mySkin, "toggle");
        findMatch.setBounds(Gdx.graphics.getWidth() / 2 - 75, Gdx.graphics.getHeight() / 2 + 125, 150, 50);
        findMatch.getLabel().setFontScale(0.6f, 0.6f);


        findMatch.addListener(new InputListener(){

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                moveToMatchmaking = true;
                return true;
            }
        });

        //Isaac-add img button for test
        tex2 = new Texture(Gdx.files.internal("button/button2.png"));
        TextureRegion[][] temp = TextureRegion.split(tex2,240,240);
        buttonUp = temp[0][0];
        buttonDown = temp[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(65,50);
        //button.setSize(480,480);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "CLICK!!");
            }
        });

        stage.addActor(findMatch);
        stage.addActor(textField);
        stage.addActor(createLobby);
        stage.addActor(joinLobby);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        game.getBatch().begin();
        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

        if(game.getSocket().isOpen()) {
            if (moveToLobby) {
                game.setScreen(new LoadingScreen(game));
            }
            if (moveToMatchmaking) {

                game.setScreen(new FindingMatch(game));
            }
            font.draw(game.getBatch(), "Hello "+game.getPlayerName()+"!\nWelcome to Draw Buddy\n", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2 + 300);
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
    }

    @Override
    public void dispose(){
        game.dispose();
        stage.dispose();
        mySkin.dispose();
        font.dispose();
    }
}