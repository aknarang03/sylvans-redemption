package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/*
Anjali Narang
Aaila Arif
Jenna Esposito
 */

public class InfoDisplay implements Disposable {

    SylvanGame game;

    Stage stage;
    Viewport viewport;
    OrthographicCamera camera;

    Table table;

    Label.LabelStyle labelFont;

    Label possessTimerLabel;
    Label tokensLabel;
    Label currentAbilityLabel;
    Label healthLabel;

    // WHAT TO DISPLAY
    double possessTimer;
    int collectedTokens;
    int totalTokens;
    int health = 0;
    String currentAbility;

    // ICONS
    Texture heartTexture;
    Texture tokenTexture;

    public InfoDisplay(SylvanGame game) {

        // init the icons
        createHeart();
        createToken();

        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(640, 480, camera);
        stage = new Stage(viewport,game.batch);

        table = new Table();
        table.setPosition(0,210);
        table.defaults().left().top(); // makes text go to top left of the screen
        table.setFillParent(true);

        labelFont = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        // for now, set labels to be blank; they will be filled in on update
        possessTimerLabel = new Label("",labelFont);
        tokensLabel = new Label("",labelFont);
        currentAbilityLabel = new Label("",labelFont);
        healthLabel = new Label("",labelFont);

        table.add(tokensLabel).expandX();
        table.row();
        table.add(currentAbilityLabel).expandX();
        table.row();
        table.add(possessTimerLabel).expandX();

        stage.addActor(table);

    }

    public void getInfo() { // get info from the level for updating the labels
        possessTimer = game.currentLevel.possessTimer;
        collectedTokens = game.currentLevel.numTokensCollected;
        totalTokens = game.currentLevel.TOKEN_COUNT;
        currentAbility = game.currentLevel.currentInhabitedEntity.ability;
        health = game.currentLevel.sylvan.health;
    }

    public void updateLabels() { // called every frame

        getInfo(); // update the vars for labels

        if (game.currentLevel.sylvan.possessed) {
            possessTimerLabel.setText(""); // if Sylvan is not possessing anyone then the timer label can be blank
        } else { // otherwise set the timer label using entity's max allowed possession time minus how much time has passed
            possessTimerLabel.setText("Unpossess in " + (int)(game.currentLevel.currentInhabitedEntity.posTime-(int)possessTimer));
        }

        // set the rest of the labels based on data from getInfo()
        healthLabel.setText("Health: " + health);
        tokensLabel.setText("    " + collectedTokens + " / " + totalTokens);
        currentAbilityLabel.setText("Ability: " + currentAbility);

    }

    public void createHeart() {
        heartTexture = new Texture(Gdx.files.internal("heart/heart_small.png"));
    }

    public void createToken() {
        tokenTexture = new Texture(Gdx.files.internal("token/soultoken_small.png"));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
