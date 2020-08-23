package com.clk.clkpieanddonut.models;

public class TextBoxPoints {
    float x;
    float y;
    int position;
    PieObject pieObject;

    public TextBoxPoints(){}

    public TextBoxPoints(float x, float y, int position, PieObject pieObject) {
        this.x = x;
        this.y = y;
        this.position = position;
        this.pieObject = pieObject;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public PieObject getPieObject() {
        return pieObject;
    }

    public void setPieObject(PieObject pieObject) {
        this.pieObject = pieObject;
    }
}
