package com.mygdx.game.Entities;

import com.mygdx.game.Entity;

public class Sylvan extends Entity {

    private boolean possessed;

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

}
