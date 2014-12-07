package com.kothead.ld31.model;

/**
 * Created by st on 12/7/14.
 */
public interface Labyrinth {

    int getWidth();
    int getHeight();
    boolean hasWallRight(int x, int y);
    boolean hasWallBottom(int x, int y);
}
