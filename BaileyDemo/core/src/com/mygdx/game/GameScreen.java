package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;

import java.text.DecimalFormat;

public class GameScreen extends ScreenAdapter {

    private SpriteBatch batch;
    private BitmapFont font;

    int lobbyID = 0;
    Json json = new Json();
    MultipleScenes game;
    Shape[] shapeArr;

    final float UPDATE_TIME = 1 / 240.0f;

    boolean canvasUpdated = false;
    boolean triangleHover;
    boolean squareHover;
    boolean circleHover;
    boolean sizeHover;
    boolean colourHover;
    boolean readyToDraw = false;
    boolean loadingData;

    SelectBox<String> drawSize;
    SelectBox<String> colour;
    TextButton triangle;
    TextButton circle;
    TextButton square;
    Stage stage;
    Skin mySkin;

    String selectedColour = "Red";
    String selectedType = "circle";

    float timer;
    float serverInfoTimer = 0.0f;
    float turnTimer = 0.0f;
    float timeToWait = 2.0f;
    float waitTime;
    float drawTimer = 0.0f;

    int serverSend = 0;
    int serverReceive = 0;
    int selectedSize = 5;
    int capacity = 1000;
    int currentSize = 0;

    boolean myTurn;
    public boolean gameFinished;


    public GameScreen(MultipleScenes game) {
        this.game = game;
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {

            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                if(!packet.contains("CanvasInfo")) {
                    Gdx.app.log("WS", "Got message: " + packet);
                }

                String[] clientMessage = packet.split("/");
                if(clientMessage[0].matches("CanvasInfo")){

                    int index = 2;
                    int size = currentSize + clientMessage.length - 2 / 4;

                    if(currentSize + size > capacity){
                        increaseArrSize();
                    }

                    for (int i = currentSize; i <= size; i++) {
                        float[] colour = new float[3];
                        colour[0] = 1.0f;
                        colour[1] = 0.0f;
                        colour[2] = 0.0f;

                        shapeArr[i] = new Shape(Integer.valueOf(clientMessage[index+2]), Integer.valueOf(clientMessage[index+3]), 10, clientMessage[index], colour);
                        index += 4;
                        currentSize++;
                    }
                }
                else if(clientMessage[0].matches("YourTurn")){
                    turnTimer = 0.0f;
                    myTurn = true;
                }
                else if(clientMessage[0].matches("GameFinished")){
                    gameFinished = true;
                }
                return FULLY_HANDLED;
            }
        };
    }

    @Override
    public void show() {
        font = new BitmapFont(Gdx.files.internal("font/font.fnt"),
        Gdx.files.internal("font/font.png"), false);

        game.getSocket().removeListener(game.listener);
        game.listener = getListener();
        game.getSocket().addListener(game.listener);

        game.getSocket().send("Ready/"+game.gameLobby.lobbyIndex);

        shapeArr = new Shape[capacity];
        for (int i = 0; i <= capacity - 1; i++) {
            shapeArr[i] = new Shape();
        }

        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        drawSize = new SelectBox<String>(mySkin);
        drawSize.setItems("5", "10", "15", "20", "25", "30");
        drawSize.setName("Pencil Size");
        drawSize.setBounds(390, Gdx.graphics.getHeight() - 33, 130, 33);
        drawSize.setSelected("5");
        stage.addActor(drawSize);

        drawSize.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                sizeHover = true;
            }

        });
        colour = new SelectBox<String>(mySkin);
        colour.setItems("Red", "Green", "Blue", "Yellow", "Black", "White");
        colour.setName("Pencil Colour");
        colour.setBounds(520, Gdx.graphics.getHeight() - 33, 70, 33);
        colour.setSelected("Red");
        stage.addActor(colour);

        colour.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                colourHover = true;
            }
        });

        triangle = new TextButton("Triangle", mySkin, "toggle");
        triangle.setBounds(260, Gdx.graphics.getHeight() - 33, 70, 33);
        triangle.getLabel().setFontScale(0.6f, 0.6f);

        triangle.addListener(new ClickListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                triangleHover = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                triangleHover = false;
                triangle.clearActions();
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedType.matches("circle")) {
                    circle.toggle();
                } else if (selectedType.matches("square")) {
                    square.toggle();
                }
                selectedType = "triangle";
                triangle.setText("Triangle Selected");
                square.setText("Square");
                circle.setText("Circle");
            }
        });
        stage.addActor(triangle);

        circle = new TextButton("Circle Selected", mySkin, "toggle");
        circle.setBounds(0, Gdx.graphics.getHeight() - 33, 130, 33);
        circle.getLabel().setFontScale(0.6f, 0.6f);
        circle.toggle();

        circle.addListener(new ClickListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                circleHover = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                circleHover = false;
                circle.clearActions();
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedType.matches("square")) {
                    square.toggle();
                } else if (selectedType.matches("triangle")) {
                    triangle.toggle();
                }
                selectedType = "circle";
                circle.setText("Circle Selected");
                square.setText("Square");
                triangle.setText("Triangle");
            }
        });

        stage.addActor(circle);
        square = new TextButton("Square", mySkin, "toggle");
        square.setBounds(130, Gdx.graphics.getHeight() - 33, 130, 33);
        square.getLabel().setFontScale(0.6f, 0.6f);

        square.addListener(new ClickListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                squareHover = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                squareHover = false;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedType.matches("circle")) {
                    circle.toggle();
                } else if (selectedType.matches("triangle")) {
                    triangle.toggle();
                }
                selectedType = "square";
                square.setText("Square Selected");
                circle.setText("Circle");
                triangle.setText("Triangle");
            }
        });

        stage.addActor(square);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        serverInfoTimer += delta;
        timer += delta;
        if(timer > UPDATE_TIME){
            timer = 0.0f;
            getCanvasUpdates();
        }

        turnTimer += delta;

        DecimalFormat dfrmt = new DecimalFormat("#.##");
        if(myTurn){
            String temp = "Your turn to draw! "+dfrmt.format(10.0f - turnTimer);
            font.draw(game.batch, temp, Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2);
            if(turnTimer >= 10.0f){
                myTurn = false;
                game.getSocket().send("TurnFinished/"+game.gameLobby.lobbyIndex);
                turnTimer = 0.0f;
            }
        }
        else{
            String temp = "Your partner is drawing! "+dfrmt.format(10.0f - turnTimer);
            font.draw(game.batch, temp, Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2);
        }
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);

        drawTimer += delta;

        game.batch.end();

        if (serverInfoTimer > 3.0f) {
          //  System.out.println("Server receives: " + serverReceive + "\nServer sends: " + serverSend);
            serverInfoTimer = 0.0f;
        }

        waitTime += delta;

        if (waitTime >= timeToWait && readyToDraw == false) {
            readyToDraw = true;
            waitTime = 0.0f;
            timeToWait = 0.02f;
        }

        if (currentSize + 50 >= capacity) {

            increaseArrSize();
        }

        if (readyToDraw && !circleHover && !squareHover && !triangleHover && Gdx.input.getY() > 50.0f && drawTimer > 0.02f && myTurn) {
            if (Gdx.input.isTouched()) {
                if (!colourHover && !sizeHover) {
                    storeMouseLoc(delta);
                    canvasUpdated = true;
                } else if (colour.getSelected() != selectedColour || selectedSize != Integer.valueOf(drawSize.getSelected())) {
                    selectedColour = colour.getSelected();
                    selectedSize = Integer.valueOf(drawSize.getSelected());
                    storeMouseLoc(delta);
                    canvasUpdated = true;
                    colourHover = false;
                    sizeHover = false;
                }

            }
        }

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        game.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1);
        game.shapeRenderer.circle(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 10.0f);

     //   System.out.println("size of arr on client = "+shapeArr.length);

            for (int i = 0; i <= shapeArr.length - 1; i++) {


               // game.shapeRenderer.setColor(shapeArr[i].rgb[0], shapeArr[i].rgb[1], shapeArr[i].rgb[2], 1);
                game.shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1);
                try {
                    String temp = shapeArr[i].type;

                    if (temp.matches("circle")) {
                      //  System.out.println("shapeArr["+i+"] has type circle, drawing now");
                    //    System.out.println("drawing shapeArr index "+i+" type = "+shapeArr[i].type+" x = "+shapeArr[i].x+" y = "+shapeArr[i].y+"");
                        game.shapeRenderer.circle(shapeArr[i].x, shapeArr[i].y, 10);

                    } else if (temp.matches("square")) {
                        game.shapeRenderer.rect(shapeArr[i].x, shapeArr[i].y, 10, 10);

                    } else if (temp.matches("triangle")) {
                        game.shapeRenderer.triangle(shapeArr[i].x - 30.0f, shapeArr[i].y, shapeArr[i].x + 30.0f, shapeArr[i].y, shapeArr[i].x, shapeArr[i].y + 45.0f);
                    }
                } catch (Exception e) {
                    System.out.println("Null error drawing shapeArr[" + i + "]");

                }
            }
            readyToDraw = false;


        game.shapeRenderer.end();

        stage.act();
        stage.draw();

        if(gameFinished){
            game.setScreen(new HomeScreen(game));
        }
    }



    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public void storeMouseLoc(float delta) {
        shapeArr[currentSize] = new Shape(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        shapeArr[currentSize].colour = colour.getSelected();

        drawTimer = 0.0f;

        float[] temp = {0.0f, 0.2f, 0.0f};

        if (colour.getSelected().matches(("Red"))) {
            shapeArr[currentSize].rgb[0] = 1.0f;
            shapeArr[currentSize].rgb[1] = 0.0f;
            shapeArr[currentSize].rgb[2] = 0.0f;
        } else if (colour.getSelected().matches(("Green"))) {
            shapeArr[currentSize].rgb[0] = 0.0f;
            shapeArr[currentSize].rgb[1] = 1.0f;
            shapeArr[currentSize].rgb[2] = 0.0f;
        } else if (colour.getSelected().matches(("Blue"))) {
            shapeArr[currentSize].rgb[0] = 0.0f;
            shapeArr[currentSize].rgb[1] = 0.0f;
            shapeArr[currentSize].rgb[2] = 1.0f;
        } else if (colour.getSelected().matches(("Yellow"))) {
            shapeArr[currentSize].rgb[0] = 1.0f;
            shapeArr[currentSize].rgb[1] = 1.0f;
            shapeArr[currentSize].rgb[2] = 0.0f;
        } else if (colour.getSelected().matches(("Black"))) {
            shapeArr[currentSize].rgb[0] = 0.0f;
            shapeArr[currentSize].rgb[1] = 0.0f;
            shapeArr[currentSize].rgb[2] = 0.0f;
        } else if (colour.getSelected().matches(("White"))) {
            shapeArr[currentSize].rgb[0] = 1.0f;
            shapeArr[currentSize].rgb[1] = 1.0f;
            shapeArr[currentSize].rgb[2] = 1.0f;
        }
        shapeArr[currentSize].type = selectedType;

        if (selectedType == "circle") {
            shapeArr[currentSize].radius = Integer.valueOf(drawSize.getSelected());
        } else if (selectedType == "square") {
            shapeArr[currentSize].width = Integer.valueOf(drawSize.getSelected());
            shapeArr[currentSize].height = Integer.valueOf(drawSize.getSelected());
        } else {
            shapeArr[currentSize].corners = Integer.valueOf(drawSize.getSelected());
            shapeArr[currentSize].radius = Integer.valueOf(drawSize.getSelected());
        }
        game.getSocket().send("GameMessage/"+"UpdateCanvas/"+String.valueOf(lobbyID)+"/"+shapeArr[currentSize].type
        +  ":" + shapeArr[currentSize].colour + ":" +   shapeArr[currentSize].x + ":" +shapeArr[currentSize].y);

        currentSize++;
    }

    public void getCanvasUpdates(){
        game.getSocket().send("GameMessage/"+"RequestCanvas/"+lobbyID+"/"+currentSize);
    }

    public void increaseArrSize(){
        Shape[] placeholder = shapeArr;

        capacity += 1000;
        shapeArr = new Shape[capacity];


        for (int k = 0; k <= (capacity - 1001); k++) {
            shapeArr[k] = placeholder[k];
        }
    }
}

