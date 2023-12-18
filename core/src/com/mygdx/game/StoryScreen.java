package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class StoryScreen implements Screen {

    private SylvanGame game;
    private OrthographicCamera camera;

    public StoryScreen(SylvanGame game) {

        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Control input = processInput();

        game.batch.begin();

        game.font.getData().setScale(0.9f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch,"Story",0,290);
        game.font.draw(game.batch, "Overlord Malgrim of the Abyss has turned Sylvan into",0,250);
        game.font.draw(game.batch, "a wandering soul with a fragmented memory.",0,230);
        game.font.draw(game.batch, "He now must enter the Abyss to collect his scattered essence.",0,210);
        game.font.draw(game.batch, "Thankfully, as a soul, Sylvan can possess the overlord's",0,190);
        game.font.draw(game.batch, "adversaries to navigate the Abyss more easily.",0,170);
        game.font.draw(game.batch, "Help Sylvan become whole again and remember his past.",0,150);

        game.batch.end();

        if (input == Control.SELECT) {
            // SET SCREEN TO LEVEL
            game.setScreen(game.howToPlay);
            game.uiSounds.get("select").play(1);
        }

    }

    public Control processInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            return Control.SELECT;
        }
        return null;
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