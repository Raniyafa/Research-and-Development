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

    //GameScreen class which is used when the player is inside the game/match

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

    private float partnerHeightRatio;
    private float partnerWidthRatio;


    private int selectedSize = 5;
    private int received = 0;
    private int sent = 0;
    private int lineNo;

    private float emoteY = Gdx.graphics.getHeight() / 6;

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

    private TextureRegionDrawable up;
    private TextureRegionDrawable down;
    private TextureRegion buttonUp;
    private TextureRegion buttonDown;
    private Texture tex2;
    private ImageButton button;

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

                    //Add new shape to shapeList when a NewShapeInfo is received from the server
                    if (clientMessage[0].matches("NewShapeInfo")) {
                        lineNo = Integer.valueOf(clientMessage[5]);
                        float[] colour = getRGB(clientMessage[4]);
                        shapeArr.add(new Shape(Integer.valueOf(clientMessage[1]), Integer.valueOf(clientMessage[2]), 10, clientMessage[3], colour, lineNo, myTurn));
                        received++;

                    }
                    //The server will tell the client when it is their turn, then appropriate values will be set
                    else if (clientMessage[0].matches("YourTurn")) {
                        turnTimer = 0.0f;
                        myTurn = true;
                        lineNo++;
                        game.getGameLobby().setPartnerName(clientMessage[1]);
                    }
                    //Statement to set the partner name when match is started
                    else if (clientMessage[0].matches("PartnerTurn")) {
                        game.getGameLobby().setPartnerName(clientMessage[1]);

                    }
                    //The server will tell the client when the match is over
                    else if (clientMessage[0].matches("GameFinished")) {
                        stage.clear();
                        gameFinished = true;
                    }
                    //Emote message from server, the corresponding emote will be drawn to screen
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
                    else if(clientMessage[0].matches("ResolutionRatio")){
                        partnerWidthRatio = Float.valueOf(clientMessage[1]);
                        partnerHeightRatio = Float.valueOf(clientMessage[2]);
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

        //Adding Background (new) - Add gameplay.png as the background
        tex = new Texture(Gdx.files.internal("image/gameplay.png"));
        batch = new SpriteBatch();

        gameMode = game.getGameLobby().getGameMode();

        game.setListener(getListener());

        float widthSlice = Gdx.graphics.getWidth() / 20;
        heightRatio = Gdx.graphics.getHeight() / 640;
        widthRatio = Gdx.graphics.getWidth() / 360;

        //Font creations
        font = new BitmapFont(Gdx.files.internal("font/dbSmallFont.fnt"), Gdx.files.internal("font/dbSmallFont.png"), false);
        font.setColor(Color.BLACK);
        fontLarge = new BitmapFont(Gdx.files.internal("font/dbfont.fnt"), Gdx.files.internal("font/dbfont.png"), false);
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
        colour.setBounds(35, 50, 90 * (Gdx.graphics.getWidth() / 360), 80 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(colour);
        colour.setVisible(false);

        //Add Color Button, click it the Color Select Box will show up
        tex2 = new Texture(Gdx.files.internal("button/ColorButton.png"));
        TextureRegion[][] temp_0 = TextureRegion.split(tex2,85,85);
        buttonUp = temp_0[0][0];
        buttonDown = temp_0[0][1];
        up = new TextureRegionDrawable(buttonUp);
        down = new TextureRegionDrawable(buttonDown);
        button = new ImageButton(up,down);
        button.setPosition(60 * (Gdx.graphics.getWidth() / 360), 45 * (Gdx.graphics.getHeight() / 640));
        button.setSize(30 * (Gdx.graphics.getWidth() / 360),30 * (Gdx.graphics.getHeight() / 640));
        stage.addActor(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!colour.isVisible()){
                    colour.setVisible(true);
                }
                else{
                    colour.setVisible(false);
                }
            }
        });

        Gdx.input.setInputProcessor(stage);

        game.getSocket().send("Ready/"+game.getGameLobby().getLobbyIndex()+"/"+String.valueOf(widthRatio)+"/"+String.valueOf(heightRatio));
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
        batch.draw(tex,0,0,360 * (Gdx.graphics.getWidth() / 360),750 * (Gdx.graphics.getHeight() / 640));

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
                    if (Gdx.input.getY() > Gdx.graphics.getHeight() / 3.6 && Gdx.input.getY() < Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 5) && drawTimer >= 0.05f) {
                        storeMouseLoc(delta);
                        drawTimer = 0.0f;
                    }
                } else if (isDrawing) {
                    isDrawing = false;
                    drawTimer = 0.0f;
                }
            } else if (gameMode.matches("One Line")) {
                oneLineModeTimer += delta;

                //If the maximum amount of time for the one line mode turn has been reached, then tell server to go to next turn
                if(oneLineModeTimer > Float.valueOf(game.getGameLobby().getTurnTimer())){
                    oneLineDrawing = false;
                    game.getSocket().send("TurnFinished/" + game.getGameLobby().getLobbyIndex());
                    myTurn = false;
                    isDrawing = false;
                    oneLineModeTimer = 0.0f;
                }

                //Check if it is my turn and if the player is touching the screen
                if (myTurn && Gdx.input.isTouched() && !colour.isTouchFocusTarget()) {
                    if (!isDrawing) {
                        isDrawing = true;
                        lineNo++;
                    }
                    //Check if the input is within the drawing area
                    if (Gdx.input.getY() > Gdx.graphics.getHeight() / 3.6 && Gdx.input.getY() < Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 5) && drawTimer >= 0.05f) {
                        oneLineDrawing = true;
                        storeMouseLoc(delta);
                        drawTimer = 0.0f;
                    }
                }
                //Check if current input is on the same line as previous input
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
                    //Get reference to shape object then draw the shape to screen
                    Shape drawShape = shapeArr.get(i);
                    shapeRenderer.setColor(drawShape.getRgb()[0], drawShape.getRgb()[1], drawShape.getRgb()[2], 1);
                    String temp = drawShape.getType();
                    float drawShapeX;
                    float drawShapeY;
                    float tempShapeX;
                    float tempShapeY = 0;


                    //incoming shapes from phone are being scaled again
                    //if its my turn then dont scale the stuff, if its not my turn then scale the stuff
                    if(drawShape.isMyTurn()) {
                         drawShapeX = drawShape.getX();
                         drawShapeY = drawShape.getY();
                    }
                    else{
                        if(widthRatio > partnerWidthRatio){
                            drawShapeX = drawShape.getX() / (partnerWidthRatio / widthRatio);
                        }
                        else if (widthRatio < partnerWidthRatio){
                            drawShapeX = drawShape.getX() * (widthRatio / partnerWidthRatio);
                        }
                        else{
                            drawShapeX = drawShape.getX();
                        }

                        if(heightRatio > partnerHeightRatio){
                            drawShapeY = drawShape.getY() / (partnerHeightRatio / heightRatio);
                        }
                        else if(heightRatio < partnerHeightRatio){
                            drawShapeY = drawShape.getY() * (heightRatio / partnerHeightRatio);
                        }
                        else{
                            drawShapeY = drawShape.getY();
                        }
                    }


                    if(tempShape.isMyTurn()) {
                        tempShapeX = tempShape.getX();
                        tempShapeY = tempShape.getY();
                    }
                    else{
                        if(widthRatio > partnerWidthRatio){
                            tempShapeX = tempShape.getX() / (partnerWidthRatio / widthRatio);
                        }
                        else if(widthRatio < partnerWidthRatio){
                            tempShapeX = tempShape.getX() * (widthRatio / partnerWidthRatio);
                        }
                        else{
                            tempShapeX = tempShape.getX();
                        }

                        if(heightRatio > partnerHeightRatio){
                            tempShapeY = tempShape.getY() / (partnerHeightRatio / heightRatio);
                        }
                        else if(heightRatio < partnerHeightRatio){
                            tempShapeY = tempShape.getY() * (heightRatio / partnerHeightRatio);
                        }
                        else{
                            tempShapeY = tempShape.getY();
                        }
                    }

                    //Check if current shape is on the same line as the previous shape, if so then connect them with a line
                    if (drawShape.getLineNo() == tempShape.getLineNo()) {
                        if ((!(drawShapeX >= tempShapeX - 5 && drawShapeX <= tempShapeX + 5)) || (!(drawShapeY >= tempShapeY - 5 && drawShapeY <= tempShapeY + 5))) {
                            //Here the 12 value is the line thickness, scaled by the screen res
                            shapeRenderer.rectLine(tempShapeX, tempShapeY, drawShapeX, drawShapeY, 12 * (widthRatio + heightRatio / 2));
                        }
                    }
                    //Draw the current shape as a circle

                    //here the 6 value is the radius size, scaled by resolution
                    shapeRenderer.circle(drawShapeX, drawShapeY, 6 * (widthRatio + heightRatio / 2));
                    tempShape = drawShape;
                } catch (Exception e) {
                    System.out.println("Null error drawing shapeArr[" + i + "]");
                }
            }

            shapeRenderer.end();

            game.getBatch().begin();

            //If emoteActive then draw the current Emote and keep track of the timer, and opacity
            if (emoteActive) {
                // emote.draw(game.getBatch(), );
                game.getBatch().draw(emote,Gdx.graphics.getWidth() / 2, emoteY,360 * (Gdx.graphics.getWidth() / 360) / 6,640 * (Gdx.graphics.getHeight() / 640) / 12);
                emoteY += 1.0f;

                if (emoteY >= (Gdx.graphics.getHeight() / 6) * 1.5f) {
                    emoteOpacity = -0.1f;
                    emote.setAlpha(emoteOpacity);
                    if (emoteY >= (Gdx.graphics.getHeight() / 6) * 2) {
                        emoteActive = false;
                        emoteY = Gdx.graphics.getHeight() / 6;
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
                    String temp_topic = "";
                    String temp_time = "";
                    if(gameMode.matches("Regular")) {
                        //Draw regular game mode UI for when it is my turn
                        temp = game.getPlayerName() + "'s turn";
                        temp_topic = game.getGameLobby().getWordTopic();
                        temp_time = ""+ (Math.round(turnLength - turnTimer));
                    }
                    else{
                        //Draw one-line mode game UI for when it is my turn
                        temp = game.getPlayerName() + "'s turn";
                        temp_topic = game.getGameLobby().getWordTopic();
                        temp_time = ""+ (Math.round(turnLength - turnTimer));
                    }

                    dbfont.draw(game.getBatch(), temp, (Gdx.graphics.getWidth() / 10) * 1, (Gdx.graphics.getHeight() / 20) * 18.4f);
                    dbfont.draw(game.getBatch(), temp_topic, (Gdx.graphics.getWidth() / 10) * 1, (Gdx.graphics.getHeight() / 20) * 16);
                    dbSmallFont.draw(game.getBatch(), temp_time, (Gdx.graphics.getWidth() / 10) * 8.5f, (Gdx.graphics.getHeight() / 20) * 16);
                    //Uncomment this line if you want to see packet debug info
                    //String debugInfo = "\nReceived: " + received + "\nSent: " + sent + "\nDrawn amount = :" + drawnAmount;
                    //   font.draw(game.getBatch(), debugInfo, 0, 200);
                    if(gameMode.matches("Regular")) {
                        if (turnTimer >= turnLength) {
                            myTurn = false;
                            game.getSocket().send("TurnFinished/" + game.getGameLobby().getLobbyIndex());
                            turnTimer = 0.0f;
                        }
                    }
                } else {
                    String temp = "";
                    String temp_topic = "";
                    String temp_time = "";
                    if(gameMode.matches("Regular")) {
                        //Draw regular game mode UI for when it is not my turn
                        temp = game.getGameLobby().getPartnerName() + "'s turn";
                        temp_topic = game.getGameLobby().getWordTopic();
                        temp_time = ""+ (Math.round(turnLength - turnTimer));
                    }
                    else{
                        //Draw one-line mode game UI for when it is not my turn
                        temp = game.getGameLobby().getPartnerName() + "'s turn";
                        temp_topic = game.getGameLobby().getWordTopic();
                        temp_time = ""+ (Math.round(turnLength - turnTimer));
                    }
                    dbfont.draw(game.getBatch(), temp, (Gdx.graphics.getWidth() / 10) * 1, (Gdx.graphics.getHeight() / 20) * 18.4f);
                    dbfont.draw(game.getBatch(), temp_topic, (Gdx.graphics.getWidth() / 10) * 1, (Gdx.graphics.getHeight() / 20) * 16);
                    dbSmallFont.draw(game.getBatch(), temp_time, (Gdx.graphics.getWidth() / 10) * 8.5f, (Gdx.graphics.getHeight() / 20) * 16);
                    //Uncomment this line if you want to see packet debug info
                    //String debugInfo = "\nReceived: " + received + "\nSent: " + sent + "\nDrawn amount = :" + drawnAmount;
                    //   font.draw(game.getBatch(), debugInfo, 0, 200);
                }
            }
        }

        //If game socket is closed and not connecting, attempt to connect
        //If game socket is closed then display that information to the user and attempt to re-connect, also keep track of the time spent disconnecting
        else if(game.getSocket().isClosed()){
            if(game.getSocket().isConnecting()){
                game.getSocket().connect();
            }
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
            //If the client is using Android or Desktop then take a screenshot of the drawing and convert to Base64 string then send to server
            //Server could interact with image hosting API to host drawing which can be used for social media sharing
            //to be able to directly share the screenshot, interfaces must be added for each platform with sharing functions created for each and depending on which
            //platform is being used the user, a different interface may be used to facilitate the sharing of a image
            if(Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.Desktop || Gdx.app.getType() == Application.ApplicationType.iOS) {

            final Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            //Screenshot can be found in the games directory on the phone storage, havent found a way to move it to the gallery for sharing use
            PixmapIO.writePNG(Gdx.files.external("drawbuddytestscreenshot.png"), pixmap, Deflater.DEFAULT_COMPRESSION, true);

            FileHandle file =  Gdx.files.external("drawbuddytestscreenshot.png");

            String encodedfile = null;

            byte[] bytes = file.readBytes();
            encodedfile = Base64Coder.encodeLines(bytes);
            game.getGameLobby().setImageString(encodedfile);
            pixmap.dispose();

            game.getSocket().send(encodedfile);
            }
            float[] scaleInfo = {partnerWidthRatio, partnerHeightRatio};
            game.setScreen(new PostGame(game, shapeArr, scaleInfo));
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public void storeMouseLoc(float delta) {
        int currentSize = shapeArr.size();
        Shape temp;

        temp = (new Shape(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()));

        //Gets the selected colour
        temp.setColour(colour.getSelected());
        //Gets the selected colour RGB value
        temp.setRgb(getRGB(colour.getSelected()));
        //Gets the selected shape (circle, square, triangle)
        temp.setType(selectedType);

        temp.setLineNo(lineNo);
        temp.setRadius(10);

        //Sending the new shape to the server to be added to the shared canvas
        game.getSocket().send("GameMessage/"+"UpdateCanvas/"+String.valueOf(lobbyID)+"/"+game.getAuthCode()+"/"+temp.getType()
                +  ":" + temp.getColour() + ":" +   temp.getX() + ":" +temp.getY() + ":" + lineNo);
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
        stage.dispose();
        font.dispose();
        fontLarge.dispose();
        tex.dispose();
        font.dispose();
        fontLarge.dispose();
        dbfont.dispose();
        dbSmallFont.dispose();
        annoyed.dispose();
        hearteye.dispose();
        sad.dispose();
        laugh.dispose();
        activeEmote.dispose();
        shapeRenderer.dispose();
        batch.dispose();
    }
}