package com.kothead.ld31.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kothead.ld31.data.Labyrinth;
import com.kothead.ld31.screen.GameScreen;

/**
 * Created by st on 12/7/14.
 */
public class Lightmap {

    private static final float MAX_SHADOW = 1f;
    private static final float DELTA_SHADOW = 0.2f;

    private Labyrinth labyrinth;
    private int lightX, lightY;
    private int width, height;
    private float[][] map;

    public Lightmap(Labyrinth labyrinth) {
        width = labyrinth.getWidth();
        height = labyrinth.getHeight();
        this.labyrinth = labyrinth;
        map = new float[height][width];
    }

    public void setLightPosition(int lightX, int lightY) {
        this.lightX = lightX;
        this.lightY = lightY;
    }

    public void draw(ShapeRenderer renderer) {
        generateLightmap();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                renderer.setColor(0, 0, 0, map[i][j]);
                renderer.rect(GameScreen.GRID_SIZE * j, GameScreen.GRID_SIZE * i,
                        GameScreen.GRID_SIZE, GameScreen.GRID_SIZE);
            }
        }
    }

    private void generateLightmap() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = MAX_SHADOW;
            }
        }
        lightCell(lightX, lightY, 0);
    }

    private void lightCell(int x, int y, float value) {
        if (value >= MAX_SHADOW) return;
        if (x < 0 || x >= width) return;
        if (y < 0 || y >= height) return;

        if (value < map[y][x]) {
            map[y][x] = value;
        }

        value += DELTA_SHADOW;

        if (!labyrinth.hasHorizontalWall(x, y)) {
            lightCell(x, y + 1, value);
        }

        if (!labyrinth.hasVerticalWall(x, y)) {
            lightCell(x - 1, y, value);
        }

        if (y > 0 && !labyrinth.hasHorizontalWall(x, y - 1)) {
            lightCell(x, y - 1, value);
        }

        if (x < GameScreen.GRID_WIDTH - 1 && !labyrinth.hasVerticalWall(x + 1, y)) {
            lightCell(x + 1, y, value);
        }
    }
}
