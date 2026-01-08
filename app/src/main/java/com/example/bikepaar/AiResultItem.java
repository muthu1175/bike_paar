package com.example.bikepaar;

public class AiResultItem {
    public final String title;
    public final String subtitle;
    public final String price;
    public final int matchPercent;
    public final int imageRes;

    public AiResultItem(String title, String subtitle, String price, int matchPercent, int imageRes) {
        this.title = title;
        this.subtitle = subtitle;
        this.price = price;
        this.matchPercent = matchPercent;
        this.imageRes = imageRes;
    }
}

