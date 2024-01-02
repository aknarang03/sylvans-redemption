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

public class MainMenu implements Screen {

    // see GameOverScreen for explanation on PointingTo, arrow, etc

    enum PointingTo {StartGame, Controls};
    PointingTo pointingTo;

    SylvanGame game;
    private Viewport viewport;
    private Stage stage;

    Sprite menuSprite; // background

    Label startGameText;
    Label controlsText;

    Label.LabelStyle selectedFont;
    Label.LabelStyle labelFont;

    Sprite arrow;

    boolean gameStarted; // tracks whether player has already selected start game before

    public MainMenu(SylvanGame game) {

        this.game = game;

        viewport = new FitViewport(SylvanGame.SCREEN_WIDTH,SylvanGame.SCREEN_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, this.game.batch);

        labelFont = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        selectedFont = new Label.LabelStyle(new BitmapFont(), Color.CYAN);

        createArrow();
        createBg();

        Label blank = new Label("",labelFont);
        Label titleText = new Label("Sylvan's Redemption",labelFont);
        startGameText = new Label("Start Game", labelFont);
        controlsText = new Label("Controls",labelFont);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        table.add(titleText).expandX();
        table.row();
        table.add(blank).expandX();
        table.row();
        table.add(blank).expandX();
        table.row();
        table.add(blank).expandX();
        table.row();
        table.add(startGameText).expandX();
        table.row();
        table.add(controlsText).expandX();
        table.row();

        stage.addActor(table);

        pointingTo = PointingTo.StartGame;

    }

    public void createArrow() {
        arrow = new Sprite();
        Texture indicatorImg = new Texture(Gdx.files.internal("arrow.png"));
        arrow.setRegion(indicatorImg);
    }

    public void createBg() { // draw the background
        Texture menuImg = new Texture(Gdx.files.internal("background.png"));
        menuSprite = new Sprite(menuImg);
        menuSprite.setSize(game.SCREEN_WIDTH,game.SCREEN_HEIGHT);
        menuSprite.setPosition(0,0);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        menuSprite.draw(game.batch); // draw this first so it's behind everything
        game.batch.end();

        stage.draw();

        Control input = processInput();

        float startX = startGameText.getX();
        float startY = startGameText.getY();

        float controlsX = controlsText.getX();
        float controlsY = controlsText.getY();

        game.batch.begin();

        if (pointingTo == PointingTo.StartGame) {
            startGameText.setStyle(selectedFont);
            controlsText.setStyle(labelFont);
            game.batch.draw(arrow,startX+70,startY-25);
        } else {
            startGameText.setStyle(labelFont);
            controlsText.setStyle(selectedFont);
            game.batch.draw(arrow,controlsX+50,controlsY-25);
        }

        if (input == Control.UP || input == Control.DOWN) {
            if (pointingTo == PointingTo.StartGame) {
                pointingTo = PointingTo.Controls;
            } else {
                pointingTo = PointingTo.StartGame;
            }
        }

        game.batch.end();

        if (processInput() == Control.SELECT) {
            game.uiSounds.get("select").play(1f);
            if (pointingTo == PointingTo.StartGame) {
                if (!gameStarted) { // if game has never been started before
                    game.setScreen(game.story); // then start the story screen -> how to play -> level 1
                    gameStarted = true; // indicate that game has been started before
                } else { // if player has already seen this progression
                    game.pickLevel(game.currentLevel); // then simply pick level 1
                    game.uiSounds.get("start game overlay").play(1f);
                    game.uiSounds.get("start game").play(1f);
                }
            } else { // player selected controls menu
                game.setScreen(game.controlsMenu);
            }
        }

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
