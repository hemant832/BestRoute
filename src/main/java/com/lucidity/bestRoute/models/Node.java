package com.lucidity.bestRoute.models;

public class Node {
    private long id;
    private double time;

    public Node(long id, double time) {
        this.id = id;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
