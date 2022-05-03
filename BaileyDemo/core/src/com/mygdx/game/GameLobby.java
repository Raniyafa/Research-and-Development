package com.mygdx.game;

import java.util.ArrayList;

public class GameLobby {

    private ArrayList<Shape> shapeList;
    private String lobbyCode;
    private int lobbyIndex;
    private String wordTopic;
    private String imageString;
    private String partnerName;

    public GameLobby(){
        lobbyCode = "code";
        lobbyIndex = -1;
        shapeList = new ArrayList<>();
        partnerName = "";
    }

    public GameLobby(String code, int index){
        lobbyCode = code;
        lobbyIndex = index;
        shapeList = new ArrayList<>();
        imageString = "";
        wordTopic = "";
        partnerName = "";
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

    public String getWordTopic() {
        return wordTopic;
    }

    public void setWordTopic(String wordTopic) {
        this.wordTopic = wordTopic;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }


    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }
}