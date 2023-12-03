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

        if (enemy.getBody().getUserData() == "bat" || enemy.getBody().getUserData() == "spider") {
            sylvan.getBody().applyForceToCenter(0.5f,0.5f,true); // fix this later it's janky
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
