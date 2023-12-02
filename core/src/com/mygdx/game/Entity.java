package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.HashMap;

public abstract class Entity extends Sprite {

    // used to set position
    public float WIDTH_MULTIPLYER;
    public float HEIGHT_MULTIPLYER;

    protected enum State {IDLE, WALK, JUMP, GLIDE, FALL, LAND, POSSESS, DEAD}; // these may change
    public State currentState;

    // vars for sprite (there will be more in each entity)
    protected HashMap<String, Animation<TextureRegion>> animations = new HashMap();
    protected TextureAtlas atlas;

    // vars for body
    protected SylvanGame game; // need game reference to get world to draw body, etc
    protected Vector2 initialPosition; // where the Entity will spawn when level is started
    protected BodyDef bodyDef;
    protected Body body;
    protected PolygonShape shape;
    protected FixtureDef fixtureDef;
    protected World world; // reference to the world
    protected boolean left;
    protected float stateTimer;
    protected boolean possessed;
    protected String name;


    // this constructor should be called in every entity constructor to init game and world. need this to make initBody() work
    public Entity(SylvanGame game) {
        this.game = game;
        stateTimer = 0;
        currentState = State.IDLE;
        left = false;
    }


    // initialize the Entity body variables
    public abstract void initBody();
    public Body getBody() {
        return body;
    }

    // implementation of these will differ based on the entity since each of their movements will differ
    public abstract void move(Control control);
    public abstract void initSprite();
    public abstract void updateFrame(float time, float dt);
    // will be used to change the Entity that is moved by the player later on

    public void setPossessed(boolean possessed) {
        this.possessed = possessed;
    }


    // THIS IS NOT GETTING THE CORRECT STATE
    public State getState() {

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        switch (currentState) {

            case IDLE: {
                if (vy > 0) { // if you press jump while idling
                    return State.JUMP;
                } else if (Math.abs(vx) > .01f) {
                    return State.WALK;
                }
                return State.IDLE;
            }

            case WALK: {
                if (vy > 0) {
                    return State.JUMP;
                } else if (Math.abs(vx) <= .01f) {
                    return State.IDLE;
                }
                return State.WALK;
            }

            case JUMP: {
                if (vy <= 0) {
                    return State.FALL;
                }
                return State.JUMP;
            }

            case FALL: {
                if (vy == 0) {
                    return State.LAND;
                }
                return State.FALL;
            }

            case LAND: {
                return State.IDLE;
            }

            default:
                return State.IDLE;

        }

        /*
        if (body.getLinearVelocity().y > 0) //  && (currentState == State.JUMP || previousState == State.JUMP)
            return State.JUMP; // do I need those checks for jump state??
        else if (body.getLinearVelocity().y == 0 && previousState == State.FALL)
            return State.LAND;
        else if (body.getLinearVelocity().y < 0)
            return State.FALL;
        else if (body.getLinearVelocity().x != 0)
            return State.WALK;
        else
            //System.out.println("idle");
            return State.IDLE;
    */
    }

    public float getStateTimer() {
        return stateTimer;
    }

}
