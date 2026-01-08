package com.example.bikepaar;

public class BrandItem {
    private String name;
    private int imageResource;

    public BrandItem(String name, int imageResource) {
        this.name = name;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }
}
