package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameContactListener implements ContactListener {

    Level level;

    GameContactListener(Level level) { // or take Game game?
        this.level = level;
    }

    @Override
    public void beginContact(Contact contact) {

        // figure out which fixture is enemy and which one is player
        Fixture other = (contact.getFixtureA().getBody().getUserData() == level.currentInhabitedEntity.getBody().getUserData()) ? contact.getFixtureB() : contact.getFixtureA();
        Fixture player = (contact.getFixtureA().getBody().getUserData() == level.currentInhabitedEntity.getBody().getUserData()) ? contact.getFixtureA() : contact.getFixtureB();

        // KNOCKBACK

        if (player.getBody().getUserData() == "sylvan" && (other.getBody().getUserData() == "bat" || other.getBody().getUserData() == "spider")) {

            double velx = player.getBody().getLinearVelocity().x;
            double vely = player.getBody().getLinearVelocity().y;

            boolean left = (velx < 0);
            boolean right = (velx > 0);
            boolean up = (vely > 0);
            boolean down = (vely < 0);

            final float FORCEUP = 1f;
            final float FORCEDOWN = -1;
            final float FORCERIGHT = 0.8f;
            final float FORCELEFT = -0.8f;

            if (left && up) {
                player.getBody().applyForceToCenter(FORCERIGHT,FORCEDOWN,true);
            } else if (right && up) {
                player.getBody().applyForceToCenter(FORCELEFT,FORCEDOWN,true);
            } else if (up) {
                player.getBody().applyForceToCenter(0,FORCEDOWN,true);
            } else if (left && down) {
                player.getBody().applyForceToCenter(FORCERIGHT,FORCEUP,true);
            } else if (right && down) {
                player.getBody().applyForceToCenter(FORCELEFT,FORCEUP,true);
            } else if (down) {
                player.getBody().applyForceToCenter(0,FORCEUP,true);
            } else if (left) {
                player.getBody().applyForceToCenter(FORCERIGHT,0,true);
            } else if (right) {
                player.getBody().applyForceToCenter(FORCELEFT,0,true);
            }

            level.sylvan.takeDamage();

        }

        else if (other.getBody().getUserData().toString().contains("token")) {
            // collect token
            String datastring = other.getBody().getUserData().toString();
            char idxChar = datastring.charAt(5);
            int idx = Integer.parseInt(String.valueOf(idxChar));
            level.getToken(idx);

        }

    }

    @Override
    public void endContact(Contact contact) {}

    // JUNK
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

}
