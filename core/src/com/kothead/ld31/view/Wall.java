package com.kothead.ld31.view;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by st on 12/7/14.
 */
public class Wall {

    private static final int WALL_WIDTH = 36;
    private static final int WALL_HEIGHT = 4;

    private float x, y;
    private boolean horizontal;

    public Wall() {}

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setHorizontal() {
        horizontal = true;
    }

    public void setVertical() {
        horizontal = false;
    }

    public void draw(float delta, ShapeRenderer renderer) {
        if (horizontal) {
            renderer.rect(x * WALL_WIDTH - WALL_HEIGHT / 2, (y + 1) * WALL_WIDTH - WALL_HEIGHT,
                    WALL_WIDTH, WALL_HEIGHT + WALL_HEIGHT / 2);
        } else {
            renderer.rect(x * WALL_WIDTH - WALL_HEIGHT / 2, y * WALL_WIDTH,
                    WALL_HEIGHT + WALL_HEIGHT / 2, WALL_WIDTH);
        }
    }

}
