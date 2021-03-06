package com.kothead.ld31.model;

/**
 * Created by st on 12/7/14.
 */
public interface LabyrinthController {

    void moveTo(int x, int y);
    int getWidth();
    int getHeight();
    boolean hasWallRight(int x, int y);
    boolean hasWallBottom(int x, int y);
}
