package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;

import com.badlogic.gdx.utils.Base64Coder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.zip.Deflater;

public class GameScreen extends ScreenAdapter {

    private BitmapFont font;
    private BitmapFont fontLarge;

    private BitmapFont dbfont;
    private BitmapFont dbSmallFont;

    private Texture annoyed;
    private Texture hearteye;
    private Texture sad;
    private Texture laugh;

    private Texture activeEmote;

    private int lobbyID = 0;
    private MultipleScenes game;
    private ArrayList<Shape> shapeArr;

    private ShapeRenderer shapeRenderer;

    private Sprite emote;

    final private float UPDATE_TIME = 1 / 30.0f;

    private SelectBox<String> drawSize;
    private SelectBox<String> colour;
    private TextButton triangle;
    private TextButton circle;
    private TextButton square;
    private Stage stage;

    private ImageButton emojiButtons[];

    private String selectedColour = "Red";
    private String selectedType = "circle";
    private String gameMode = "";

    private float renderTimer;
    private float turnTimer = 0.0f;

    private float turnLength = 0.0f;

    private float waitTime;
    private float drawTimer = 0.0f;
    private float menuTimer = 0.0f;

    private float oneLineModeTimer = 0.0f;

    private float disconnectedTimer = 0.0f;
    private float emoteOpacity = 1.0f;
    private float heightRatio;
    private float widthRatio;


    private int selectedSize = 5;
    private int received = 0;
    private int sent = 0;
    private int lineNo;

    private float emoteY = 75;

    private boolean myTurn = false;
    private boolean gameFinished;
    private boolean isDrawing = false;
    private boolean isUpdating;
    private boolean menuOpen = false;
    private boolean emoteActive = false;
    private boolean readyToDraw = false;
    private boolean oneLineDrawing = false;

    private Texture tex;
    private Image image;
    private TextureRegion region;
    private SpriteBatch batch;

    public GameScreen(MultipleScenes game) {
        this.game = game;
    }

    private WebSocketAdapter getListener() {

        return new WebSocketAdapter() {

            @Override
            public boolean onMessage(final WebSocket webSocket, final String packet) {
                //Network handler for this class, takes messages from the server and uses the information for the game
                if(!packet.contains("CanvasInfo")) {
                    Gdx.app.log("WS GameScreen", "Got message: " + packet);
                }

                try {
                    String[] clientMessage = packet.split("/");

                    if (clientMessage[0].matches("GameMessage2")) {
                        lineNo = Integer.valueOf(clientMessage[5]);
                        float[] colour = getRGB(clientMessage[4]);
                        shapeArr.add(new Shape(Integer.valueOf(clientMessage[1]), Integer.valueOf(clientMessage[2]), 10, clientMessage[3], colour, lineNo));
                        received++;

                    }
                    else if (clientMessage[0].matches("CanvasInfo") && Integer.valueOf(clientMessage[1]) > shapeArr.size() && !isUpdating) {
                        isUpdating = true;
                        int shapeArrSize = shapeArr.size();
                        int index = 2;
                        int size = shapeArrSize + ((clientMessage.length - 2) / 4);

                        for (int i = shapeArrSize; i <= size - 1; i++) {
                            float[] colour = getRGB(clientMessage[index + 1]);
                            shapeArr.add(new Shape(Integer.valueOf(clientMessage[index + 2]), Integer.valueOf(clientMessage[index + 3]), 10, clientMessage[index], colour, Integer.valueOf(clientMessage[5])));
                            index += 4;
                            received++;
                        }
                    } else if (clientMessage[0].matches("YourTurn")) {
                        turnTimer = 0.0f;
                        myTurn = true;
                        lineNo++;
                        game.getGameLobby().setPartnerName(clientMessage[1]);
                    }
                    else if (clientMessage[0].matches("PartnerTurn")) {
                        game.getGameLobby().setPartnerName(clientMessage[1]);

                    } else if (clientMessage[0].matches("GameFinished")) {
                        stage.clear();
                        gameFinished = true;
                    }
                    else if(clientMessage[0].matches("Emote")) {
                        emoteActive = true;

                        if(clientMessage[1].matches("Annoyed")){
                            emote = new Sprite(annoyed);
                        }
                        else if(clientMessage[1].matches("Laugh")){
                            emote = new Sprite(laugh);
                        }
                        else if(clientMessage[1].matches("Hearteye")){
                            emote = new Sprite(hearteye);
                        }
                        else if(clientMessage[1].matches("Sad")){
                            emote = new Sprite(sad);
                        }

                        emote.setPosition(195, emoteY);
                        emote.setScale(0.75f);
                    }
                }catch(Exception e){
                    System.out.println("Exception in update from server " +e);
                }
                isUpdating = false;
                return FULLY_HANDLED;
            }
        };
    }

