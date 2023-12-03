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

            // get the current velocity and set up force to be the opposite
            double velx = sylvan.getBody().getLinearVelocity().x;
            //float forcex = (float)-velx;
            double vely = sylvan.getBody().getLinearVelocity().y;
            //float forcey = (float)-vely;

            boolean left = (velx < 0);
            boolean right = (velx > 0);
            boolean up = (vely > 0);
            boolean down = (vely < 0);

            // apply opposite force
            //sylvan.getBody().applyForceToCenter(forcex,forcey,true); // works but is too much

            final float FORCE = 1f;
            final float NEG_FORCE = -1;

            if (left) {
                sylvan.getBody().applyForceToCenter(FORCE,0,true);
            } if (right) {
                sylvan.getBody().applyForceToCenter(NEG_FORCE,0,true);
            } if (up) {
                sylvan.getBody().applyForceToCenter(0,NEG_FORCE,true);
            } if (down) {
                sylvan.getBody().applyForceToCenter(0,FORCE,true);
            }


            //System.out.println("force x: " + forcex + ", force y: " + forcey);

            // WHERE I LEFT OFF: Print the force and see why it wont subtract properly

            //System.out.println("velocity: " + velx);

            /*
            if (velx != 0 && vely !=0) { // sylvan is moving left / right and was jumping / falling
                sylvan.getBody().applyForceToCenter(-0.05f,-0.05f,true);
            } else if (velx != 0) {
                sylvan.getBody().applyForceToCenter(-0.05f,0f,true);
            }
             */




            //sylvan.getBody().applyForceToCenter(0.5f,0.5f,true); // fix this later it's janky
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
