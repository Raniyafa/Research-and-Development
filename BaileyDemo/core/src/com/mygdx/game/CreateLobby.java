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

    public CreateLobby(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show(){
        font = new BitmapFont(Gdx.files.internal("font/font.fnt"),
                Gdx.files.internal("font/font.png"), false);

        game.getSocket().removeListener(game.getListener());
        game.setListener(getListener());
        game.getSocket().addListener(game.getListener());
        shapeRenderer = new ShapeRenderer();

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        Gdx.graphics.setWindowedMode(360, 640);

        exitLobby = new TextButton("Go Back", mySkin, "toggle");
        exitLobby.setBounds(Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() / 2 - 150, 150, 50);
        exitLobby.getLabel().setFontScale(0.6f, 0.6f);
        exitLobby.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                exitLobby.setText("Go Back");
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(game.getSocket().isOpen()) {
                    game.setScreen(new HomeScreen(game));
                }
                return true;
            }
        });
        stage.addActor(exitLobby);

        createLobby = new TextButton("Create Lobby", mySkin, "toggle");
        createLobby.setBounds(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 150, 150, 50);
        createLobby.getLabel().setFontScale(0.6f, 0.6f);
        createLobby.addListener(new InputListener(){

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                createLobby.setText("Create Lobby");
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(game.getSocket().isOpen() && lobbyType.getSelected() != null && lobbyType.getSelected() != "Lobby Type:") {
                    game.getSocket().send("LobbyMessage/CreateLobby/"+lobbyType.getSelected());
                }
                return true;
            }
        });
        stage.addActor(createLobby);

        lobbyType = new SelectBox<String>(mySkin);
        lobbyType.setItems("Regular", "Rush", "Test");
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

                if (packet.contains("LobbyInfo")) {
                    String[] serverMessage = packet.split("/");
                    CreateLobby(Integer.valueOf(serverMessage[1]), serverMessage[2]);
                    game.getGameLobby().setWordTopic(serverMessage[3]);
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