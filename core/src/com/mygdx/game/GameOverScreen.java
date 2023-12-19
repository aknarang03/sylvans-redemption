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

public class GameOverScreen implements Screen {

    enum PointingTo {RestartLevel, RestartGame}; // for indicating which label the arrow is pointing to
    PointingTo pointingTo; // instance of pointingTo enum to use

    // setup
    private SylvanGame game;
    private Viewport viewport;
    private Stage stage;

    // labels
    Label restartLevelText;
    Label restartGameText;

    // fonts
    Label.LabelStyle selectedFont; // font to show if arrow is pointing to label
    Label.LabelStyle labelFont; // font to show if arrow is not pointing to label

    Sprite arrow;

    public GameOverScreen(SylvanGame game) {

        this.game = game;
        viewport = new FitViewport(SylvanGame.SCREEN_WIDTH,SylvanGame.SCREEN_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, this.game.batch);

        // init fonts
        labelFont = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        selectedFont = new Label.LabelStyle(new BitmapFont(), Color.CYAN);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label blank = new Label("",labelFont); // for adding blank rows for space

        Label gameOverText = new Label("Game Over", labelFont); // not selectable, just a title
        restartLevelText = new Label("Restart Level", labelFont); // selecting this restarts current level
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

        pointingTo = PointingTo.RestartLevel; // arrow starts at Restart Level text

    }

    public void createArrow() {
        arrow = new Sprite();
        Texture indicatorImg = new Texture(Gdx.files.internal("arrow.png"));
        arrow.setRegion(indicatorImg);
    }



    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();

        Control input = processInput();

        // get the xs and ys of the texts so that arrow can point to them

        float levelX = restartLevelText.getX();
        float levelY = restartLevelText.getY();

        float gameX = restartGameText.getX();
        float gameY = restartGameText.getY();

        game.batch.begin();

        // set arrow to be next to whatever text is being pointed to
        if (pointingTo == PointingTo.RestartLevel) {
            restartLevelText.setStyle(selectedFont);
            restartGameText.setStyle(labelFont);
            game.batch.draw(arrow,levelX+70,levelY-25);
        } else {
            restartLevelText.setStyle(labelFont);
            restartGameText.setStyle(selectedFont);
            game.batch.draw(arrow,gameX+70,gameY-25);
        }

        // change pointingTo if user does UP or DOWN arrows
        if (input == Control.UP || input == Control.DOWN) {
            if (pointingTo == PointingTo.RestartGame) {
                pointingTo = PointingTo.RestartLevel;
            } else {
                pointingTo = PointingTo.RestartGame;
            }
        }

        // select whatever user is pointing to
        if (input == Control.SELECT) {
            game.uiSounds.get("select").play(1);
            if (pointingTo == PointingTo.RestartGame) {
                game.restartGame();
            } else {
                game.restartLevel(game.currentLevel.id);
            }
        }

        game.batch.end();

    }

    public Control processInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            return Control.UP;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            return Control.DOWN;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            return Control.SELECT;
        }
        return null;
    }

    @Override
    public void show() {}
    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}

}
