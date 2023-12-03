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

    // for enemies when they aren't possessed
    public abstract void aiMove(float dt);

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
    public abstract void update(float time, float dt);
    // will be used to change the Entity that is moved by the player later on

    public void setPossessed(boolean possessed) {
        this.possessed = possessed;
    }


    // THIS IS NOT GETTING THE CORRECT STATE
    public abstract State getState();

    public float getStateTimer() {
        return stateTimer;
    }

    //public abstract boolean shouldFlip();

}
