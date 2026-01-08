package com.example.bikepaar; // Replace with your package name

public class RideStyleItem {
    private String name;
    private int iconResId;
    private String color;

    public RideStyleItem(String name, int iconResId, String color) {
        this.name = name;
        this.iconResId = iconResId;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getColor() {
        return color;
    }
}