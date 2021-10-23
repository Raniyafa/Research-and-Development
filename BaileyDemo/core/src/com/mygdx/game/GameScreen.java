package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameScreen extends ScreenAdapter {

    MultipleScenes game;
    private Socket socket;
    final float UPDATE_TIME = 1/ 240.0f;
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
    String selectedType = "circle";
    Shape[] shapeArr;
    float timer;
    float serverInfoTimer = 0.0f;
    float timeToWait = 2.0f;
    float waitTime;
    int serverSend = 0;
    int serverReceive = 0;
    int capacity = 1000;
    int currentSize = 0;
    int selectedSize = 5;
    String selectedColour = "Red";

    public GameScreen(MultipleScenes game) {
        this.game = game;
    }

    @Override
    public void show() {
        shapeArr = new Shape[capacity];
        for(int i = 0; i <= capacity - 1; i++){
            shapeArr[i] = new Shape();
        }
        connectSocket();
        configSocketEvents();

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
            public void clicked (InputEvent event, float x, float y) {
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
            public void clicked (InputEvent event, float x, float y) {
                    colourHover = true;
            }
        });

        triangle = new TextButton("Triangle", mySkin, "toggle");
        triangle.setBounds(260, Gdx.graphics.getHeight() - 33, 70, 33);
        triangle.getLabel().setFontScale(0.6f, 0.6f);

        triangle.addListener(new ClickListener(){

            @Override
            public void  enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
                triangleHover = true;
            }

            @Override
            public void  exit(InputEvent event, float x, float y, int pointer, Actor toActor){
                triangleHover = false;
                triangle.clearActions();
            }

            @Override
            public void clicked (InputEvent event, float x, float y) {
                if(selectedType.matches("circle")){
                    circle.toggle();
                }
                else if (selectedType.matches("square")){
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

        circle.addListener(new ClickListener(){

            @Override
            public void  enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
                circleHover = true;
            }

            @Override
            public void  exit(InputEvent event, float x, float y, int pointer, Actor toActor){
                circleHover = false;
                circle.clearActions();
            }

            @Override
            public void clicked (InputEvent event, float x, float y) {
                if(selectedType.matches("square")){
                    square.toggle();
                }
                else if (selectedType.matches("triangle")){
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

        square.addListener(new ClickListener(){

            @Override
            public void  enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
                squareHover = true;
            }

            @Override
            public void  exit(InputEvent event, float x, float y, int pointer, Actor toActor){
                squareHover = false;
            }

            @Override
            public void clicked (InputEvent event, float x, float y) {
                if(selectedType.matches("circle")){
                    circle.toggle();
                }
                else if (selectedType.matches("triangle")){
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
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        serverInfoTimer += delta;



        if(serverInfoTimer > 3.0f){
            System.out.println("Server receives: "+serverReceive+"\nServer sends: "+serverSend);
            serverInfoTimer = 0.0f;
        }

        waitTime += delta;

        if(waitTime >= timeToWait && readyToDraw == false){
            readyToDraw = true;
            waitTime = 0.0f;
            timeToWait = 0.02f;
        }

        if(currentSize>= capacity){

            Shape[] placeholder = shapeArr;

            capacity += 1000;
            shapeArr = new Shape[capacity];


            for(int k = 0; k <= (capacity - 1001); k++){
                shapeArr[k] = placeholder[k];
            }
        }

        if(readyToDraw && !circleHover && !squareHover && !triangleHover && Gdx.input.getY() > 50.0f) {
            if (Gdx.input.isTouched()) {
                    if(!colourHover && !sizeHover)  {
                        storeMouseLoc(delta);
                        canvasUpdated = true;
                    }
                    else if(colour.getSelected() != selectedColour || selectedSize != Integer.valueOf(drawSize.getSelected())){
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


            if(currentSize > 0) {
                for (int i = 0; i <= currentSize - 2; i++) {
                    game.shapeRenderer.setColor(shapeArr[i].rgb[0], shapeArr[i].rgb[1], shapeArr[i].rgb[2], 1);
                    try {
                        String temp = shapeArr[i].type;

                        if (temp.matches("circle")) {

                            game.shapeRenderer.circle(shapeArr[i].x, shapeArr[i].y, shapeArr[i].radius);

                        } else if (temp.matches("square")) {
                            game.shapeRenderer.rect(shapeArr[i].x, shapeArr[i].y, shapeArr[i].width, shapeArr[i].height);

                        } else if (temp.matches("triangle")) {
                            game.shapeRenderer.triangle(shapeArr[i].x - 30.0f, shapeArr[i].y, shapeArr[i].x + 30.0f, shapeArr[i].y, shapeArr[i].x, shapeArr[i].y + 45.0f);
                        }
                    } catch (Exception e) {
                        System.out.println("Null error drawing shapeArr[" + i + "]");

                    }
                }
                readyToDraw = false;
            }

        game.shapeRenderer.end();
        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public void storeMouseLoc(float delta){
        shapeArr[currentSize] = new Shape(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        shapeArr[currentSize].colour = colour.getSelected();

        float[] temp = {0.0f, 0.2f, 0.0f};

        if(colour.getSelected().matches(("Red"))){
            shapeArr[currentSize].rgb[0] = 1.0f;
            shapeArr[currentSize].rgb[1] = 0.0f;
            shapeArr[currentSize].rgb[2] = 0.0f;
        }
        else if(colour.getSelected().matches(("Green"))) {
            shapeArr[currentSize].rgb[0] = 0.0f;
            shapeArr[currentSize].rgb[1] = 1.0f;
            shapeArr[currentSize].rgb[2] = 0.0f;
        }
        else if(colour.getSelected().matches(("Blue"))) {
            shapeArr[currentSize].rgb[0] = 0.0f;
            shapeArr[currentSize].rgb[1] = 0.0f;
            shapeArr[currentSize].rgb[2] = 1.0f;
        }
        else if(colour.getSelected().matches(("Yellow"))) {
            shapeArr[currentSize].rgb[0] = 1.0f;
            shapeArr[currentSize].rgb[1] = 1.0f;
            shapeArr[currentSize].rgb[2] = 0.0f;
        }
        else if(colour.getSelected().matches(("Black"))) {
            shapeArr[currentSize].rgb[0] = 0.0f;
            shapeArr[currentSize].rgb[1] = 0.0f;
            shapeArr[currentSize].rgb[2] = 0.0f;
        }
        else if(colour.getSelected().matches(("White"))) {
            shapeArr[currentSize].rgb[0] = 1.0f;
            shapeArr[currentSize].rgb[1] = 1.0f;
            shapeArr[currentSize].rgb[2] = 1.0f;
        }
        shapeArr[currentSize].type = selectedType;

        if(selectedType == "circle"){
            shapeArr[currentSize].radius = Integer.valueOf(drawSize.getSelected());
        }
        else if(selectedType == "square"){
            shapeArr[currentSize].width = Integer.valueOf(drawSize.getSelected());
            shapeArr[currentSize].height = Integer.valueOf(drawSize.getSelected());
        }
        else{
            shapeArr[currentSize].corners = Integer.valueOf(drawSize.getSelected());
            shapeArr[currentSize].radius = Integer.valueOf(drawSize.getSelected());
        }
        updateServer(delta);
        currentSize++;
    }

    public void connectSocket(){
        try {
        socket = IO.socket("http://localhost:8080");
        socket.connect();

        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Connected to server");
            }
        }).on("socketID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    Gdx.app.log("SocketIO", "MY ID:"+ id);
                } catch (JSONException e) {
                    System.out.println("Error getting ID");
                }
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    Gdx.app.log("SocketIO", "New Player Connected:"+ id);
                } catch (JSONException e) {
                    System.out.println("Error getting new Player ID");
                }
            }
        }).on("updateCanvas", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                loadingData = true;
                serverReceive++;
                JSONArray data = (JSONArray) args[0];
                capacity = data.length() - 1;
                Shape[] tempArr = new Shape[capacity];
                for(int k = 0; k <= capacity - 1; k++){
                    tempArr[k] = new Shape();
                }
                    currentSize = capacity - 1;
                    for (int i = 0; i <= currentSize - 1; i++) {
                        try {
                            String colourString = data.getJSONObject((i)).getString("colour");
                            tempArr[i].rgb[0] = 1.0f;
                            tempArr[i].rgb[1] = 0.0f;
                            tempArr[i].rgb[2] = 0.0f;
                            switch(colourString){
                                case("Green"):
                                    tempArr[i].rgb[0] = 0.0f;
                                    tempArr[i].rgb[1] = 1.0f;
                                    tempArr[i].rgb[2] = 0.0f;
                                    break;
                                case("Blue"):
                                    tempArr[i].rgb[0] = 0.0f;
                                    tempArr[i].rgb[1] = 0.0f;
                                    tempArr[i].rgb[2] = 1.0f;
                                    break;
                                case("Yellow"):
                                    tempArr[i].rgb[0] = 1.0f;
                                    tempArr[i].rgb[1] = 1.0f;
                                    tempArr[i].rgb[2] = 0.0f;
                                    break;
                                case("Black"):
                                    tempArr[i].rgb[0] = 0.0f;
                                    tempArr[i].rgb[1] = 0.0f;
                                    tempArr[i].rgb[2] = 0.0f;
                                    break;
                                case("White"):
                                    tempArr[i].rgb[0] = 1.0f;
                                    tempArr[i].rgb[1] = 1.0f;
                                    tempArr[i].rgb[2] = 1.0f;
                                    break;
                            }

                            tempArr[i].type = data.getJSONObject((i)).getString("type");
                            tempArr[i].x = data.getJSONObject((i)).getInt("x");
                            tempArr[i].y = data.getJSONObject((i)).getInt("y");

                            if(tempArr[i].type.matches("circle")){
                                tempArr[i].radius = data.getJSONObject((i)).getInt("radius");
                            }
                            else if(tempArr[i].type.matches("square")) {
                                tempArr[i].height = data.getJSONObject((i)).getInt("height");
                                tempArr[i].width = data.getJSONObject((i)).getInt("width");

                            }
                            else if(tempArr[i].type.matches("triangle")) {
                                tempArr[i].radius = data.getJSONObject((i)).getInt("radius");
                                tempArr[i].corners = data.getJSONObject((i)).getInt("corners");
                            }

                            } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    shapeArr = tempArr;
                loadingData = false;
                }
        });

    }

    public void updateServer(float dt){
        timer+= dt;
        serverSend++;
        if(timer > UPDATE_TIME && canvasUpdated && currentSize > 0){
            timer = 0.0f;
            JSONObject data = new JSONObject();
            try{
                 for(int i = 0; i <= currentSize - 1; i++) {
                     data.put("type", shapeArr[i].type);
                     data.put("x", shapeArr[i].x);
                     data.put("y", shapeArr[i].y);
                     data.put("colour", shapeArr[i].colour);

                     if(shapeArr[i].type.matches("square")){
                         data.put("height", shapeArr[i].height);
                         data.put("width", shapeArr[i].width);
                     }
                     if(shapeArr[i].type.matches("circle")){
                         data.put("radius", shapeArr[i].radius);
                     }
                     if(shapeArr[i].type.matches("triangle")){
                         data.put("radius", shapeArr[i].radius);
                         data.put("corners",  shapeArr[i].corners);
                     }
                 }
                canvasUpdated = false;
                socket.emit("sendCanvas", data);
            }catch(JSONException e){
            Gdx.app.log("SOCKET.IO", "Error sending data to server");
            }

        }
    }
}
