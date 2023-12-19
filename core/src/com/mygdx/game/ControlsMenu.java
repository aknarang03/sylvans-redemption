package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ControlsMenu implements Screen {

    private SylvanGame game;
    private OrthographicCamera camera;

    public ControlsMenu(SylvanGame game) {

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

        game.font.getData().setScale(1.3f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch,"Controls",165,290);
        game.font.getData().setScale(1.1f);
        game.font.draw(game.batch, "Jump: UP/W",150,250);
        game.font.draw(game.batch, "Left: LEFT/A",150,230);
        game.font.draw(game.batch, "Right: RIGHT/D",150,210);
        game.font.draw(game.batch, "Possess / Unpossess: Click enemy, RSHIFT/E",30,190);
        game.font.draw(game.batch, "Select: ENTER",150,170);
        game.font.draw(game.batch, "Pause: ESC",150,150);
        game.font.draw(game.batch, "Glide: Hold Jump as Sylvan",100,110);
        game.font.draw(game.batch, "Fly: Hold Jump as Bat",100,90);
        game.font.draw(game.batch, "Climb: Hold Jump as Spider",100,70);
        game.font.draw(game.batch, "Press ENTER to return to Main Menu",80,30);

        game.batch.end();

        if (input == Control.SELECT) {
            // GO BACK TO MAIN MENU
            game.setScreen(game.mainMenu);
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
