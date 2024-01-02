package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Control;
import com.mygdx.game.Entity;
import com.mygdx.game.SylvanGame;

/*
Anjali Narang
Aaila Arif
Jenna Esposito
 */

// Flying Enemy

public class Bat extends Entity {

    // ANIMATION VARS
    private Animation attack;
    private Animation fly;
    private Animation die;
    private Array<TextureAtlas.AtlasRegion> attackFrames;
    private Array<TextureAtlas.AtlasRegion> flyFrames;
    private Array<TextureAtlas.AtlasRegion> dieFrames;

    // TIMERS
    private float moveTimer = 0; // for "ai" move
    private float flapTimer = 0; // for playing flap sound
    private float attackCooldown = 0; // to ensure attacks don't happen back to back

    private double distanceToSylvan = 0; // for attack checks
    float gravscale; // gravity scale to switch to when possessed
    Sound attackSound; // sound to play while attacking

    public Bat(SylvanGame game, Vector2 initPos) {
        super(game,true,0.44f,0.44f,"Fly");
        initialPosition = initPos;
        deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bat_death.mp3"));
        attackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bat_attack.mp3"));
        posTime = 10;
    }

    @Override
    public void aiMove(float dt) { // used to move Bat while not possessed

        // swap which way it's moving after move timer goes off
        moveTimer += dt;
        if (moveTimer >= 3) {
            left = !left;
            moveTimer = 0;
        }

        if (left) {
            body.setLinearVelocity(-1,0); // move left
        } else {
            body.setLinearVelocity(1,0); // move right
        }

    }

    @Override
    public void initBody() { // set up the Bat body

        world = game.currentLevel.getWorld();

        // set up body
        bodyDef = new BodyDef();
        bodyDef.position.set(initialPosition.x,initialPosition.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setUserData("bat");
        body.setFixedRotation(true);

        // set up fixture
        shape = new PolygonShape();
        shape.setAsBox(getWidth()/2.5f,getHeight()/2.5f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.009f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f; // to prevent sticking to platforms

        body.createFixture(fixtureDef);

        shape.dispose();

        gravscale = body.getGravityScale(); // save the default gravity scale

    }

    @Override
    public State getState() { // state machine

        // see Sylvan class for explanation

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        if (dead) {
            return State.DEAD;
        }

        switch (currentState) {

            case ATTACK: {
                if (stateTimer >= 1) {
                    return State.IDLE;
                }
                return State.ATTACK;
            }

            case IDLE: {
                if (vy > 0) { return State.JUMP; }
                else if (Math.abs(vx) > .01f) { return State.WALK; }
                return State.IDLE;
            }

            case WALK: {
                if (vy > 0) { return State.JUMP; }
                else if (Math.abs(vx) <= .01f) { return State.IDLE; }
                else if (vy < 0) { return State.FALL; }
                return State.WALK;
            }

            case JUMP: {
                if (vy <= 0) { return State.FALL; }
                return State.JUMP;
            }

            case FALL: {
                if (vy == 0) { return State.LAND;
                } else if (vy > 0) { return State.JUMP; } // allows jump in midair (flight)
                return State.FALL;
            }

            case LAND: {
                return State.IDLE;
            }

            default: {
                return State.IDLE;
            }

        }

    }

    @Override
    public void move(Control control) { // used to move while Bat is possessed

        // get current velocity to use below so that Bat can move diagonally
        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        switch (control) {
            case UP:
                //if (vy <= 0) { body.setLinearVelocity(vx,3f); } // jump flight option
                body.setLinearVelocity(vx,1f); // smoother flight option
                break;
            case LEFT:
                body.setLinearVelocity(-1f, vy);
                left = true;
                break;
            case RIGHT:
                body.setLinearVelocity(1f, vy);
                left = false;
                break;
            case POSSESS:
                game.currentLevel.unpossess();
                break;
        }

    }

    @Override
    public void initSprite() { // set up the Bat animations and sprite

        atlas = new TextureAtlas(Gdx.files.internal("bat/bat.atlas"));

        attackFrames = atlas.findRegions("BatAttack");
        flyFrames = atlas.findRegions("BatMovement");
        dieFrames = atlas.findRegions("BatDeath");

        attack = new Animation<TextureRegion>(1/9f, attackFrames);
        fly = new Animation<TextureRegion>(1/9f, flyFrames);
        die = new Animation<TextureRegion>(1/9f,dieFrames);

        animations.put("attack",attack);
        animations.put("fly",fly);
        animations.put("die",die);

        setBounds(0,0, attackFrames.get(0).getRegionWidth() / SylvanGame.PPM, attackFrames.get(0).getRegionHeight() / SylvanGame.PPM);
        setScale(1f);
        setRegion(attackFrames.get(0));

    }

    public void checkAttack() { // called every frame to check if Bat should attack Sylvan

        boolean correctDir = false;
        boolean leftHit = false;

        Vector2 sylvanPos = game.currentLevel.sylvan.getBody().getPosition();
        distanceToSylvan = game.currentLevel.getDistance(sylvanPos,body.getPosition());

        // determine how attack should happen
        if ((sylvanPos.x > body.getPosition().x && !left)) {
            // sylvan is to the right of bat and bat is facing right
            correctDir = true;
            leftHit = true;
        } else if (sylvanPos.x < body.getPosition().x && left) {
            // sylvan is to the left of bat and bat is facing left
            correctDir = true;
            leftHit = false;
        }

        // if Bat is close enough to Sylvan and the y is similar enough, Bat can attack
        if (distanceToSylvan <= 1.8 && Math.abs(sylvanPos.y-body.getPosition().y) <= 0.3 && correctDir) {
            currentState = State.ATTACK;
            stateTimer = 0;
            attackSound.play(1);
            game.currentLevel.sylvan.getAttacked(leftHit);
            attackCooldown = 5;
        }

    }

    @Override
    public void update(float timeElapsed, float dt) {

        TextureRegion frame;
        detectTouch(); // see if Bat was clicked
        flapTimer+=dt; // increase timer for whether to play flap sound

        // set the current state
        final State newState = getState();
        if (currentState == newState) { // state has not changed
            stateTimer = stateTimer + dt;
        } else {
            stateTimer = 0;
        }
        currentState = newState;

        // check attack if it makes sense to attack
        if (!possessed && attackCooldown <= 0) {
            checkAttack();
        }

        switch (currentState) { // update animation / sounds

            case DEAD:
                frame = (animations.get("die").getKeyFrame(stateTimer,true));
                break;

            case ATTACK:
                frame = (animations.get("attack").getKeyFrame(stateTimer,true));
                break;

            case JUMP:
                frame = (animations.get("fly").getKeyFrame(timeElapsed, true));
                if (flapTimer >= 0.8) { // play flap sound if enough time has passed
                    game.currentLevel.sounds.get("flap").play(0.5f);
                    flapTimer = 0;
                }
                break;

            default:
                frame = (animations.get("fly").getKeyFrame(timeElapsed, true));
                break;
        }

        // flip frame if it's facing the wrong way
        if ((left && frame.isFlipX()) || (!left && !frame.isFlipX())) {
            frame.flip(true, false);
        }

        setRegion(frame);

        if (!possessed) {
            body.setGravityScale(0f); // Bat should stay in the air if not possessed
            if (currentState != State.ATTACK && !dead) {
                aiMove(dt); // move around automatically if not possessed, not attacking, and not dead
            }
        } else {
            body.setGravityScale(gravscale); // change Bat's gravity back to normal so it can fly around
        }

        attackCooldown -= dt;

    }

}
