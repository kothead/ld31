package com.kothead.ld31.screen;

import static com.kothead.ld31.data.Configuration.LABYRINTH_CELL_SIZE;
import static com.kothead.ld31.data.Configuration.LABYRINTH_WIDTH;
import static com.kothead.ld31.data.Configuration.LABYRINTH_HEIGHT;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kothead.ld31.LD31;
import com.kothead.ld31.data.*;
import com.kothead.ld31.model.BacktrackController;
import com.kothead.ld31.util.Messages;
import com.kothead.ld31.util.Util;
import com.kothead.ld31.view.*;

import java.util.Iterator;
import java.util.Random;

/**
 * Created by st on 12/7/14.
 */
public class GameScreen extends BaseScreen {

    private static final String TEXTURE_FLOOR = "floor";
    private static final int DAMAGE_TO_DESTROY_WALL = 110;
    private static final int MAX_ENEMIES = 20;

    private static final String UI_HIT = "hit : %d";
    private static final String UI_LVL = "lvl : %d";
    private static final String UI_EXP = "exp : %d";
    private static final int UI_HEIGHT = 15;
    private static final int UI_WIDTH = 50;

    public static long seed = 100;

    private TiledSprite background;
    private Player player;
    private Wall wall;
    private BacktrackController controller;
    private Lightmap lightmap;
    private Array<Bullet> bullets;
    private Array<Enemy> enemies;
    private Life life;
    private Portal portal;

    private Label labelHit, labelLvl, labelExp;

    private Messages messages;

    public GameScreen(LD31 game) {
        this(game, GameScreen.seed);
    }

    public GameScreen(LD31 game, long seed) {
        super(game);
        GameScreen.seed = seed;

        background = new TiledSprite(ImageCache.getTexture(TEXTURE_FLOOR),
                LABYRINTH_WIDTH * LABYRINTH_CELL_SIZE, LABYRINTH_HEIGHT * LABYRINTH_CELL_SIZE);

        player = new Player();
        float position = (Configuration.LABYRINTH_CELL_SIZE - Player.SIZE) / 2f;
        player.setPosition(position, position);
        portal = new Portal();
        wall = new Wall();

        controller = new BacktrackController(seed);
        bullets = new Array<Bullet>();
        enemies = new Array<Enemy>();
        lightmap = new Lightmap(controller);

        Label label = new Label(null, SkinCache.getDefaultSkin(), "message");
        stage().addActor(label);
        messages = new Messages(label, player);
        messages.setMessage(Messages.START_TUTORIAL);

        recreateThingsOnMap();
        createUi();

        Gdx.input.setInputProcessor(new Processor());
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch().begin();
        background.draw(batch(), 0, 0);
        if (life != null) life.draw(batch());
        for (int i = 0; i < controller.getHeight(); i++) {
            for (int j = 0; j < controller.getWidth(); j++) {
                if (controller.isPortal(j, i)) {
                    portal.setPositionGrid(j, i);
                    portal.draw(batch());
                }
            }
        }
        batch().end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes().begin(ShapeRenderer.ShapeType.Filled);

        player.setNotBlocked();
        for (int i = 0; i <= Configuration.LABYRINTH_HEIGHT; i++) {
            for (int j = -1; j < Configuration.LABYRINTH_WIDTH; j++) {
                if (controller.hasWallBottom(j, i)) {
                    wall.setPosition(true, j, i);
                    player.processWall(delta, wall);
                    wall.draw(delta, shapes());
                    processBullets(wall, delta);
                }

                if (controller.hasWallRight(j, i)) {
                    wall.setPosition(false, j, i);
                    player.processWall(delta, wall);
                    wall.draw(delta, shapes());
                    processBullets(wall, delta);
                }
            }
        }

        for (Bullet bullet: bullets) {
            bullet.draw(shapes(), delta);
        }

        Iterator<Enemy> enemyIter = enemies.iterator();
        while (enemyIter.hasNext()) {
            Enemy enemy = enemyIter.next();
            if (enemy.grab()) {
                enemyIter.remove();
                player.decLife();
                labelHit.setText(String.format(UI_HIT, player.getLifes()));
                SoundCache.play(SoundCache.SOUND_MINUS);
            }
        }

        for (Enemy enemy: enemies) {
            enemy.process();
            enemy.draw(delta, shapes());
        }

        player.updateLabyrinth(delta, controller);
        if (controller.isUpdated()) {
            recreateThingsOnMap();
        }
        player.draw(delta, shapes());
        lightmap.setLightPosition(player.getGridX(), player.getGridY());
        lightmap.draw(shapes());

        shapes().end();

        messages.process(delta);
        stage().act(delta);
        stage().draw();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        if (player.checkLife(life)) {
            life = null;
            labelHit.setText(String.format(UI_HIT, player.getLifes()));
            SoundCache.play(SoundCache.SOUND_BONUS);
        }

        if (player.levelUp()) {
            messages.setMessage(Messages.MESSAGE_NEW_LEVEL);
            labelLvl.setText(String.format(UI_LVL, player.getLevel()));
            SoundCache.play(SoundCache.SOUND_BONUS);
        }
        if (player.isDead()) {
            getGame().setGameOverScreen(false);
            SoundCache.play(SoundCache.SOUND_DEATH);
        }
        if (player.isOutThere()) {
            getGame().setGameOverScreen(true);
            SoundCache.play(SoundCache.SOUND_DEATH);
        }
    }

