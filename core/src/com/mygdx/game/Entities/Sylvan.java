package com.mygdx.game.Entities;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Entity;
import com.mygdx.game.SylvanGame;

public class Sylvan extends Entity {



    public Sylvan(SylvanGame game, Vector2 initPos) {
        super(game); // set the game
        initialPosition = initPos;
        initBody();
        body.setUserData("Sylvan");
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
