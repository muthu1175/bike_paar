package com.example.bikepaar;

import com.google.gson.annotations.SerializedName;

public class AiBikeModel {

    // 🔹 Backend fields
    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private String price;

    @SerializedName("match_score")
    private int aiPercent;

    @SerializedName("image_url")
    private String imageUrl;   // backend image

    // 🔹 Frontend drawable fallback
    private int imageRes;

    // 🔹 OLD constructor (DO NOT REMOVE – adapter safe)
    public AiBikeModel(String name, String price, int aiPercent, int imageRes) {
        this.name = name;
        this.price = price;
        this.aiPercent = aiPercent;
        this.imageRes = imageRes;
    }

    // 🔹 EMPTY constructor (Retrofit needs this)
    public AiBikeModel() {}

    // ---------- GETTERS ----------
    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getAiPercent() {
        return aiPercent;
    }

    // 🔹 backend image (Glide / Picasso)
    public String getImageUrl() {
        return imageUrl;
    }

    // 🔹 local drawable (fallback)
    public int getImageRes() {
        return imageRes;
    }
}
