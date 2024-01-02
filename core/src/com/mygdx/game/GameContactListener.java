package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/*
Anjali Narang
Aaila Arif
Jenna Esposito
 */

public class GameContactListener implements ContactListener {

    Level level; // need level to be passed in thru constructor so that it can be acted on from here

    GameContactListener(Level level) { // or take Game game?
        this.level = level;
    }

    @Override
    public void beginContact(Contact contact) {

        // figure out which fixture is player and which one is not
        Fixture other = (contact.getFixtureA().getBody().getUserData() == level.currentInhabitedEntity.getBody().getUserData()) ? contact.getFixtureB() : contact.getFixtureA();
        Fixture player = (contact.getFixtureA().getBody().getUserData() == level.currentInhabitedEntity.getBody().getUserData()) ? contact.getFixtureA() : contact.getFixtureB();

        // KNOCKBACK (for when Sylvan runs into an enemy)

        if (player.getBody().getUserData() == "sylvan" && (other.getBody().getUserData() == "bat" || other.getBody().getUserData() == "spider")) {
            // if player is Sylvan (so he is not possessing anyone) and the other fixture is a bat or spider

            double velx = player.getBody().getLinearVelocity().x;
            double vely = player.getBody().getLinearVelocity().y;

            // get direction(s) that Sylvan was going when he ran into enemy
            boolean left = (velx < 0);
            boolean right = (velx > 0);
            boolean up = (vely > 0);
            boolean down = (vely < 0);

            // forces to apply
            final float FORCEUP = 0.5f;
            final float FORCEDOWN = -0.5f;
            final float FORCERIGHT = 0.5f;
            final float FORCELEFT = -0.5f;

            // apply the opposite force of whatever direction Sylvan was going in
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

            // Sylvan only takes damage from knockback if both cooldowns are low enough
            if (level.sylvan.knockbackTimer <= 0 && level.possessCooldown <= 0) {
                level.sylvan.hitEnemy();
            }

        }

        // TOKEN COLLECT

        else if (other.getBody().getUserData().toString().contains("token")) {
            // player can be anyone (so Sylvan can be possessing something) and other fixture has to be the token
            // token's user data for body contains "token" and a number, so parse out the number to see which one was collected
            String datastring = other.getBody().getUserData().toString();
            char idxChar = datastring.charAt(5); // 5th character is the token number
            int idx = Integer.parseInt(String.valueOf(idxChar)); // turn char of token number into an int
            level.getToken(idx); // use this int to collect the correct token
        }

        // SYLVAN DEATH IF HE FALLS IN WATER

        else if (player.getBody().getUserData() == "sylvan" && other.getBody().getUserData() == "damage") {
            level.sylvan.die();
        }

    }

    @Override
    public void endContact(Contact contact) {}
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

}
