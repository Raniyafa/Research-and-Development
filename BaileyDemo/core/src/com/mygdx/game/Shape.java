package com.mygdx.game;

public class Shape {

    //Shape class to represent a shape in the drawing canvas
    private int x, y, radius, width, height, corners;
    private float[] rgb;
    private String type;
    private String colour;
    private int lineNo;

    public Shape(int x, int y, int width, int height, String type, float[] color, int lineNo){
        rgb = new float[3];
        rgb[0] = color[0];
        rgb[1] = color[1];
        rgb[2] = color[2];
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.lineNo = lineNo;
    }

    public Shape(int x, int y, int radius, String type, float[] color, int lineNo){
        rgb = new float[3];
        rgb[0] = color[0];
        rgb[1] = color[1];
        rgb[2] = color[2];
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.type = type;
        this.lineNo = lineNo;
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
        type = "circl";
        x = -1000;
        y = -1000;
        radius = 1;
        rgb = new float[3];
        lineNo = -1;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCorners() {
        return corners;
    }

    public void setCorners(int corners) {
        this.corners = corners;
    }

    public float[] getRgb() {
        return rgb;
    }

    public void setRgb(float[] rgb) {
        this.rgb = rgb;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }
}