    @Override
    public void show() {

        turnLength = Float.valueOf(game.getGameLobby().getTurnTimer());

        stage = new Stage(new ScreenViewport());

        //Adding Background IMG
//        tex = new Texture(Gdx.files.internal("image/gameplaytest.png"));
//        region = new TextureRegion(tex,0,0,750,1334);
//        image = new Image(region);
//        image.setPosition(0,0);
//        image.setSize(360 * (Gdx.graphics.getWidth() / 360),750 * (Gdx.graphics.getHeight() / 640));
//        stage.addActor(image);

        //Adding Background (new)
        tex = new Texture(Gdx.files.internal("image/gameplay.png"));
        batch = new SpriteBatch();

        gameMode = game.getGameLobby().getGameMode();
        System.out.println("gamemode = "+gameMode);
        game.setListener(getListener());

        float widthSlice = Gdx.graphics.getWidth() / 20;
        heightRatio = Gdx.graphics.getHeight() / 640;
        widthRatio = Gdx.graphics.getHeight() / 360;

        //Font creations
        font = new BitmapFont(Gdx.files.internal("smallfont/smallfont.fnt"),
                Gdx.files.internal("smallfont/smallfont.png"), false);
        font.setColor(Color.BLACK);
        fontLarge = new BitmapFont(Gdx.files.internal("font/font.fnt"), Gdx.files.internal("font/font.png"), false);
        fontLarge.setColor(Color.BLACK);

        dbfont = new BitmapFont(Gdx.files.internal("font/dbfont.fnt"),
                Gdx.files.internal("font/dbfont.png"), false);
        dbfont.setColor(Color.BLACK);
        dbSmallFont = new BitmapFont(Gdx.files.internal("font/dbSmallFont.fnt"),
                Gdx.files.internal("font/dbSmallFont.png"), false);
        dbSmallFont.setColor(Color.BLACK);


        shapeRenderer = new ShapeRenderer();


        emojiButtons = new ImageButton[4];

        annoyed = new Texture(Gdx.files.internal("button/emoji/annoyed.png"));
        Texture annoyedButton = new Texture(Gdx.files.internal("button/emoji/annoyed.png"));
        TextureRegion myTextureRegion = new TextureRegion(annoyedButton);
        TextureRegionDrawable myTexRegionAnnoyed = new TextureRegionDrawable(myTextureRegion);
        emojiButtons[0] = new ImageButton(myTexRegionAnnoyed); //Set the button up
        emojiButtons[0].setBounds(widthSlice * 10, 50, 35 * (Gdx.graphics.getWidth() / 360), 35 * (Gdx.graphics.getHeight() / 640));
        emojiButtons[0].addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!emoteActive)
                    game.getSocket().send("GameMessage/Emote/"+game.getGameLobby().getLobbyIndex()+"/Annoyed");
            }
        });

        stage.addActor(emojiButtons[0]);

        hearteye = new Texture(Gdx.files.internal("button/emoji/hearteye.png"));
        Texture hearteyeButton = new Texture(Gdx.files.internal("button/emoji/hearteye.png"));
        TextureRegion myTextureRegionHeart = new TextureRegion(hearteyeButton);
        TextureRegionDrawable myTexRegionHeart = new TextureRegionDrawable(myTextureRegionHeart);
        emojiButtons[1] = new ImageButton(myTexRegionHeart); //Set the button up
        emojiButtons[1].setBounds(widthSlice * 12 , 50, 35 * (Gdx.graphics.getWidth() / 360), 35 * (Gdx.graphics.getHeight() / 640));

        emojiButtons[1].addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!emoteActive)
                    game.getSocket().send("GameMessage/Emote/"+game.getGameLobby().getLobbyIndex()+"/Hearteye");
            }
        });
        stage.addActor(emojiButtons[1]);

        laugh = new Texture(Gdx.files.internal("button/emoji/laugh.png"));
        Texture laughButton = new Texture(Gdx.files.internal("button/emoji/laugh.png"));
        TextureRegion myTextureRegionLaugh = new TextureRegion(laughButton);
        TextureRegionDrawable myTexRegionLaugh = new TextureRegionDrawable(myTextureRegionLaugh);
        emojiButtons[2] = new ImageButton(myTexRegionLaugh); //Set the button up
        emojiButtons[2].setBounds(widthSlice * 14, 50, 35 * (Gdx.graphics.getWidth() / 360), 35 * (Gdx.graphics.getHeight() / 640));

        emojiButtons[2].addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!emoteActive)
                    game.getSocket().send("GameMessage/Emote/"+game.getGameLobby().getLobbyIndex()+"/Laugh");
            }
        });

        stage.addActor(emojiButtons[2]);

        sad = new Texture(Gdx.files.internal("button/emoji/sad.png"));
        Texture sadButton = new Texture(Gdx.files.internal("button/emoji/sad.png"));
        TextureRegion myTextureRegionSad = new TextureRegion(sad);
        TextureRegionDrawable myTexRegionSad = new TextureRegionDrawable(myTextureRegionSad);
        emojiButtons[3] = new ImageButton(myTexRegionSad); //Set the button up
        emojiButtons[3].setBounds(widthSlice * 16, 50, 35 * (Gdx.graphics.getWidth() / 360), 35 * (Gdx.graphics.getHeight() / 640));

        emojiButtons[3].addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!emoteActive)
                    game.getSocket().send("GameMessage/Emote/"+game.getGameLobby().getLobbyIndex()+"/Sad");
            }
        });

        stage.addActor(emojiButtons[3]);

        //Attach the network listener for this class to the WebSocket

        game.getSocket().send("Ready/"+game.getGameLobby().getLobbyIndex());
        shapeArr = new ArrayList<>(5000);

        //Skin created for buttons and UI elements, stage set for this class
        Skin mySkin = new Skin(Gdx.files.internal("plain-james/skin/plain-james-ui.json"));

        //All the buttons and listeners for the buttons are added below
        drawSize = new SelectBox<String>(mySkin);
        drawSize.setItems("5", "10", "15", "20", "25", "30");
        drawSize.setName("Pencil Size");
        drawSize.setSize(1.0f * (Gdx.graphics.getWidth() / 360), 1.0f * (Gdx.graphics.getHeight() / 640));
        drawSize.setBounds(widthSlice * 5, 35, 75, 33);
        drawSize.setSelected("5");
        // stage.addActor(drawSize);

        colour = new SelectBox<String>(mySkin);
        colour.setItems("Black", "Green", "Blue", "Yellow", "Red");
        colour.setName("Pencil Colour");
        colour.setBounds(35, 50, 70 * (Gdx.graphics.getWidth() / 360), 33 * (Gdx.graphics.getHeight() / 640));
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
        //  stage.addActor(triangle);

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

        // stage.addActor(circle);

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

        // stage.addActor(square);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        drawTimer += delta;
        waitTime += delta;
        menuTimer -= delta;

        if (waitTime >= 2.0f) {
            readyToDraw = true;
        }

        renderTimer += delta;

        turnTimer += delta;

        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(tex,0,0,360,750);
        batch.end();

        //If the WebSocket is open (connected) then process the game controller logic
        if (game.getSocket().isOpen()) {



            //If the requirements are met then the current mouse location is captured and a shape is created corresponding to the selected
            //shape, colour and size
            if (gameMode.matches("Regular")) {
                if (myTurn && Gdx.input.isTouched() && !colour.isTouchFocusTarget()) {
                    if (!isDrawing) {
                        isDrawing = true;
                        lineNo++;
                    }
                    if (Gdx.input.getY() > 100.0f && Gdx.input.getY() < Gdx.graphics.getHeight() - 100.0f && drawTimer >= 0.05f) {
                        storeMouseLoc(delta);
                        drawTimer = 0.0f;
                    }
                } else if (isDrawing) {
                    isDrawing = false;
                    drawTimer = 0.0f;
                }
            } else if (gameMode.matches("One Line")) {
                oneLineModeTimer += delta;

                if(oneLineModeTimer > Float.valueOf(game.getGameLobby().getTurnTimer())){
                    oneLineDrawing = false;
                    game.getSocket().send("TurnFinished/" + game.getGameLobby().getLobbyIndex());
                    myTurn = false;
                    isDrawing = false;
                    oneLineModeTimer = 0.0f;
                }

                if (myTurn && Gdx.input.isTouched() && !colour.isTouchFocusTarget()) {
                    if (!isDrawing) {
                        isDrawing = true;
                        lineNo++;
                    }
                    if (Gdx.input.getY() > 100.0f && Gdx.input.getY() < Gdx.graphics.getHeight() - 100.0f && drawTimer >= 0.05f) {
                        oneLineDrawing = true;
                        storeMouseLoc(delta);
                        drawTimer = 0.0f;
                    }
                }
                else if(oneLineDrawing && !Gdx.input.isTouched()){
                    oneLineDrawing = false;
                    game.getSocket().send("TurnFinished/" + game.getGameLobby().getLobbyIndex());
                    myTurn = false;
                    isDrawing = false;
                }
            }

            int drawnAmount = 0;

            //Start the shape renderer with shapes being colour filled
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            //Draw a cursor to show the current position of the mouse/finger
            if(!gameFinished) {
                shapeRenderer.circle(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 10.0f);
            }
            Shape tempShape = new Shape();
            //For loop which iterates through the shape array and draws each shape individually
            for (int i = 0; i <= shapeArr.size() - 1; i++) {
                drawnAmount++;
                try {
                    Shape drawShape = shapeArr.get(i);
                    shapeRenderer.setColor(drawShape.rgb[0], drawShape.rgb[1], drawShape.rgb[2], 1);
                    String temp = drawShape.type;
                    float drawShapeX = drawShape.x * widthRatio;
                    float drawShapeY = drawShape.y * heightRatio;

                    float tempShapeX = tempShape.x * widthRatio;
                    float tempShapeY = tempShape.y * heightRatio;

                    if (drawShape.lineNo == tempShape.lineNo) {
                        if ((!(drawShapeX >= tempShapeX - 5 && drawShapeX <= tempShapeX + 5)) || (!(drawShapeY >= tempShapeY - 5 && drawShapeY <= tempShapeY + 5))) {

                            shapeRenderer.rectLine(tempShapeX, tempShapeY, drawShapeX, drawShapeY, 20);
                        }
                    }
                    //   if (temp.matches("circle")) {
                    shapeRenderer.circle(drawShapeX, drawShapeY, 10);
                    /*
                    } else if (temp.matches("square")) {
                        shapeRenderer.rect(drawShape.x, drawShape.y, 10, 10);

                    } else {
                        shapeRenderer.triangle(drawShape.x - 30.0f, drawShape.y, drawShape.x + 30.0f, drawShape.y, drawShape.x, drawShape.y + 45.0f);
                    }
                     */
                    tempShape = drawShape;
                } catch (Exception e) {
                    System.out.println("Null error drawing shapeArr[" + i + "]");
                }
            }

            shapeRenderer.end();

            game.getBatch().begin();

            if (emoteActive) {
                emote.draw(game.getBatch());
                emoteY += 1.0f;
                emote.setPosition(195, emoteY);
                if (emoteY >= 150) {
                    emoteOpacity = -0.1f;
                    emote.setAlpha(emoteOpacity);
                    if (emoteY >= 200) {
                        emoteActive = false;
                        emoteY = 95.0f;
                        emoteOpacity = 1.0f;
                        activeEmote = null;
                    }
                }
            }


            //In game information displayed here, such as whos turn it is and how many shapes have been sent/received by the client, and how many
            //shapes are being drawn to the screen.
            if (!gameFinished) {
                if (myTurn) {
                    String temp = "";
                    String temp_time = "";
                    if(gameMode.matches("Regular")) {
                        temp = game.getGameLobby().getWordTopic() + "\n" + game.getPlayerName()+ "'s turn to draw! ";
                        temp_time = ""+ (Math.round(turnLength - turnTimer));
                    }
                    else{
                        temp = game.getGameLobby().getWordTopic() + "\n" + game.getPlayerName() + "'s turn to draw!\nOne line mode..";
                        temp_time = ""+ (Math.round(turnLength - turnTimer));
                    }
                    String temp2 = "\nReceived: " + received + "\nSent: " + sent + "\nDrawn amount = :" + drawnAmount;
                    dbfont.draw(game.getBatch(), temp, Gdx.graphics.getWidth() / 2 - 165, Gdx.graphics.getHeight() - 10);
                    dbSmallFont.draw(game.getBatch(), temp_time, Gdx.graphics.getWidth() / 2 + 130, Gdx.graphics.getHeight() - 125);
                 //   font.draw(game.getBatch(), temp2, 0, 200);
                    if(gameMode.matches("Regular")) {
                        if (turnTimer >= turnLength) {
                            myTurn = false;
                            game.getSocket().send("TurnFinished/" + game.getGameLobby().getLobbyIndex());
                            turnTimer = 0.0f;
                        }
                    }
                } else {
                    String temp = "";
                    String temp_time = "";
                    if(gameMode.matches("Regular")) {
                        temp = game.getGameLobby().getWordTopic() + "\nYour partner is drawing!";
                        temp_time = ""+ (Math.round(turnLength - turnTimer));
                    }
                    else{
                        temp = game.getGameLobby().getWordTopic() + "\nYour partner is drawing!\nOne line mode..";
                        temp_time = ""+ (Math.round(turnLength - turnTimer));
                    }
                    String temp2 = "\nReceived: " + received + "\nSent: " + sent + "\nDrawn amount = :" + drawnAmount;
                    dbfont.draw(game.getBatch(), temp, Gdx.graphics.getWidth() / 2 - 165, Gdx.graphics.getHeight() - 10);
                    dbSmallFont.draw(game.getBatch(), temp_time, Gdx.graphics.getWidth() / 2 + 130, Gdx.graphics.getHeight() - 125);
                    // font.draw(game.getBatch(), temp2, 0, 200);
                }
            }
        }

        //If game socket is closed and not connecting, attempt to connect


        //If game socket is closed then display that information to the user and attempt to re-connect, also keep track of the time spent disconnecting
        else if(game.getSocket().isClosed()){
            disconnectedTimer += delta;

            if(disconnectedTimer >= 5.0f) {
                game.setScreen(new HomeScreen(game));
            }
        }
        else{
            if(disconnectedTimer >= 0.0f){
                getCanvasUpdates();
            }
        }

        game.getBatch().end();

        stage.act();
        stage.draw();



        //When the game is finished (The server sends a message to the clients to say so) then the client will exit to the main menu
        if (gameFinished) {
            if(Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.Desktop) {


            final Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            PixmapIO.writePNG(Gdx.files.external("drawbuddytestscreenshot.png"), pixmap, Deflater.DEFAULT_COMPRESSION, true);

            FileHandle file =  Gdx.files.external("drawbuddytestscreenshot.png");

            String encodedfile = null;

            byte[] bytes = file.readBytes();
            encodedfile = Base64Coder.encodeLines(bytes);
            game.getGameLobby().setImageString(encodedfile);
            pixmap.dispose();

            game.getSocket().send(encodedfile);
            game.setScreen(new PostGame(game, shapeArr));
            }
        }

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public void storeMouseLoc(float delta) {

        int currentSize = shapeArr.size();

        Shape temp =  (new Shape(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()));

        //Gets the selected colour
        temp.colour = colour.getSelected();
        //Gets the selected colour RGB value
        temp.rgb = getRGB(colour.getSelected());
        //Gets the selected shape (circle, square, triangle)
        temp.type = selectedType;

        temp.lineNo = lineNo;
        temp.radius = 10;

        /*
        //Sets the required values for the selected shape
        if (selectedType == "circle") {
            temp.radius = 10;
        } else if (selectedType == "square") {
            shapeArr.get(currentSize).width = 10;
            shapeArr.get(currentSize).height = 10;
        } else {
            shapeArr.get(currentSize).corners = 10;
            shapeArr.get(currentSize).radius = 10;
        }

        */

        //Sending the new shape to the server to be added to the shared canvas
        game.getSocket().send("GameMessage/"+"UpdateCanvas/"+String.valueOf(lobbyID)+"/"+game.getAuthCode()+"/"+temp.type
                +  ":" + temp.colour + ":" +   temp.x + ":" +temp.y + ":" + lineNo);
        sent++;
    }

    public void getCanvasUpdates(){
        //Gets the new shapes from the server that has been added to the shared canvas since the last update call from the client
         game.getSocket().send("GameMessage/"+"RequestCanvas/"+lobbyID+"/"+game.getAuthCode()+"/"+shapeArr.size());
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