package com.mygdx.game.Entities;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Control;
import com.mygdx.game.Entity;
import com.mygdx.game.SylvanGame;

public class Spider extends Entity {

    @Override
    public void aiMove(float dt) {

    }

    public Spider(SylvanGame game) {
        super(game);
    }

    @Override
    public void initBody() {

        // set body user data to "spider"
    }

    @Override
    public void move(Control control) {

    }

    @Override
    public void initSprite() {

    }

    @Override
    public void update(float time, float dt) {

    }

    @Override
    public State getState() {
        return null;
    }

    /*
    @Override
    public boolean shouldFlip() {
        return false;
    }
     */
}
