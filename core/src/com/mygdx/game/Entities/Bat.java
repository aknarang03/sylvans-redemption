package com.mygdx.game.Entities;

// Flying Enemy

import com.mygdx.game.Entity;
import com.mygdx.game.SylvanGame;

public class Bat extends Entity {

    private boolean possessed;

    public Bat(SylvanGame game) {
        super(game);
    }

    public void setPossessed(boolean possessed) {
        this.possessed = possessed;
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
