package com.example.bikepaar;

import com.google.gson.annotations.SerializedName;

public class AiBikeModel {

    // 🔹 Backend fields
    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private String price;

    @SerializedName("matchPercent")
    private int aiPercent;

    @SerializedName("imageUrl")
    private String imageUrl;   // backend image

    @SerializedName("engine")
    private String engine;

    @SerializedName("mileage")
    private String mileage;

    @SerializedName("description")
    private String description;

    @SerializedName("max_power")
    private String maxPower;

    @SerializedName("max_torque")
    private String maxTorque;

    @SerializedName("kerb_weight")
    private String kerbWeight;

    @SerializedName("fuel_tank_capacity")
    private String fuelTankCapacity;

    @SerializedName("transmission")
    private String transmission;

    @SerializedName("braking_system")
    private String brakingSystem;

    @SerializedName("top_speed")
    private String topSpeed;

    // Full Specs
    @SerializedName("front_brake_type")
    private String frontBrakeType;
    @SerializedName("rear_brake_type")
    private String rearBrakeType;
    @SerializedName("front_suspension")
    private String frontSuspension;
    @SerializedName("rear_suspension")
    private String rearSuspension;
    @SerializedName("tyre_type")
    private String tyreType;
    @SerializedName("headlight")
    private String headlight;
    @SerializedName("tail_light")
    private String tailLight;
    @SerializedName("battery_capacity")
    private String batteryCapacity;


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
    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getAiPercent() { return aiPercent; }
    public String getImageUrl() { return imageUrl; }
    public int getImageRes() { return imageRes; }
    
    public String getEngine() { return engine; }
    public String getMileage() { return mileage; }
    public String getDescription() { return description; }
    public String getMaxPower() { return maxPower; }
    public String getMaxTorque() { return maxTorque; }
    public String getKerbWeight() { return kerbWeight; }
    public String getFuelTankCapacity() { return fuelTankCapacity; }
    public String getTransmission() { return transmission; }
    public String getBrakingSystem() { return brakingSystem; }
    public String getTopSpeed() { return topSpeed; }
    public String getFrontBrakeType() { return frontBrakeType; }
    public String getRearBrakeType() { return rearBrakeType; }
    public String getFrontSuspension() { return frontSuspension; }
    public String getRearSuspension() { return rearSuspension; }
    public String getTyreType() { return tyreType; }
    public String getHeadlight() { return headlight; }
    public String getTailLight() { return tailLight; }
    public String getBatteryCapacity() { return batteryCapacity; }
}
