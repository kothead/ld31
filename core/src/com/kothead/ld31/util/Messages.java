package com.kothead.ld31.util;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kothead.ld31.view.Player;

/**
 * Created by st on 12/8/14.
 */
public class Messages {

    private static final float TIME_FOR_MESSAGE = 5f;

    public static final int START_TUTORIAL = 0;
    public static final int MESSAGE_NULL = 3;
    public static final int MESSAGE_PRESS_SPACE = 4;
    public static final int MESSAGE_GAIN_EXP = 5;
    public static final int MESSAGE_NEW_LEVEL = 6;

    private static final String[] DEFAULT_MESSAGES = {
            "press WASD to move",
            "use ARROW keys to shoot",
            "find the exit",
            "",
            "press SPACE",
            "gain +%s exp",
            "level up!"
    };

    private static final float MESSAGE_HORIZONTAL_PADDING = -20;
    private static final float MESSAGE_VERTICAL_PADDING = 40;

    private Label label;
    private Player player;
    private float timer;
    private int current = MESSAGE_NULL;

    public Messages(Label label, Player player) {
        this.label = label;
        this.player = player;
    }

    public void process(float delta) {
        if (current == MESSAGE_NULL) return;
        label.setPosition(player.getX()/* - label.getPrefWidth() / 2f + player.getWidth() / 2f*/,
                player.getY() + MESSAGE_VERTICAL_PADDING);

        timer += delta;
        if (timer < TIME_FOR_MESSAGE) return;

        timer = 0;
        switch (current) {
            case 0:
            case 1:
            case 2:
                setMessage(current + 1);
                break;

            case MESSAGE_PRESS_SPACE:
            case MESSAGE_GAIN_EXP:
            case MESSAGE_NEW_LEVEL:
                setMessage(MESSAGE_NULL);
                break;
        }
    }

    public void setMessage(int message, Object... objs) {
        if ((current == 0 || current == 1 || current == 2)
                && message != 0 && message != 1
                && message != 2 && message != MESSAGE_NULL) return;
        current = message;
        timer = 0;
        label.setText(String.format(DEFAULT_MESSAGES[message], objs));
    }
}
