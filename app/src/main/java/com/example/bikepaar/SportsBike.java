package com.example.bikepaar;

import com.google.gson.annotations.SerializedName;

public class SportsBike {
    @SerializedName(value = "name", alternate = {"model", "bike_name"})
    public String name;
    
    @SerializedName(value = "description", alternate = {"category", "desc"})
    public String description;
    
    public float rating;
    public String engine;
    
    @SerializedName(value = "brand", alternate = {"make", "manufacturer"})
    public String brand;

    public boolean isFavorite;

    @SerializedName(value = "price", alternate = {"cost", "ex-showroom price"})
    public Object price;
    
    @SerializedName(value = "imageUrl", alternate = {"image", "img", "picture"})
    public String imageUrl;

    @SerializedName("max_power")
    public String maxPower;

    @SerializedName("max_torque")
    public String maxTorque;

    @SerializedName("kerb_weight")
    public String kerbWeight;

    @SerializedName("mileage")
    public String mileage;

    @SerializedName("transmission")
    public String transmission;
    
    @SerializedName("fuel_tank_capacity")
    public String fuelTankCapacity;

    @SerializedName("braking_system")
    public String brakingSystem;

    @SerializedName("top_speed")
    public String topSpeed;

    @SerializedName("front_brake_type")
    public String frontBrakeType;

    @SerializedName("rear_brake_type")
    public String rearBrakeType;
    
    @SerializedName("front_suspension")
    public String frontSuspension;

    @SerializedName("rear_suspension")
    public String rearSuspension;

    @SerializedName("tyre_type")
    public String tyreType;

    @SerializedName("headlight")
    public String headlight;

    @SerializedName("tail_light")
    public String tailLight;
    
    @SerializedName("battery_capacity")
    public String batteryCapacity;

    // No-arg constructor for Gson
    public SportsBike() {
    }

    public SportsBike(String name, String description, float rating, String engine, int price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.engine = engine;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEngine() {
        return engine;
    }

    public int getPrice() {
        if (price instanceof Number) {
            return ((Number) price).intValue();
        } else if (price instanceof String) {
            try {
                String s = ((String) price).replaceAll("[^\\d]", "");
                if (s.isEmpty()) return 0;
                return Integer.parseInt(s);
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBrand() {
        return brand;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

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
}