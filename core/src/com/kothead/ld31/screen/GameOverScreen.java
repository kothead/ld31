package com.kothead.ld31.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kothead.ld31.LD31;
import com.kothead.ld31.data.SkinCache;

/**
 * Created by st on 12/9/14.
 */
public class GameOverScreen extends BaseScreen {

    private static final int BUTTON_WIDTH = 80;
    private static final int PADDING = 20;
    private static final int HEIGHT = 30;

    private Label message;
    private TextButton btnAgain, btnMenu;

    public GameOverScreen(LD31 game, boolean success) {
        super(game);
        Skin skin = SkinCache.getDefaultSkin();
        String text = success ? "Well, look at you,\n Smarty Pants!" : "You are dead";
        message = new Label(text, skin, "title");
        btnAgain = new TextButton("Again?", skin, "default");
        btnMenu = new TextButton("Menu", skin, "default");

        Table table = new Table();
        table.setFillParent(true);
        table.add(message).center().colspan(2).expandY().row();
        table.add(btnAgain).pad(PADDING).width(BUTTON_WIDTH).height(HEIGHT).right();
        table.add(btnMenu).pad(PADDING).width(BUTTON_WIDTH).height(HEIGHT).left().row();
        stage().addActor(table);

        setCallbacks();

        Gdx.input.setInputProcessor(stage());
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage().act(delta);
        stage().draw();
    }

    private void setCallbacks() {
        btnAgain.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getGame().setGameScreen();
            }
        });

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getGame().setStartScreen();
            }
        });
    }
}