    private void processBullets(Wall wall, float delta) {
        boolean removing = false;
        boolean destroying = false;

        Iterator<Bullet> iterBullet = bullets.iterator();
        while (iterBullet.hasNext()) {
            Bullet bullet = iterBullet.next();
            if (bullet.collided(wall, delta)) {
                iterBullet.remove();
                removing = true;
                if (bullet.getDamage() >= DAMAGE_TO_DESTROY_WALL) {
                    if (wall.isRight()) {
                        controller.destroyWallRight(wall.getGridX(), wall.getGridY());
                        destroying = true;
                    } else {
                        controller.destroyWallBottom(wall.getGridX(), wall.getGridY());
                        destroying = true;
                    }
                }
                continue;
            }

            if (bullet.farAway()) {
                iterBullet.remove();
                continue;
            }

            for (int i = 0; i < enemies.size; i++) {
                Enemy enemy = enemies.get(i);
                if (enemy.hit(bullet)) {
                    iterBullet.remove();
                    removing = true;
                    break;
                }
            }

        }

        Iterator<Enemy> iterEnemy = enemies.iterator();
        while (iterEnemy.hasNext()) {
            Enemy enemy = iterEnemy.next();
            if (enemy.isDead()) {
                destroying = true;
                iterEnemy.remove();
                int exp = enemy.getExp();
                player.addExp(exp);
                messages.setMessage(Messages.MESSAGE_GAIN_EXP, exp);
                labelExp.setText(String.format(UI_EXP, player.getExp()));
            }
        }

        if (removing) SoundCache.play(SoundCache.SOUND_HIT);
        if (destroying) SoundCache.play(SoundCache.SOUND_EXPLOSION);
    }

    private void recreateThingsOnMap() {
        Random random = Util.getRandom();
        random.setSeed(seed);

        enemies.clear();
        int count = (int) (random.nextFloat() * MAX_ENEMIES);
        for (int i = 0; i < count; i++) {
            Enemy enemy = new Enemy(controller, player, seed + i);
            enemy.setLevel(controller.getLevel());
            enemies.add(enemy);
        }

        life = new Life(seed);
    }

    private void createUi() {
        Skin skin = SkinCache.getDefaultSkin();
        labelExp = new Label(String.format(UI_EXP, player.getExp()), skin, "message");
        labelHit = new Label(String.format(UI_HIT, player.getLifes()), skin, "message");
        labelLvl = new Label(String.format(UI_LVL, player.getLevel()), skin, "message");
        Table table = new Table();
        table.setFillParent(true);
        table.add(labelExp).expandX().width(UI_WIDTH).height(UI_HEIGHT).right().row();
        table.add(labelHit).expandX().width(UI_WIDTH).height(UI_HEIGHT).right().row();
        table.add(labelLvl).expand().width(UI_WIDTH).height(UI_HEIGHT).right().top();
        stage().addActor(table);
    }

    private class Processor extends InputAdapter {
        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Input.Keys.W:
                    player.stopUp();
                    return true;

                case Input.Keys.A:
                    player.stopLeft();
                    return true;

                case Input.Keys.S:
                    player.stopDown();
                    return true;

                case Input.Keys.D:
                    player.stopRight();
                    return true;

                case Input.Keys.UP:
                    bullets.add(player.shoot(Direction.TOP));
                    return true;

                case Input.Keys.RIGHT:
                    bullets.add(player.shoot(Direction.RIGHT));
                    return true;

                case Input.Keys.DOWN:
                    bullets.add(player.shoot(Direction.BOTTOM));
                    return true;

                case Input.Keys.LEFT:
                    bullets.add(player.shoot(Direction.LEFT));
                    return true;

                case Input.Keys.ESCAPE:
                    getGame().setStartScreen();
                    return true;
            }

            return super.keyDown(keycode);
        }

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.W:
                    player.goUp();
                    return true;

                case Input.Keys.A:
                    player.goLeft();
                    return true;

                case Input.Keys.S:
                    player.goDown();
                    return true;

                case Input.Keys.D:
                    player.goRight();
                    return true;
            }

            return super.keyDown(keycode);
        }
    }
}
