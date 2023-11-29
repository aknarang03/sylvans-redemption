package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Entity;
import com.mygdx.game.SylvanGame;

import java.util.HashMap;

public class Sylvan extends Entity {

    private final HashMap<String, Animation<TextureRegion>> animations = new HashMap();
    private TextureAtlas atlas;



    public Sylvan(SylvanGame game, Vector2 initPos) {
        super(game); // set the game
        initialPosition = initPos;

    }

    @Override
    public boolean keyDown(int keycode) {
        if (!possessed) { return false; }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!possessed) { return false; }
        return false;
    }
    @Override
    public void initSprite() {

    }

}
