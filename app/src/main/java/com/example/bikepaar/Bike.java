package com.example.bikepaar;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class Bike implements Serializable {
    public String id;
    public String name;
    public int price; // ex-showroom approx (INR)
    public int imageRes; // R.drawable.xxx
    public String vehicleType; // "Motorcycle" / "Scooter"
    public String usage; // "Daily use" / "Adventure" / "Sports"
    public int minRangeKm; // approximate comfortable daily distance (for matching)
    public String fuelCategory; // "high" / "medium" / "low" (efficiency)
    public String experience; // "Beginner","Intermediate","Expert"

    // New fields for HTML/budget design
    public String engine; // Engine capacity like "97.2 cc"
    public String mileage; // Mileage like "60 kmpl"
    public String imageUrl; // URL for online images
    public String badge; // Badge text like "Top Seller", "High Mileage"
    public boolean isFavorite; // Favorite status
    public boolean isPopular; // Popular flag for sorting

    // Dynamic Specifications
    @SerializedName("max_power") public String maxPower;
    @SerializedName("max_torque") public String maxTorque;
    @SerializedName("kerb_weight") public String kerbWeight;
    @SerializedName("transmission") public String transmission;
    @SerializedName("fuel_tank_capacity") public String fuelTankCapacity;
    @SerializedName("braking_system") public String brakingSystem;
    @SerializedName("top_speed") public String topSpeed;

    // Full Specifications
    @SerializedName("front_brake_type") public String frontBrakeType;
    @SerializedName("rear_brake_type") public String rearBrakeType;
    @SerializedName("front_suspension") public String frontSuspension;
    @SerializedName("rear_suspension") public String rearSuspension;
    @SerializedName("tyre_type") public String tyreType;
    @SerializedName("headlight") public String headlight;
    @SerializedName("tail_light") public String tailLight;
    @SerializedName("battery_capacity") public String batteryCapacity;

    // computed
    public int matchPercent;

    // Existing constructor for backward compatibility
    public Bike(String id, String name, int price, int imageRes,
                String vehicleType, String usage, int minRangeKm,
                String fuelCategory, String experience) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.vehicleType = vehicleType;
        this.usage = usage;
        this.minRangeKm = minRangeKm;
        this.fuelCategory = fuelCategory;
        this.experience = experience;
        this.matchPercent = 0;
        this.isFavorite = false;
        this.isPopular = false;
        this.engine = "";
        this.mileage = "";
        this.imageUrl = "";
        this.badge = "";
    }

    // New constructor for budget bikes with HTML design
    public Bike(String id, String name, String engine, String mileage, int price,
                String imageUrl, String badge, String vehicleType, String usage) {
        this.id = id;
        this.name = name;
        this.engine = engine;
        this.mileage = mileage;
        this.price = price;
        this.imageUrl = imageUrl;
        this.badge = badge;
        this.vehicleType = vehicleType != null ? vehicleType : "Motorcycle";
        this.usage = usage != null ? usage : "Daily use";
        this.minRangeKm = 0;
        this.fuelCategory = "medium";
        this.experience = "Beginner";
        this.matchPercent = 0;
        this.isFavorite = false;
        this.isPopular = (badge != null && badge.equals("Top Seller"));
        this.imageRes = 0; // No local image resource
    }

    // Helper methods for compatibility
    public String getFormattedPrice() {
        return "₹" + String.format("%,d", this.price);
    }

    public String getPriceWithLabel() {
        return "₹" + String.format("%,d", this.price) + " Onwards";
    }

    // Getters for Specs (Null Safe)
    public String getMaxPower() { return maxPower != null ? maxPower : "N/A"; }
    public String getMaxTorque() { return maxTorque != null ? maxTorque : "N/A"; }
    public String getKerbWeight() { return kerbWeight != null ? kerbWeight : "N/A"; }
    public String getMileage() { return mileage != null ? mileage : "N/A"; }
    public String getTransmission() { return transmission != null ? transmission : "N/A"; }
    public String getFuelTankCapacity() { return fuelTankCapacity != null ? fuelTankCapacity : "N/A"; }
    public String getBrakingSystem() { return brakingSystem != null ? brakingSystem : "N/A"; }
    public String getTopSpeed() { return topSpeed != null ? topSpeed : "N/A"; }
    
    public String getFrontBrakeType() { return frontBrakeType != null ? frontBrakeType : "Disc"; }
    public String getRearBrakeType() { return rearBrakeType != null ? rearBrakeType : "Drum"; }
    public String getFrontSuspension() { return frontSuspension != null ? frontSuspension : "Telescopic"; }
    public String getRearSuspension() { return rearSuspension != null ? rearSuspension : "Hydraulic"; }
    public String getTyreType() { return tyreType != null ? tyreType : "Tubeless"; }
    public String getHeadlight() { return headlight != null ? headlight : "LED"; }
    public String getTailLight() { return tailLight != null ? tailLight : "LED"; }
    public String getBatteryCapacity() { return batteryCapacity != null ? batteryCapacity : "N/A"; }

    public String getSpecifications() {
        // Return a summary string if needed for budget view
        if (engine != null && !engine.isEmpty()) return engine;
        return "N/A";
    }

    public boolean hasBadge() {
        return badge != null && !badge.isEmpty();
    }
}