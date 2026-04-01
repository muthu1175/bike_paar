package com.example.bikepaar;

public class CompareItem {
    private Bike bike1;
    private Bike bike2;

    public CompareItem(Bike bike1, Bike bike2) {
        this.bike1 = bike1;
        this.bike2 = bike2;
    }

    public Bike getBike1() {
        return bike1;
    }

    public Bike getBike2() {
        return bike2;
    }
}
