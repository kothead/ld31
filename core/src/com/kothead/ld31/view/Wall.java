package com.kothead.ld31.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by st on 12/7/14.
 */
public class Wall extends Sprite {

    public static final int WALL_HEIGHT = 4;
    private static final int WALL_WIDTH = 36;

    private int gridX, gridY;
    private boolean right;

    public Wall() {
    }

    public void setPosition(boolean horizontal, int x, int y) {
        this.right = !horizontal;
        this.gridX = x;
        this.gridY = y;

        if (horizontal) {
            setX(x * WALL_WIDTH - WALL_HEIGHT / 2);
            setY(y * WALL_WIDTH - WALL_HEIGHT / 2);
            setSize(WALL_WIDTH + WALL_HEIGHT, WALL_HEIGHT);
        } else {
            setX((x + 1) * WALL_WIDTH - WALL_HEIGHT / 2);
            setY(y * WALL_WIDTH - WALL_HEIGHT / 2);
            setSize(WALL_HEIGHT, WALL_WIDTH + WALL_HEIGHT);
        }
    }

    public void draw(float delta, ShapeRenderer renderer) {
        renderer.setColor(Color.WHITE);
        renderer.rect(getX(), getY(), getWidth(), getHeight());
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public boolean isRight() {
        return right;
    }
}
