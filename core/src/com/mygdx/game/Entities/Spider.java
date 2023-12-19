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

// Climbing Enemy

public class Spider extends Entity {

    // ANIMATION VARS
    private Animation jump;
    private Animation fall;
    private Animation walk;
    private Animation idle;
    private Array<TextureAtlas.AtlasRegion> jumpFrames;
    private Array<TextureAtlas.AtlasRegion> fallFrames;
    private Array<TextureAtlas.AtlasRegion> walkFrames;
    private Array<TextureAtlas.AtlasRegion> idleFrames;

    // TIMER VARS
    private float moveTimer = 0; // for "ai" movement
    private float attackCooldown = 0;

    private double distanceToSylvan = 0; // used for checking attack
    boolean playWalk; // used for whether walk sound should play or not

    Sound attackSound;
    Sound walkSound;

    public Spider(SylvanGame game, Vector2 initPos) {
        super(game,true,0.7f,0.33f,"Climb");
        initialPosition = initPos;
        playWalk = true;
        deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/spider_death.mp3"));
        attackSound = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy attack.mp3"));
        walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/skitter.mp3"));
        posTime = 5;
    }

    @Override
    public void initBody() {

        world = game.currentLevel.getWorld();
        System.out.println("init body");

        // set up body
        bodyDef = new BodyDef();
        bodyDef.position.set(initialPosition.x,initialPosition.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setUserData("spider");
        body.setFixedRotation(true);

        // set up fixture
        shape = new PolygonShape();
        shape.setAsBox(getWidth()/4f,getHeight()/4f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.009f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;
        body.createFixture(fixtureDef);
        shape.dispose();

    }

    @Override
    public void move(Control control) { // movement while possessed

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        switch (control) { // switch on user input

            case UP:
                if (currentState != State.FALL && currentState != State.JUMP) {
                    body.setLinearVelocity(vx, 5f);
                }
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

    public void checkAttack() { // called every frame to check whether Spider should attack

        boolean correctDir = false;
        boolean leftHit = false;

        Vector2 sylvanPos = game.currentLevel.sylvan.getBody().getPosition();
        distanceToSylvan = game.currentLevel.getDistance(sylvanPos,body.getPosition());

        if ((sylvanPos.x > body.getPosition().x && !left)) {
            // sylvan is to the right of spider and spider is facing right
            correctDir = true;
            leftHit = true;
        } else if (sylvanPos.x < body.getPosition().x && left) {
            // sylvan is to the left of spider and spider is facing left
            correctDir = true;
            leftHit = false;
        }

        // if Spider is close enough to Sylvan and the y is similar enough, Spider can attack
        if (distanceToSylvan <= 1.7 && Math.abs(sylvanPos.y-body.getPosition().y) <= 0.3 && correctDir) { // CAN ATTACK
            System.out.println("spider in attack range");
            stateTimer = 0;
            attackSound.play(1);
            if (leftHit) {
                body.applyForceToCenter(1.3f,0.08f,true);
            } else {
                body.applyForceToCenter(-1.3f,0.08f,true);
            }
            attackCooldown = 5;
        }
    }

    @Override
    public void initSprite() {

        atlas = new TextureAtlas(Gdx.files.internal("spider/spider.atlas"));

        walkFrames = atlas.findRegions("spiderwalk");
        jumpFrames = atlas.findRegions("spiderjump");
        fallFrames = atlas.findRegions("spiderfall");
        idleFrames = atlas.findRegions("spideridle");

        idle = new Animation<TextureRegion>(1/9f,idleFrames);
        walk = new Animation<TextureRegion>(1/9f, walkFrames);
        jump = new Animation<TextureRegion>(1/9f, jumpFrames);
        fall = new Animation<TextureRegion>(1/9f,fallFrames);

        animations.put("walk",walk);
        animations.put("jump",jump);
        animations.put("fall",fall);
        animations.put("idle",idle);

        setBounds(0,0, walkFrames.get(0).getRegionWidth() / SylvanGame.PPM, walkFrames.get(0).getRegionHeight() / SylvanGame.PPM);
        setScale(1.5f);
        setRegion(walkFrames.get(0));

    }

    @Override
    public void update(float timeElapsed, float dt) {

        TextureRegion frame;

        detectTouch(); // detect whether Spider has been clicked

        final State newState = getState();
        if (currentState == newState) { // state has not changed
            stateTimer = stateTimer + dt; // so increase the state timer
        } else {
            stateTimer = 0; // state has changed so reset the timer
        }
        currentState = newState;

        if (possessed && currentState != State.WALK) {
            walkSound.stop();
            playWalk = true;
        }
        if (!possessed) {
            walkSound.stop();
        }

        switch (currentState) {
            case DEAD:
                frame = (animations.get("fall").getKeyFrame(stateTimer, true));
                break;
            case WALK:
                frame = (animations.get("walk").getKeyFrame(timeElapsed, true));
                if (playWalk) {
                    walkSound.loop(1f);
                    playWalk = false;
                }
                break;
            case FALL:
                frame = (animations.get("fall").getKeyFrame(timeElapsed, false));
                break;
            case JUMP:
                frame = (animations.get("jump").getKeyFrame(timeElapsed, false));
                if (stateTimer < 0.01) { game.currentLevel.sounds.get("jump").play(0.6f); }
                break;
            case LAND:
                if (stateTimer < 0.01) { game.currentLevel.sounds.get("land").play(0.4f); }
            case IDLE:
            default:
                frame = (animations.get("idle").getKeyFrame(timeElapsed, false));
                break;
        }

        // flip frame if it's facing the wrong way
        if ((left && frame.isFlipX()) || (!left && !frame.isFlipX())) {
            frame.flip(true, false);
        }

        setRegion(frame);

        // move the sprite with "ai" if not possessed
        if (!possessed && currentState != State.ATTACK && !dead) {
            aiMove(dt);
        }

        if (attackCooldown <= 0) {
            checkAttack();
        }

        attackCooldown -= dt;

    }

    @Override
    public void aiMove(float dt) {
        final float vy = body.getLinearVelocity().y;
        moveTimer += dt;
        if (moveTimer >= 1) {
            // flip after certain amount of time
            left = !left;
            moveTimer = 0;
        }
        if (left) { body.setLinearVelocity(-0.8f,vy); } // move left
        else { body.setLinearVelocity(0.8f,vy); } // move right
    }

    @Override
    public State getState() { // nearly the same as sylvan's

        // see Sylvan class for explanation

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        if (dead) {
            return State.DEAD;
        }

        switch (currentState) {

            case DEAD: {
                return State.DEAD;
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
                if (vy == 0) { return State.LAND; }
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

}
