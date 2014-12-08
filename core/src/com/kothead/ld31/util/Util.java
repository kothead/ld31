package com.kothead.ld31.util;

import com.badlogic.gdx.Gdx;

import java.util.Random;

/**
 * Created by st on 12/8/14.
 */
public class Util {

    private static Random random;

    public static Random getRandom() {
        if (random == null) random = new Random();
        return random;
    }

    public static void logArray(int[][] array) {
        int height = array.length;
        if (height == 0) return;
        int width = array[0].length;

        for (int i = height - 1; i >= 0; i--) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < width; j++) {
                builder.append(String.format("%2d ", array[i][j]));
            }
            Gdx.app.log("wall", builder.toString());
        }
        Gdx.app.log("wall", "--------------------------------------------");
    }
}
