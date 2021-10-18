package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameScreen extends ScreenAdapter {

    MultipleScenes game;
    private Socket socket;
    final float UPDATE_TIME = 1/120.0f;
    boolean canvasUpdated = false;

    float timer;
    int[] xLoc;
    int[] yLoc;

    int[] xLoc2;
    int[] yLoc2;

    int capacity = 1000;
    int currentSize = 0;
    float waitTime;
    boolean readyToDraw = false;

    float circleX = 300;
    float circleY = 150;
    float circleRadius = 50;

    float goalX = 100.0f;
    float goalY = 100.0f;

    float xSpeed = 4;
    float ySpeed = 3;

    public GameScreen(MultipleScenes game) {
        this.game = game;
    }


    public void create() {
        xLoc = new int[1000];
        yLoc = new int[1000];
    }


    @Override
    public void show() {
        xLoc = new int[1000];
        yLoc = new int[1000];
        connectSocket();
        configSocketEvents();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateServer(delta);

        waitTime += delta;

        if(waitTime >= 1.0f){
            readyToDraw = true;
        }

        if(currentSize >= capacity - 1){
            int[] placeHolderX = xLoc;
            int[] placeHolderY = yLoc;

            capacity += 1000;
            xLoc = new int[capacity];
            yLoc = new int[capacity];

            for(int i = 0; i <= (capacity - 1001); i++){
                xLoc[i] = placeHolderX[i];
                yLoc[i] = placeHolderY[i];
            }
        }

        if(readyToDraw) {
            if (Gdx.input.isTouched()) {

                if (currentSize == 0) {
                    canvasUpdated = true;
                    storeMouseLoc();
                } else if ((Gdx.input.getX() > xLoc[currentSize] || Gdx.input.getX() < xLoc[currentSize]) && (Gdx.input.getY() > xLoc[currentSize] || Gdx.input.getY() < xLoc[currentSize])) {
                    canvasUpdated = true;
                    storeMouseLoc();
                }
            }
        }


        circleX = Gdx.input.getX();
        circleY = -Gdx.input.getY() + 480.0f;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(int i = 0; i <= currentSize - 1; i++){
            game.shapeRenderer.setColor(0, 1, 0, 1);
            game.shapeRenderer.circle(xLoc[i], Gdx.graphics.getHeight() - yLoc[i], 5);
        }


        game.shapeRenderer.setColor(0, 1, 0, 1);
        game.shapeRenderer.circle(circleX, circleY, 5);


        game.shapeRenderer.end();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public void storeMouseLoc(){
        xLoc[currentSize] = Gdx.input.getX();
        yLoc[currentSize] = (Gdx.input.getY());
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

                JSONArray data = (JSONArray) args[0];

              //  if (data.length() - 1 > currentSize) {
                    capacity = data.length() - 1;
                    xLoc = new int[capacity];
                    yLoc = new int[capacity];
                    currentSize = capacity;

                    for (int i = 0; i <= capacity - 1; i++) {

                        try {
                            xLoc[i] = data.getJSONObject((i)).getInt("x");
                            yLoc[i] = data.getJSONObject((i)).getInt("y");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
           // }
        });

    }

    public void updateServer(float dt){
        timer+= dt;

        if(timer > UPDATE_TIME && canvasUpdated && currentSize > 0){
            System.out.println("updating server");
            timer = 0.0f;
            JSONObject data = new JSONObject();
            try{
                 for(int i = 0; i <= currentSize - 1; i++) {
                     data.put("x", xLoc[i]);
                     data.put("y", yLoc[i]);
                 }
                canvasUpdated = false;
                socket.emit("sendCanvas", data);
            }catch(JSONException e){
            Gdx.app.log("SOCKET.IO", "Error sending data to server");
            }

        }
    }
}
