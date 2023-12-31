package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/*
Anjali Narang
Aaila Arif
Jenna Esposito
 */

public class GameCompleteScreen implements Screen {

    private SylvanGame game;

    private Viewport viewport;
    private Stage stage;

    Label gameCompletedText;
    Label.LabelStyle labelFont;

    public GameCompleteScreen (SylvanGame game) {

        this.game = game;

        viewport = new FitViewport(SylvanGame.SCREEN_WIDTH,SylvanGame.SCREEN_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, this.game.batch);

        labelFont = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true); // size actor to the stage

        gameCompletedText = new Label("Currently, there are only two levels.\nPress Enter to return to Main Menu.", labelFont);
        table.add(gameCompletedText).expandX(); // occupy full X of the row

        stage.addActor(table);

    }


    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();

        Control input = processInput(); // get any input

        game.batch.begin();

        if (input == Control.SELECT) { // restart the game if Enter pressed
            game.uiSounds.get("select").play(1);
            game.restartGame();
        }

        game.batch.end();

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
