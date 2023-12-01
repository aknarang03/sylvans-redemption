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

    protected enum State {IDLE, WALK, JUMP, GLIDE, FALL, LAND, POSSESS, DEAD}; // these may change
    public State currentState;
    public State previousState;

    // vars for sprite (there will be more in each entity)
    protected HashMap<String, Animation<TextureRegion>> animations = new HashMap();
    protected TextureAtlas atlas;

    // vars for body
    SylvanGame game; // need game reference to get world to draw body, etc
    protected Vector2 initialPosition; // where the Entity will spawn when level is started
    protected BodyDef bodyDef;
    protected Body body;
    PolygonShape shape;
    FixtureDef fixtureDef;
    protected World world; // reference to the world
    protected boolean left;
    protected float stateTimer;
    protected boolean possessed;


    // this constructor should be called in every entity constructor to init game and world. need this to make initBody() work
    public Entity(SylvanGame game) {
        this.game = game;
        stateTimer = 0;
        currentState = State.IDLE;
        left = false;
    }


    // initialize the Entity body variables
    public void initBody() {

        world = game.currentLevel.getWorld();
        System.out.println("init body");

        bodyDef = new BodyDef();
        setPosition(5/SylvanGame.PPM, 5/SylvanGame.PPM);
        System.out.println(getX());
        bodyDef.position.set(5 + getWidth() / 2,5 + getHeight() / 2);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        body.setFixedRotation(true);
        shape = new PolygonShape();
        //shape.setAsBox(0.3f,0.3f); // temp values
        shape.setAsBox(getWidth()/3.8f,getHeight()/3.1f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.009f;
        fixtureDef.friction = 0.5f;
        //fixtureDef.friction = 0;
        fixtureDef.restitution = 0.1f; // to prevent sticking to platforms

        body.createFixture(fixtureDef);
        //body.setUserData(this);
        shape.dispose();
        //System.out.println(body.getPosition());
    }

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

        if (body.getLinearVelocity().y != 0 && (currentState == State.JUMP || previousState == State.JUMP))
            return State.JUMP; // do I need those checks for jump state??
        else if (body.getLinearVelocity().y == 0 && previousState == State.FALL)
            return State.LAND;
        else if (body.getLinearVelocity().y < 0)
            return State.FALL;
        else if (body.getLinearVelocity().x != 0)
            return State.WALK;
        else
            System.out.println("idle");
            return State.IDLE;

    }

    public float getStateTimer() {
        return stateTimer;
    }

}
