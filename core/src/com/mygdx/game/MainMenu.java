package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class MainMenu implements Screen {

    SylvanGame game;

    Texture menuImg;
    Sprite menuSprite;

    OrthographicCamera camera;

    // MAKE START GAME AND CONTROLS SELECTABLE TEXT

    public MainMenu(SylvanGame game) {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.SCREEN_WIDTH, game.SCREEN_HEIGHT);

        this.game = game;
        menuImg = new Texture(Gdx.files.internal("badlogic.jpg"));
        menuSprite = new Sprite(menuImg);
        menuSprite.setSize(game.SCREEN_WIDTH,game.SCREEN_HEIGHT);
        menuSprite.setPosition(0,0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        menuSprite.draw(game.batch);

        game.batch.end();

        if (processInput() == Control.SELECT) {
            game.uiSounds.get("start game overlay").play(1f);
            game.uiSounds.get("start game").play(1f);
            game.pickLevel(game.levels.get(0));
        }

    }

    public Control processInput() { // idk how this will work yet. may make it void

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            return Control.UP;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            return Control.DOWN;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            return Control.SELECT;
        }
        return Control.POSSESS;
    }







    @Override
    public void show() {

    }



    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
