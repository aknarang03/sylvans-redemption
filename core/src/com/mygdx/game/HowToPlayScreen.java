package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

/*
Anjali Narang
Aaila Arif
Jenna Esposito
 */

public class HowToPlayScreen implements Screen {

    private SylvanGame game;
    private OrthographicCamera camera;

    Texture tokenImg;
    Texture possessIndicatorImg;

    public HowToPlayScreen(SylvanGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        initIcons();
    }

    public void initIcons() { // icons to show next to the text for explanation purposes
        tokenImg = new Texture(Gdx.files.internal("token/soultoken_small.png"));
        possessIndicatorImg = new Texture(Gdx.files.internal("possess_indicator_small.png"));
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Control input = processInput();

        game.batch.begin();

        // DRAW HOW TO PLAY TEXT

        game.font.getData().setScale(1.3f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch,"How to Play",0,290);
        game.font.getData().setScale(1.1f);
        game.font.draw(game.batch, "Collect soul tokens",0,250);
        game.font.draw(game.batch, "Possess enemies when in range",0,230);
        game.font.draw(game.batch, "Different enemies have different movement abilities",0,210);
        game.font.draw(game.batch, "Avoid taking damage from enemy attacks",0,190);
        game.font.draw(game.batch, "Avoid falling in water",0,170);

        // DRAW ICONS

        game.batch.draw(tokenImg,145,235);
        game.batch.draw(possessIndicatorImg,225,190);

        game.batch.end();

        if (input == Control.SELECT) { // set screen to level
            game.pickLevel(game.currentLevel);
            game.uiSounds.get("select").play(1);
            game.uiSounds.get("start game overlay").play(1f);
            game.uiSounds.get("start game").play(1f);
        }

    }

    public Control processInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            return Control.SELECT;
        }
        return null;
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void show() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}

}