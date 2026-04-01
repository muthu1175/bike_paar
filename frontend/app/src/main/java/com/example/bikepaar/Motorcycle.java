package com.example.bikepaar;

public class Motorcycle {
    private String name;
    private String brand;
    private String cc;
    private String type;
    private String price;
    private String imageUrl;
    private boolean isFavorite;
    private boolean isBestseller;

    public Motorcycle(String name, String brand, String cc, String type, String price, String imageUrl, boolean isFavorite) {
        this.name = name;
        this.brand = brand;
        this.cc = cc;
        this.type = type;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isFavorite = isFavorite;
        this.isBestseller = false;
    }

    public Motorcycle(String name, String brand, String cc, String type, String price, String imageUrl, boolean isFavorite, boolean isBestseller) {
        this.name = name;
        this.brand = brand;
        this.cc = cc;
        this.type = type;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isFavorite = isFavorite;
        this.isBestseller = isBestseller;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCc() { return cc; }
    public void setCc(String cc) { this.cc = cc; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public boolean isBestseller() { return isBestseller; }
    public void setBestseller(boolean bestseller) { isBestseller = bestseller; }
}