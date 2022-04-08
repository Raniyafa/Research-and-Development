package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import java.util.ArrayList;

public class GameScreen extends ScreenAdapter {

    private BitmapFont font;
    private BitmapFont fontLarge;

    private int lobbyID = 0;
    private MultipleScenes game;
    private ArrayList<Shape> shapeArr;

    private ShapeRenderer shapeRenderer;

    final private float UPDATE_TIME = 1 / 30.0f;

    private boolean readyToDraw = false;

    private SelectBox<String> drawSize;
    private SelectBox<String> colour;
    private TextButton triangle;
    private TextButton circle;
    private TextButton square;
    private Stage stage;

    private String selectedColour = "Red";
    private String selectedType = "circle";

    private float timer;
    private float turnTimer = 0.0f;
    private float waitTime;
    private float drawTimer = 0.0f;
    private float disconnectedTimer = 0.0f;

    private int selectedSize = 5;
    private int received = 0;
    private int sent = 0;

    private boolean myTurn;
    private boolean gameFinished;

    public GameScreen(MultipleScenes game) {
        this.game = game;
    }

    private WebSocketAdapter getListener() {
        return new WebSocketAdapter() {

            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                //Network handler for this class, takes messages from the server and uses the information for the game

                if(!packet.contains("CanvasInfo")) {
                    Gdx.app.log("WS", "Got message: " + packet);
                }

                try {
                    String[] clientMessage = packet.split("/");
                    if (clientMessage[0].matches("CanvasInfo") && Integer.valueOf(clientMessage[1]) > shapeArr.size()) {

                        int index = 2;
                        int size = shapeArr.size() + ((clientMessage.length - 2) / 4);

                        System.out.println("shapeArr size = "+shapeArr.size() +" size = "+size);

                        for (int i = shapeArr.size(); i <= size - 1; i++) {
                            float[] colour = getRGB(clientMessage[index + 1]);

                            shapeArr.add(new Shape(Integer.valueOf(clientMessage[index + 2]), Integer.valueOf(clientMessage[index + 3]), 10, clientMessage[index], colour));
                            index += 4;
                            received++;
                        }
                    } else if (clientMessage[0].matches("YourTurn")) {
                        turnTimer = 0.0f;
                        myTurn = true;
                    } else if (clientMessage[0].matches("GameFinished")) {
                        game.setGameLobby(new GameLobby());
                        gameFinished = true;
                    }
                }catch(Exception e){
                    System.out.println("Exception in update from server " +e);
                }
                return FULLY_HANDLED;
            }
        };
    }

    @Override
    public void show() {
        //Font creations
        font = new BitmapFont(Gdx.files.internal("smallfont/smallfont.fnt"),
        Gdx.files.internal("smallfont/smallfont.png"), false);
        font.setColor(Color.BLACK);



        fontLarge = new BitmapFont(Gdx.files.internal("font/font.fnt"),
        Gdx.files.internal("font/font.png"), false);
        fontLarge.setColor(Color.BLACK);

        shapeRenderer = new ShapeRenderer();

        //Attach the network listener for this class to the WebSocket
        game.getSocket().removeListener(game.getListener());
        game.setListener(getListener());
        game.getSocket().addListener(game.getListener());

        game.getSocket().send("Ready/"+game.getGameLobby().getLobbyIndex());
        shapeArr = new ArrayList<>(5000);

        //Skin created for buttons and UI elements, stage set for this class
        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());

        //All the buttons and listeners for the buttons are added below
        drawSize = new SelectBox<String>(mySkin);
        drawSize.setItems("5", "10", "15", "20", "25", "30");
        drawSize.setName("Pencil Size");
        drawSize.setBounds(200, 35, 130, 33);
        drawSize.setSelected("5");
        stage.addActor(drawSize);

        colour = new SelectBox<String>(mySkin);
        colour.setItems("Red", "Green", "Blue", "Yellow", "Black", "White");
        colour.setName("Pencil Colour");
        colour.setBounds(100, 35, 70, 33);
        colour.setSelected("Red");
        stage.addActor(colour);


        triangle = new TextButton("Triangle", mySkin, "toggle");
        triangle.setBounds(260, Gdx.graphics.getHeight() - 33, 70, 33);
        triangle.getLabel().setFontScale(0.6f, 0.6f);

        triangle.addListener(new ClickListener() {

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
        drawTimer += delta;

        waitTime += delta;

        if (waitTime >= 2.0f) {
            readyToDraw = true;
        }

        timer += delta;

        //If enough time has passed since the last server update, then request a server update
        if (timer > UPDATE_TIME && !myTurn) {
            timer = 0.0f;
            getCanvasUpdates();
        }

        turnTimer += delta;

        if(drawTimer >= 0.0166f) {
        drawTimer = 0.0f;

            Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            //If the WebSocket is open (connected) then process the game controller logic
            if (game.getSocket().isOpen()) {

                //If the requirements are met then the current mouse location is captured and a shape is created corresponding to the selected
                //shape, colour and size
                if(myTurn && Gdx.input.isTouched()) {
                    if (Gdx.input.getY() > 50.0f && Gdx.input.getY() < Gdx.graphics.getHeight() - 50.0f) {
                        storeMouseLoc(delta);
                    }
                }

                int drawnAmount = 0;

                //Start the shape renderer with shapes being colour filled
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

                //Draw a cursor to show the current position of the mouse/finger
                shapeRenderer.circle(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 10.0f);

                //For loop which iterates through the shape array and draws each shape individually
                for (int i = 0; i <= shapeArr.size() - 1; i++) {
                    drawnAmount++;
                    try {
                        Shape drawShape = shapeArr.get(i);
                        shapeRenderer.setColor(drawShape.rgb[0], drawShape.rgb[1], drawShape.rgb[2], 1);
                        String temp = drawShape.type;

                        if (temp.matches("circle")) {
                            shapeRenderer.circle(drawShape.x, drawShape.y, 10);

                        } else if (temp.matches("square")) {
                            shapeRenderer.rect(drawShape.x, drawShape.y, 10, 10);

                        } else {
                            shapeRenderer.triangle(drawShape.x - 30.0f, drawShape.y, drawShape.x + 30.0f, drawShape.y, drawShape.x, drawShape.y + 45.0f);
                        }
                    } catch (Exception e) {
                        System.out.println("Null error drawing shapeArr[" + i + "]");
                    }
                }

                shapeRenderer.end();

                game.getBatch().begin();



                //In game information displayed here, such as whos turn it is and how many shapes have been sent/received by the client, and how many
                //shapes are being drawn to the screen.

                if (myTurn) {
                    String temp = "Your turn to draw! " + (Math.round(10.0f - turnTimer));
                    String temp2 = "\nReceived: " + received + "\nSent: " + sent + "\nDrawn amount = :" + drawnAmount;
                    fontLarge.draw(game.getBatch(), temp, 0, Gdx.graphics.getHeight() / 2 + 280);
                    font.draw(game.getBatch(), temp2, 0, 200);
                    if (turnTimer >= 10.0f) {
                        myTurn = false;
                        game.getSocket().send("TurnFinished/" + game.getGameLobby().getLobbyIndex());
                        turnTimer = 0.0f;
                    }
                } else {
                    String temp = "Your partner is drawing! " + (Math.round(10.0f - turnTimer));
                    String temp2 = "\nReceived: " + received + "\nSent: " + sent + "\nDrawn amount = :" + drawnAmount;
                    fontLarge.draw(game.getBatch(), temp, 0, Gdx.graphics.getHeight() / 2 + 280);
                    font.draw(game.getBatch(), temp2, 0, 200);
                }

            }


            //If game socket is closed and not connecting, attempt to connect
            else if (!game.getSocket().isConnecting()) {
                game.getSocket().connect();
            }

            //If game socket is closed then display that information to the user and attempt to re-connect, also keep track of the time spent disconnecting
            //If this time is longer than 5 seconds the client will exit to the main menu
            if (game.getSocket().isClosed()) {
                disconnectedTimer += delta;
                font.draw(game.getBatch(), "CONNECTION LOST TO SERVER\n", Gdx.graphics.getWidth() / 2 - 160, Gdx.graphics.getHeight() / 2);
                if (disconnectedTimer >= 5.0f) {
                    game.setScreen(new HomeScreen(game));
                }
            } else if (disconnectedTimer > 0.0f && !game.getSocket().isConnecting()) {
                disconnectedTimer = 0.0f;
            }

            game.getBatch().end();

            stage.act();
            stage.draw();

            //When the game is finished (The server sends a message to the clients to say so) then the client will exit to the main menu
            if (gameFinished) {
                game.setScreen(new HomeScreen(game));
            }
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public void storeMouseLoc(float delta) {

        int currentSize = shapeArr.size();
        //Gets the x and y of the cursor/press
        shapeArr.add(new Shape(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()));
        //Gets the selected colour
        shapeArr.get(currentSize).colour = colour.getSelected();
        //Gets the selected colour RGB value
        shapeArr.get(currentSize).rgb = getRGB(colour.getSelected());
        //Gets the selected shape (circle, square, triangle)
        shapeArr.get(currentSize).type = selectedType;

        //Sets the required values for the selected shape
        if (selectedType == "circle") {
            shapeArr.get(currentSize).radius = 10;
        } else if (selectedType == "square") {
            shapeArr.get(currentSize).width = 10;
            shapeArr.get(currentSize).height = 10;
        } else {
            shapeArr.get(currentSize).corners = 10;
            shapeArr.get(currentSize).radius = 10;
        }

        //Sending the new shape to the server to be added to the shared canvas
        game.getSocket().send("GameMessage/"+"UpdateCanvas/"+String.valueOf(lobbyID)+"/"+shapeArr.get(currentSize).type
                +  ":" + shapeArr.get(currentSize).colour + ":" +   shapeArr.get(currentSize).x + ":" +shapeArr.get(currentSize).y);

        sent++;
    }

    public void getCanvasUpdates(){
        //Gets the new shapes from the server that has been added to the shared canvas since the last update call from the client
        game.getSocket().send("GameMessage/"+"RequestCanvas/"+lobbyID+"/"+shapeArr.size());
    }

    public float[] getRGB(String colour){
        //Converts a string colour into RGB values

        float[] temp = {0.0f, 0.2f, 0.0f};

        if (colour.matches(("Red"))) {
            temp[0] = 1.0f;
            temp[1] = 0.0f;
            temp[2] = 0.0f;
        } else if (colour.matches(("Green"))) {
            temp[0] = 0.0f;
            temp[1] = 1.0f;
            temp[2] = 0.0f;
        } else if (colour.matches(("Blue"))) {
            temp[0] = 0.0f;
            temp[1] = 0.0f;
            temp[2] = 1.0f;
        } else if (colour.matches(("Yellow"))) {
            temp[0] = 1.0f;
            temp[1] = 1.0f;
            temp[2] = 0.0f;
        } else if (colour.matches(("Black"))) {
            temp[0] = 0.0f;
            temp[1] = 0.0f;
            temp[2] = 0.0f;
        } else if (colour.matches(("White"))) {
            temp[0] = 1.0f;
            temp[1] = 1.0f;
            temp[2] = 1.0f;
        }
        return temp;
    }

    @Override
    public void dispose(){
        game.dispose();
        stage.dispose();
        font.dispose();
        fontLarge.dispose();
    }
}