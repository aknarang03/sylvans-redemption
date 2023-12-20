package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

/*
Anjali Narang
Aaila Arif
Jenna Esposito
 */

public abstract class Entity extends Sprite implements Disposable {

    // SETUP VARS
    protected SylvanGame game; // need game reference to get world to draw body, etc
    protected World world;

    // SPRITE VARS
    protected HashMap<String, Animation<TextureRegion>> animations = new HashMap(); // stores animations
    protected TextureAtlas atlas;
    public Sprite possessIndicator; // every Entity gets this and it's drawn if they are able to be possessed

    // used to set sprite position inside box properly:
    public float WIDTH_MULTIPLIER;
    public float HEIGHT_MULTIPLIER;

    // STATE VARS
    protected enum State {IDLE, WALK, JUMP, FALL, LAND, POSSESS, ATTACK, DEAD}; // states for movement / animations
    public State currentState;

    // BODY VARS
    protected BodyDef bodyDef;
    protected Body body;
    protected PolygonShape shape;
    protected FixtureDef fixtureDef;


    // BOOLS
    protected boolean left; // direction Entity is facing
    protected boolean possessed; // whether Entity is possessed or not
    protected boolean dead; // whether Entity is dead or not

    // TIMERS
    protected float stateTimer; // how long Entity has been in current state
    public float posTime; // how long Entity can be possessed for

    // OTHER
    public Sound deathSound;
    public Sound walkSound;
    public Sound attackSound;
    public String ability; // ability string to show in HUD
    protected Vector2 initialPosition; // where the Entity will spawn when level is started

    public Entity(SylvanGame game, boolean left, float wm, float hm, String ability) {

        this.game = game;
        this.left = left;
        WIDTH_MULTIPLIER = wm;
        HEIGHT_MULTIPLIER = hm;
        this.ability = ability;

        stateTimer = 0;
        currentState = State.IDLE;
        dead = false;
        createIndicator();

    }

    public void createIndicator() { // create the possess indicator sprite
        possessIndicator = new Sprite();
        Texture possessIndicatorImg = new Texture(Gdx.files.internal("possess_indicator.png"));
        possessIndicator.setRegion(possessIndicatorImg);
    }

    public void detectTouch() { // detect whether Entity is clicked
        if (Gdx.input.isTouched()) { // detect any touch
            Vector2 touch = game.currentLevel.viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            if (getBoundingRectangle().contains(touch)) { // if the touch is inside an Entity's bounding rectangle
                game.currentLevel.getTargetFromClick(this); // get possess target
            }
        }
    }

    public void die() { // kill Entity
        dead = true;
        currentState = State.DEAD;
        stateTimer = 0;
        deathSound.play(1);
    }

    // implementations of the below vary based on the Entity

    public abstract void initBody(); // init the Entity body variables
    public abstract void initSprite(); // set up Entity animations

    public abstract void update(float time, float dt); // update frame etc

    public abstract void aiMove(float dt); // for Entities to move when they aren't possessed
    public abstract void move(Control control); // for Entities to move when they are possessed

    // GETTERS
    public Body getBody() { return body; }
    public abstract State getState(); // return state based on what entity is doing

    @Override
    public void dispose() {
        if (!world.isLocked()) {
            if (walkSound != null) {walkSound.dispose();};
            if (attackSound != null) {attackSound.dispose();}
            if (deathSound != null) {deathSound.dispose();}
        }
    }

}