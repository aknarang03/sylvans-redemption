package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class InfoDisplay {

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

    Texture heartTexture;
    Texture tokenTexture;

    public InfoDisplay(SylvanGame game) {

        createHeart();
        createToken();

        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/1.2f);
        viewport = new FitViewport(camera.viewportWidth, camera.viewportHeight, camera);
        stage = new Stage(viewport,game.batch);

        table = new Table();
        table.setPosition(0,160);
        //table.left().top();
        table.defaults().left().top();
        table.setFillParent(true);

        labelFont = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

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

    public void getInfo() {
        possessTimer = game.currentLevel.possessTimer;
        collectedTokens = game.currentLevel.numTokensCollected;
        totalTokens = game.currentLevel.TOKEN_COUNT;
        currentAbility = game.currentLevel.currentInhabitedEntity.ability;
        health = game.currentLevel.sylvan.health;
    }

    public void updateLabels() {

        getInfo();

        if (game.currentLevel.sylvan.possessed) {
            possessTimerLabel.setText("");
        } else {
            possessTimerLabel.setText((int)(game.currentLevel.currentInhabitedEntity.posTime-(int)possessTimer));
        }

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

}
