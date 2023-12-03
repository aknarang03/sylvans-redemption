package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

// NOT IMPLEMENTED YET

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

            final float FORCE = 1f;
            final float NEG_FORCE = -1;

            if (left) {
                sylvan.getBody().applyForceToCenter(0.8f,0,true);
            } if (right) {
                sylvan.getBody().applyForceToCenter(-0.8f,0,true);
            } if (up) {
                sylvan.getBody().applyForceToCenter(0,NEG_FORCE,true);
            } if (down) {
                sylvan.getBody().applyForceToCenter(0,FORCE,true);
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
