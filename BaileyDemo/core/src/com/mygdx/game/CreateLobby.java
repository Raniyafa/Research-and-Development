package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

public class CreateLobby extends ScreenAdapter {

    private MultipleScenes game;
    private TextButton createLobby;
    private TextButton exitLobby;
    private Stage stage;
    private Skin mySkin;
    private BitmapFont font;
    private SelectBox<String> lobbyType;
    private boolean moveToLobby = false;
    private ShapeRenderer shapeRenderer;

    private Texture tex;
    private Image image;
    private TextureRegion region;

    private TextureRegionDrawable up;
    private TextureRegionDrawable down;
    private TextureRegion buttonUp;
    private TextureRegion buttonDown;
    private Texture tex2;
    private ImageButton button;

    public CreateLobby(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/dbfont.fnt"),
                Gdx.files.internal("font/dbfont.png"), false);

        game.setListener(getListener());
        shapeRenderer = new ShapeRenderer();

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        Gdx.graphics.setWindowedMode(360, 640);

        tex = new Texture(Gdx.files.internal("image/CreateRoom.png"));
        region = new TextureRegion(tex,0,0,750,1334);
        image = new Image(region);
        image.setPosition(0,0);
        image.setSize(360 * (Gdx.graphics.getWidth() / 360),750 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(image);

        tex2 = new Texture(Gdx.files.internal("button/BackButton.png"));
        TextureRegion[][] temp_0 = TextureRegion.split(tex2,210,60);
        buttonUp = temp_0[0][0];
        buttonDown = temp_0[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth()/2 - 180,Gdx.graphics.getHeight() / 2 + 270);
        button.setSize(105,30);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.getSocket().isOpen()) {
                    game.setScreen(new HomeScreen(game));
                }
                SoundManager.button.play();
            }
        });

//        exitLobby = new TextButton("Go Back", mySkin, "toggle");
//        exitLobby.setBounds(Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() / 2 - 150, 150, 50);
//        exitLobby.getLabel().setFontScale(0.6f, 0.6f);
//        exitLobby.addListener(new InputListener(){
//
//            @Override
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                exitLobby.setText("Go Back");
//            }
//            @Override
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                SoundManager.button.play();
//                if(game.getSocket().isOpen()) {
//                    game.setScreen(new HomeScreen(game));
//                }
//                return true;
//            }
//        });
//        stage.addActor(exitLobby);

        //Adding Create Button
        tex2 = new Texture(Gdx.files.internal("button/CreateButton.png"));
        TextureRegion[][] temp_2 = TextureRegion.split(tex2,480,140);
        buttonUp = temp_2[0][0];
        buttonDown = temp_2[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(Gdx.graphics.getWidth() / 2 - 120,Gdx.graphics.getHeight()/2 - 250);
        button.setSize(240,70);
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.button.play();
                if(game.getSocket().isOpen() && lobbyType.getSelected() != null && lobbyType.getSelected() != "Lobby Type:") {
                    game.getSocket().send("LobbyMessage/CreateLobby/"+lobbyType.getSelected());
                }
            }
        });

//        createLobby = new TextButton("Create Lobby", mySkin, "toggle");
//        createLobby.setBounds(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 150, 150, 50);
//        createLobby.getLabel().setFontScale(0.6f, 0.6f);
//        createLobby.addListener(new InputListener(){
//
//            @Override
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                createLobby.setText("Create Lobby");
//            }
//            @Override
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                SoundManager.button.play();
//                if(game.getSocket().isOpen() && lobbyType.getSelected() != null && lobbyType.getSelected() != "Lobby Type:") {
//                    game.getSocket().send("LobbyMessage/CreateLobby/"+lobbyType.getSelected());
//                }
//                return true;
//            }
//        });
//        stage.addActor(createLobby);

        lobbyType = new SelectBox<String>(mySkin);
        lobbyType.setItems("Regular", "One Line", "Test");
        lobbyType.setName("Lobby Type:");
        lobbyType.setBounds((Gdx.graphics.getWidth() / 2 - 50), Gdx.graphics.getHeight() / 2, 200, 60);
        lobbyType.setColor(0.0f, 0.0f, 0.0f, 1.0f);
        lobbyType.setSelected("5");
        stage.addActor(lobbyType);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if(moveToLobby){
            game.setScreen(new LobbyScreen(game));
        }

        Gdx.gl.glClearColor(0, 0, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        shapeRenderer.rect((Gdx.graphics.getWidth() / 2 - 70), Gdx.graphics.getHeight() / 2 + 10, 125, 35);
        shapeRenderer.end();
        stage.act();

        stage.draw();
        game.getBatch().begin();

        if(game.getSocket().isClosed() && !game.getSocket().isConnecting()){
            game.getSocket().connect();
            font.draw( game.getBatch(), "CONNECTION LOST TO SERVER\n", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2);
        }

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
                    CreateLobby(Integer.valueOf(serverMessage[1]), serverMessage[2]);
                    game.getGameLobby().setWordTopic(serverMessage[3]);
                    game.getGameLobby().setGameMode(serverMessage[4]);
                    moveToLobby = true;
                }
                return FULLY_HANDLED;
            }
        };
    }

    public void CreateLobby(int id, String code){
        game.setGameLobby(new GameLobby(code, id));
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