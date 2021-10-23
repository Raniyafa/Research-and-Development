package com.mygdx.game;

public class Shape {

    int x, y, radius, width, height, corners;
    float[] rgb;
    String type;
    String colour;

    public Shape(int x, int y, int width, int height, String type, float[] color){
        rgb = new float[3];
        rgb[0] = color[0];
        rgb[1] = color[1];
        rgb[2] = color[2];
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    public Shape(int x, int y, int radius, String type, float[] color){
        rgb = new float[3];
        rgb[0] = color[0];
        rgb[1] = color[1];
        rgb[2] = color[2];
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.type = type;
    }

    public Shape(int x, int y){
        this.x = x;
        this.y = y;
        rgb = new float[3];
        type = "";
        radius = 0;
        width = 0;
        height = 0;
        corners = 0;
    }

    public Shape(){
        type = "circle";
        x = -1000;
        y = -1000;
        radius = 1;
        rgb = new float[3];
    }

}
