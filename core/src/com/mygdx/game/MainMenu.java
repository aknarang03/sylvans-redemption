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

public class MainMenu implements Screen {

    enum PointingTo {StartGame, Controls};
    PointingTo pointingTo;

    SylvanGame game;
    private Viewport viewport;
    private Stage stage;

    Sprite menuSprite;

    OrthographicCamera camera;

    Label startGameText;
    Label controlsText;

    Label.LabelStyle selectedFont;
    Label.LabelStyle labelFont;

    Sprite arrow;

    boolean gameStarted;

    // MAKE START GAME AND CONTROLS SELECTABLE TEXT

    // for some reason, when I make one main menu to use, it messes up the width and height. but if I send in a new one, it doesn't until the second time.

    public MainMenu(SylvanGame game) {

        /*
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.SCREEN_WIDTH, game.SCREEN_HEIGHT);
         */

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

    public void createBg() {
        Texture menuImg = new Texture(Gdx.files.internal("background.png"));
        menuSprite = new Sprite(menuImg);
        menuSprite.setSize(game.SCREEN_WIDTH,game.SCREEN_HEIGHT);
        menuSprite.setPosition(0,0);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        menuSprite.draw(game.batch);
        game.batch.end();

        stage.draw();

        Control input = processInput();

        //camera.update();
        //game.batch.setProjectionMatrix(camera.combined);

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
                if (!gameStarted) {
                    //game.setScreen(game.howToPlay);
                    game.setScreen(game.story);
                } else {
                    game.pickLevel(game.currentLevel);
                    game.uiSounds.get("start game overlay").play(1f);
                    game.uiSounds.get("start game").play(1f);
                }
                gameStarted = true;
            } else {
                // GO TO CONTROLS SCREEN
                game.setScreen(game.controlsMenu);
            }

        }

    }

    public Control processInput() { // idk how this will work yet. may make it void

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
