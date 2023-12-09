package com.mygdx.game.Entities;
import com.badlogic.gdx.Gdx;
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

public class Sylvan extends Entity {

    // sprite variables
    private Animation idle;
    private Animation walk;
    private Animation jump;
    private Animation glide;
    private Animation land;
    private Animation glidepossess;
    private Animation standpossess;
    private Array<TextureAtlas.AtlasRegion> idleFrames;
    private Array<TextureAtlas.AtlasRegion> walkFrames;
    private Array<TextureAtlas.AtlasRegion> jumpFrames;
    private Array<TextureAtlas.AtlasRegion> glideFrames;
    private Array<TextureAtlas.AtlasRegion> landFrames;
    private Array<TextureAtlas.AtlasRegion> glidepossessFrames;
    private Array<TextureAtlas.AtlasRegion> standpossessFrames;

    public int health;

    // timers
    public double knockbackTimer;

    public Sylvan(SylvanGame game, Vector2 initPos) {
        super(game,false, 0.36f,0.33f);
        initialPosition = initPos;
        health = 3;
    }

    public void initBody() {

        world = game.currentLevel.getWorld();

        // set up body
        bodyDef = new BodyDef();
        bodyDef.position.set(initialPosition.x,initialPosition.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setUserData("sylvan");
        body.setFixedRotation(true);

        // set up fixture
        shape = new PolygonShape();
        shape.setAsBox(getWidth() / 3.8f, getHeight() / 3.1f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.009f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f; // to prevent sticking to platforms

        body.createFixture(fixtureDef);

        shape.dispose();

    }

    @Override
    public void initSprite() {

        atlas = new TextureAtlas(Gdx.files.internal("sylvan/sylvan.atlas"));

        idleFrames = atlas.findRegions("idle");
        walkFrames = atlas.findRegions("walk");
        jumpFrames = atlas.findRegions("jump");
        glideFrames = atlas.findRegions("glide");
        landFrames = atlas.findRegions("land");
        glidepossessFrames = atlas.findRegions("glidepossess");
        standpossessFrames = atlas.findRegions("standpossess");

        idle = new Animation<TextureRegion>(1 / 9f, idleFrames);
        walk = new Animation<TextureRegion>(1 / 9f, walkFrames);
        jump = new Animation<TextureRegion>(1 / 5f, jumpFrames);
        glide = new Animation<TextureRegion>(1 / 9f, glideFrames);
        land = new Animation<TextureRegion>(1 / 9f, landFrames);
        glidepossess = new Animation<TextureRegion>(1 / 4f, glidepossessFrames);
        standpossess = new Animation<TextureRegion>(1 / 4f, standpossessFrames);

        animations.put("idle", idle);
        animations.put("walk", walk);
        animations.put("jump", jump);
        animations.put("glide", glide);
        animations.put("land", land);
        animations.put("glidepossess", glidepossess);
        animations.put("standpossess", standpossess);

        setBounds(0, 0, idleFrames.get(0).getRegionWidth() / SylvanGame.PPM, idleFrames.get(0).getRegionHeight() / SylvanGame.PPM);
        setScale(0.7f);
        setRegion(idleFrames.get(0));

    }

    @Override
    public void move(Control control) {

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        if (knockbackTimer >= 0) {
            return;
        }
        if (currentState == State.POSSESS || currentState == State.LAND || currentState == State.DEAD) { // prevent from changing state with states where the timer matters
            return;
        }

        switch (control) {
            case UP: {
                if (currentState == State.FALL) { // your vertical velocity is not close to 0 (ie jumping or falling)
                    body.setLinearVelocity(vx, 0.1f * vy); // glide
                } else if (currentState != State.JUMP) { // your vertical velocity is close to 0
                    body.setLinearVelocity(vx, 5f); // jump
                }
                break;
            }
            case LEFT:
                body.setLinearVelocity(-1f, vy);
                left = true;
                break;
            case RIGHT:
                body.setLinearVelocity(1f, vy);
                left = false;
                break;
            case POSSESS:
                game.currentLevel.getPossessTarget();
                break;
            default:
                break;
        }
    }

    @Override
    public void update(float timeElapsed, float dt) {

        TextureRegion frame;

        final State newState = getState(); // to use in stateTimer check
        final boolean playStand; // to decide whether to play glide or stand possess anim

        /*
        if (currentState == State.WALK || currentState == State.LAND) {
            playStand = true;
        } else {
            playStand = false;
        }
         */

        if (currentState == newState) { // state has not changed
            stateTimer += dt;
        } else {
            stateTimer = 0;
        }

        currentState = newState; // set currentState to new state

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        switch (currentState) {
            case POSSESS:
                if (vy == 0) {
                    frame = (animations.get("standpossess").getKeyFrame(stateTimer, true)); // NOTE: figure out whether to play glide or stand
                } else {
                    frame = (animations.get("glidepossess").getKeyFrame(stateTimer, true));
                }
                break;
            case JUMP:
                frame = (animations.get("jump").getKeyFrame(stateTimer, true));
                break;
            case FALL:
                frame = (animations.get("glide").getKeyFrame(timeElapsed, false));
                break;
            case WALK:
                frame = (animations.get("walk").getKeyFrame(timeElapsed, true));
                break;
            case LAND:
                frame = (animations.get("land").getKeyFrame(timeElapsed, false));
                break;
            default:
                frame = (animations.get("idle").getKeyFrame(timeElapsed, false));
                break;
        }

        // make sprite face correct way
        if ((left && !frame.isFlipX()) || (!left && frame.isFlipX())) {
            frame.flip(true, false);
        }

        setRegion(frame);
    }



    public void resetState() {
        currentState = State.IDLE;
    }

    @Override
    public State getState() {

        // to be used in checks
        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        if (dead) {
            return State.DEAD;
        }

        switch (currentState) {

            case POSSESS: {
                return State.POSSESS;
            }

            case IDLE: {
                if (vy > 0) { return State.JUMP; } // jump pressed
                else if (Math.abs(vx) > .01f) { return State.WALK; } // if left / right pressed
                return State.IDLE; // nothing pressed
            }

            case WALK: {
                if (vy > 0) { return State.JUMP; } // jump pressed
                else if (Math.abs(vx) <= .01f) { return State.IDLE; } // stopped walking
                else if (vy < 0) { return State.FALL; } // walked off platform
                return State.WALK; // still walking
            }

            case JUMP: {
                if (vy <= 0) { return State.FALL; } // jump reached max point
                return State.JUMP; // jump has not reached max point
            }

            // is it an issue that I'm checking exactly 0?
            case FALL: {
                if (vy == 0) { return State.LAND; } // no longer falling
                return State.FALL; // still falling
            }

            case LAND: {
                if (stateTimer <= 0.1) {return State.LAND; }
                return State.IDLE; // state is idle after landing
            }
            // NOTE: later, check state timer in here and wait to return idle so that animation can play

            default: { return State.IDLE; }

        }

    }

    public void takeDamage() {
        knockbackTimer = 0.5f;
        health--;
        System.out.println("health:" + health);

        //setColor(Color.RED);

        if (health == 0) {
            die();
        }

    }

    @Override
    public void die() { // this will be different from the enemies I think
        dead = true;
        //world.destroyBody(body); // causes issues with collision listener
    }

    @Override
    public void aiMove(float dt) {} // this will never be called on sylvan

}
