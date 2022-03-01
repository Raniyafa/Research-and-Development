package com.mygdx.game;

import java.util.ArrayList;

public class GameLobby {

    public ArrayList<Shape> shapeList;
    public String lobbyCode;
    public int lobbyIndex;

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

}