package com.kothead.ld31.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by st on 12/7/14.
 */
public class Player {

    private static final int SIZE = 10;
    private static final float SPEED = 100f;

    private float x, y;
    private float vx, vy;

    public Player() {

    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void draw(float delta, ShapeRenderer renderer) {
        x += vx * delta;
        y += vy * delta;
        renderer.setColor(Color.WHITE);
        renderer.circle(x, y, SIZE);
    }

    public void goUp() {
        vy += SPEED;
    }

    public void goDown() {
        vy -= SPEED;
    }

    public void goLeft() {
        vx -= SPEED;
    }

    public void goRight() {
        vx += SPEED;
    }

    public void stopUp() {
        vy -= SPEED;
    }

    public void stopDown() {
        vy += SPEED;
    }

    public void stopLeft() {
        vx += SPEED;
    }

    public void stopRight() {
        vx -= SPEED;
    }
}
