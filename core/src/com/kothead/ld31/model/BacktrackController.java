package com.kothead.ld31.model;

import com.badlogic.gdx.Game;
import com.kothead.ld31.data.Configuration;
import com.kothead.ld31.screen.GameScreen;

/**
 * Created by st on 12/7/14.
 */
public class BacktrackController implements LabyrinthController {

    private Labyrinth labyrinth;

    public BacktrackController() {
        labyrinth = new LabyrinthBacktrack.Builder()
                .setCanMoveBack(false)
                .setWidth(Configuration.LABYRINTH_WIDTH)
                .setHeight(Configuration.LABYRINTH_HEIGHT)
                .setSeed(100)
                .setStartX(0)
                .setStartY(0)
                .create();
    }

    @Override
    public void moveTo(int x, int y) {

    }

    @Override
    public int getWidth() {
        return labyrinth.getWidth();
    }

    @Override
    public int getHeight() {
        return labyrinth.getHeight();
    }

    @Override
    public boolean hasWallRight(int x, int y) {
        return labyrinth.hasWallRight(x, y);
    }

    @Override
    public boolean hasWallBottom(int x, int y) {
        return labyrinth.hasWallBottom(x, y);
    }
}
