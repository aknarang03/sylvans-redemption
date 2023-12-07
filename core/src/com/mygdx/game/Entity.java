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

    // used to set sprite position inside box properly
    public float WIDTH_MULTIPLIER;
    public float HEIGHT_MULTIPLIER;

    // for enemies when they aren't possessed
    public abstract void aiMove(float dt);

    // states for movement / animations
    protected enum State {IDLE, WALK, JUMP, FALL, LAND, POSSESS, HIT, DEAD}; // these may change
    public State currentState;

    // vars for sprite
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
    protected float stateTimer; // this doesn't do anything yet but will be used to ensure animations play before something happens eg. possess animation before possess
    protected boolean possessed;
    protected boolean dead;

    public Entity(SylvanGame game, boolean left, float wm, float hm) { // called for each entity
        this.game = game;
        stateTimer = 0;
        currentState = State.IDLE;
        this.left = left;
        WIDTH_MULTIPLIER = wm;
        HEIGHT_MULTIPLIER = hm;
        dead = false;
    }

    public abstract void initBody(); // init the Entity body variables
    public abstract void initSprite(); // set up Entity animations
    public Body getBody() {
        return body;
    }
    public abstract void move(Control control); // differs based on entity since they can each move differently
    public abstract void update(float time, float dt); // update frame etc
    public abstract State getState(); // return state based on what entity is doing
    public void die() {
        dead = true;
        world.destroyBody(body);
    }

}