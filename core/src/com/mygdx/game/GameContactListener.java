package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

// PLAN TO PREVENT WALL JUMP
// make a constructor that takes in sylvan
// store it
// make map wall object layer
// build walls seperately in the level and set user data to "wall"
// in move() remove the abs check
// do something in here to check if sylvan is hitting the wall
// if so send some boolean? back to sylvan
// use that to check in "fall" if he's hitting a wall and if he is dont allow jump

public class GameContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {

        Fixture enemy = (contact.getFixtureA().getBody().getUserData() == "sylvan") ? contact.getFixtureB() : contact.getFixtureA();
        Fixture sylvan = (contact.getFixtureA().getBody().getUserData() == "sylvan") ? contact.getFixtureA() : contact.getFixtureB();

        // KNOCKBACK

        if (enemy.getBody().getUserData() == "bat" || enemy.getBody().getUserData() == "spider") {

            double velx = sylvan.getBody().getLinearVelocity().x;
            double vely = sylvan.getBody().getLinearVelocity().y;

            boolean left = (velx < 0);
            boolean right = (velx > 0);
            boolean up = (vely > 0);
            boolean down = (vely < 0);

            final float FORCEUP = 1f;
            final float FORCEDOWN = -1;
            final float FORCERIGHT = 0.8f;
            final float FORCELEFT = -0.8f;

            if (left && up) {
                sylvan.getBody().applyForceToCenter(FORCERIGHT,FORCEDOWN,true);
            } else if (right && up) {
                sylvan.getBody().applyForceToCenter(FORCELEFT,FORCEDOWN,true);
            } else if (up) {
                sylvan.getBody().applyForceToCenter(0,FORCEDOWN,true);
            } else if (left && down) {
                sylvan.getBody().applyForceToCenter(FORCERIGHT,FORCEUP,true);
            } else if (right && down) {
                sylvan.getBody().applyForceToCenter(FORCELEFT,FORCEUP,true);
            } else if (down) {
                sylvan.getBody().applyForceToCenter(0,FORCEUP,true);
            } else if (left) {
                sylvan.getBody().applyForceToCenter(FORCERIGHT,0,true);
            } else if (right) {
                sylvan.getBody().applyForceToCenter(FORCELEFT,0,true);
            }

            // he will take damage here too
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    // JUNK
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

}
