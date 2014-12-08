package com.kothead.ld31.view;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.kothead.ld31.data.Configuration;

import java.awt.*;

/**
 * Created by st on 12/8/14.
 */
public class Walker extends Sprite {

    public static final int SIZE = 10;

    private float vx, vy;
    private Rectangle rectWalker, rectObstacle, overlap;
    private boolean horizontalBlocked, verticalBlocked;
    private float speedValue;

    public Walker() {
        setSize(SIZE, SIZE);
        rectWalker = new Rectangle();
        rectWalker.setSize(SIZE, SIZE);
        rectObstacle = new Rectangle();
        overlap = new Rectangle();
    }

    public void draw(float delta, ShapeRenderer renderer) {
        if (!horizontalBlocked) setX(getX() + vx * delta);
        if (!verticalBlocked) setY(getY() + vy * delta);
    }

    public void setNotBlocked() {
        horizontalBlocked = false;
        verticalBlocked = false;
    }

    public void processWall(float delta, Wall wall) {
        rectObstacle.set(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
        if (!horizontalBlocked) {
            rectWalker.setPosition(getX() + vx * delta, getY());
            if (Intersector.intersectRectangles(rectWalker, rectObstacle, overlap)) {
                horizontalBlocked = true;
            }
        }
        if (!verticalBlocked) {
            rectWalker.setPosition(getX(), getY() + vy * delta);
            if (Intersector.intersectRectangles(rectWalker, rectObstacle, overlap)) {
                verticalBlocked = true;
            }
        }
    }

    public int getGridX() {
        return getGridX(getX() + getWidth() / 2f);
    }

    public int getGridY() {
        return getGridY(getY() + getHeight() / 2f);
    }

    public static int getGridX(float x) {
        return (int) (x / Configuration.LABYRINTH_CELL_SIZE);
    }

    public static int getGridY(float y) {
        return (int) (y / Configuration.LABYRINTH_CELL_SIZE);
    }

    public void goUp() {
        vy += speedValue;
    }

    public void goDown() {
        vy -= speedValue;
    }

    public void goLeft() {
        vx -= speedValue;
    }

    public void goRight() {
        vx += speedValue;
    }

    public void stopUp() {
        vy -= speedValue;
    }

    public void stopDown() {
        vy += speedValue;
    }

    public void stopLeft() {
        vx += speedValue;
    }

    public void stopRight() {
        vx -= speedValue;
    }

    public void stop() {
        vy = 0;
        vx = 0;
    }

    protected float getVx() {
        return vx;
    }

    protected float getVy() {
        return vy;
    }

    protected void setVx(float vx) {
        this.vx = vx;
    }

    protected void setVy(float vy) {
        this.vy = vy;
    }

    protected void setSpeedValue(float speedValue) {
        this.speedValue = speedValue;
    }
}
