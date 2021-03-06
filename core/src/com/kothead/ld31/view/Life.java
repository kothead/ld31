package com.kothead.ld31.view;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.kothead.ld31.data.Configuration;
import com.kothead.ld31.data.ImageCache;
import com.kothead.ld31.util.Util;

import java.util.Random;

import static com.kothead.ld31.data.Configuration.*;

/**
 * Created by st on 12/8/14.
 */
public class Life extends Sprite {

    private static final String TEXTURE_NAME = "heart";
;
    private int gridX, gridY;

    public Life(long seed) {
        super(ImageCache.getTexture(TEXTURE_NAME));
        setSize(LABYRINTH_CELL_SIZE, LABYRINTH_CELL_SIZE);

        Random random = Util.getRandom();
        random.setSeed(seed);
        random.nextBoolean();
        gridX = (int) (random.nextFloat() * (LABYRINTH_WIDTH - 4)) + 2;
        setX(gridX * LABYRINTH_CELL_SIZE);

        gridY = (int) (random.nextFloat() * (LABYRINTH_HEIGHT - 4)) + 2;
        setY(gridY * LABYRINTH_CELL_SIZE);
    }


    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}
