package com.mygdx.game.Entities;

// Flying Enemy

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Control;
import com.mygdx.game.Entity;
import com.mygdx.game.SylvanGame;

public class Bat extends Entity {

    public Bat(SylvanGame game) {
        super(game);
    }

    @Override
    public void initBody() {

    }

    public void setPossessed(boolean possessed) {
        this.possessed = possessed;
    }

    @Override
    public void move(Control control) {

    }

    @Override
    public void initSprite() {

    }

    @Override
    public void updateFrame(float time, float dt) {
    }

}
