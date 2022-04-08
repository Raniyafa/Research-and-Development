package com.mygdx.game;

import java.util.ArrayList;

public class GameLobby {

    private ArrayList<Shape> shapeList;
    private String lobbyCode;
    private int lobbyIndex;

    public GameLobby(){
        lobbyCode = "code";
        lobbyIndex = -1;
        shapeList = new ArrayList<>();

    }

    public GameLobby(String code, int index){
        lobbyCode = code;
        lobbyIndex = index;
        shapeList = new ArrayList<>();
    }

    public String lobbyToString(){
        return String.valueOf(lobbyIndex)+"/"+lobbyCode;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public int getLobbyIndex() {
        return lobbyIndex;
    }

    public void setLobbyIndex(int lobbyIndex) {
        this.lobbyIndex = lobbyIndex;
    }
}