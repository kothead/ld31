package com.kothead.ld31.view;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.kothead.ld31.data.Configuration;
import com.kothead.ld31.data.ImageCache;

import static com.kothead.ld31.data.Configuration.*;

/**
 * Created by st on 12/8/14.
 */
public class Board extends Sprite {

    private static final String TEXTURE_NAME = "message";
;
    private int gridX, gridY;

    public Board() {
        super(ImageCache.getTexture(TEXTURE_NAME));
        setSize(LABYRINTH_CELL_SIZE, LABYRINTH_CELL_SIZE);
    }

    public void randomGridX() {
        gridX = (int) (Math.random() * (LABYRINTH_WIDTH - 4)) + 2;
        setX(gridX * LABYRINTH_CELL_SIZE);
    }

    public void randomGridY() {
        gridY = (int) (Math.random() * (LABYRINTH_HEIGHT - 4)) + 2;
        setY(gridY * LABYRINTH_CELL_SIZE);
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}
