package com.kothead.ld31.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kothead.ld31.LD31;
import com.kothead.ld31.data.SkinCache;

/**
 * Created by st on 12/9/14.
 */
public class StartScreen extends BaseScreen {

    private static final int BUTTON_WIDTH = 80;
    private static final int PADDING = 20;
    private static final int TEXT_FIELD_WIDTH = 2 * (BUTTON_WIDTH + PADDING);
    private static final int HEIGHT = 30;

    private Label title, info;
    private TextButton btnStart, btnExit;
    private TextField textField;

    public StartScreen(LD31 game) {
        super(game);
        Skin skin = SkinCache.getDefaultSkin();
        title = new Label("PaPaPaPath!!!", skin, "title");
        btnStart = new TextButton("Start", skin, "default");
        btnExit = new TextButton("Exit", skin, "default");
        textField = new TextField(Long.toString(GameScreen.seed), skin, "default");
        info = new Label("kothead 2014 Ludum Dare 31", skin, "message");
        Table table = new Table();
        table.setFillParent(true);
        table.add(title).center().colspan(2).expandY().row();
        table.add(btnStart).pad(PADDING).width(BUTTON_WIDTH).height(HEIGHT).right();
        table.add(btnExit).pad(PADDING).width(BUTTON_WIDTH).height(HEIGHT).left().row();
        table.add(textField).width(TEXT_FIELD_WIDTH).height(HEIGHT).colspan(2).row();
        table.add(info).colspan(2).expandY().bottom().pad(PADDING);
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
        btnStart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                String text = textField.getText();
                Long seed = null;
                try {
                    seed = Long.parseLong(text);
                } catch (NumberFormatException e) {

                }
                if (seed == null) {
                    getGame().setGameScreen();
                } else {
                    getGame().setGameScreen(seed);
                }
            }
        });

        btnExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.exit();
            }
        });
    }
}
