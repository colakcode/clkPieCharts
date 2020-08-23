package com.clk.clkpieanddonut.models;

public class PieObject {
    String id;
    String name;
    double value;
    int color;

    public PieObject(){}

    public PieObject(String id, String name, double value, int color) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.color = color;
    }

    public PieObject(String id, String name, double value) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.color = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
