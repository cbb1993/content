package com.huanhong.content.model.plan.server;


import java.util.List;

/**
 * Created by tuka2401 on 2017/3/21.
 */

public class ServerSplit {
    private String id;
    private float x,y,width,height;
    private List<ServerAction> action;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public List<ServerAction> getAction() {
        return action;
    }

    public void setAction(List<ServerAction> action) {
        this.action = action;
    }
}
