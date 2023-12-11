package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameOverScreen implements Screen {

    private SylvanGame game;
    private Viewport viewport;
    private Stage stage;

    Label restartLevelText;
    Label restartGameText;

    Sprite arrow;

    public GameOverScreen(SylvanGame game) {

        this.game = game;
        viewport = new FitViewport(SylvanGame.SCREEN_WIDTH,SylvanGame.SCREEN_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, this.game.batch);

        Label.LabelStyle labelFont = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label.LabelStyle selectedFont = new Label.LabelStyle(new BitmapFont(), Color.CYAN);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label blank = new Label("",labelFont);

        Label gameOverText = new Label("Game Over", labelFont);
        restartLevelText = new Label("Restart Level", labelFont);
        restartGameText = new Label("Restart Game", labelFont); // selecting this sends player to main menu

        table.add(gameOverText).expandX();
        table.row();
        table.add(blank).expandX();
        table.row();
        table.add(restartLevelText).expandX();
        table.row();
        table.add(restartGameText).expandX();
        table.row();

        stage.addActor(table);
        createArrow();

    }

    public void createArrow() {
        arrow = new Sprite();
        Texture indicatorImg = new Texture(Gdx.files.internal("indicator.png"));
        arrow.setRegion(indicatorImg);
        arrow.setRotation(90);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

        Control input = processInput();

        float levelX = restartLevelText.getX();
        float levelY = restartLevelText.getY();

        float gameX = restartGameText.getX();
        float gameY = restartGameText.getY();

        game.batch.begin();

        game.batch.draw(arrow,levelX+5,levelY);

        /*
        if (input == Control.UP) {
            game.batch.draw(arrow,levelX+5,levelY);

        }

         */

        game.batch.end();





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
