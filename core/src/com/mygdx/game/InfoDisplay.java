package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
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

    // WHAT TO DISPLAY
    double possessTimer;
    int collectedTokens;
    int totalTokens;
    String currentAbility;

    public InfoDisplay(SylvanGame game) {

        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth()/2.5f,Gdx.graphics.getHeight()/2.5f);
        viewport = new FitViewport(camera.viewportWidth, camera.viewportHeight, camera);
        stage = new Stage(viewport,game.batch);

        table = new Table();
        table.top();
        table.setFillParent(true);

        labelFont = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        possessTimerLabel = new Label("",labelFont);
        tokensLabel = new Label("",labelFont);
        currentAbilityLabel = new Label("",labelFont);

        table.add(tokensLabel).expandX().padTop(10);
        table.row();
        table.add(currentAbilityLabel).expandX().padTop(10);
        table.row();
        table.add(possessTimerLabel).expandX().padTop(10);

        stage.addActor(table);

    }

    public void getInfo() {
        possessTimer = game.currentLevel.possessTimer;
        collectedTokens = game.currentLevel.numTokensCollected;
        totalTokens = game.currentLevel.TOKEN_COUNT;
        currentAbility = game.currentLevel.currentInhabitedEntity.ability;
    }

    public void updateLabels() {

        getInfo();

        if (game.currentLevel.sylvan.possessed) {
            possessTimerLabel.setText("");
        } else {
            possessTimerLabel.setText("Unpossess in " + (5-(int)possessTimer));
        }

        tokensLabel.setText(collectedTokens + " / " + totalTokens + " tokens");
        currentAbilityLabel.setText("Ability: " + currentAbility);

    }

}
