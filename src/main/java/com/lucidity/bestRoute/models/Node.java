package com.lucidity.bestRoute.models;

public class Node {
    private long id;
    private double distance;

    public Node(long id, double distance) {
        this.id = id;
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
