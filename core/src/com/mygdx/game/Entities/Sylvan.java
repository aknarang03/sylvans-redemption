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

/*
Anjali Narang
Aaila Arif
Jenna Esposito
 */

public class Sylvan extends Entity {

    // ANIMATION VARS
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

    // TIMERS
    public double knockbackTimer;

    // BOOLS
    boolean playGlide; // whether to play glide sound or not
    boolean playWalk; // whether to play walk sound or not
    public boolean shouldDraw; // whether to draw Sylvan or not
    public boolean flashRed = false; // whether to flash red (from damage) or not

    public int health; // keep track of HP

    public Sylvan(SylvanGame game, Vector2 initPos) {
        super(game,false, 0.36f,0.29f,"Glide");
        initialPosition = initPos;
        health = 3;
        deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sylvan_death.mp3"));
        playGlide = true;
        playWalk = true;
        shouldDraw = true;
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
        shape.setAsBox(getWidth() / 6.5f, getHeight() / 3.7f);
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
        jump = new Animation<TextureRegion>(1 / 6f, jumpFrames);
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
    public void move(Control control) { // takes in keyboard input

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        // can't move if he was recently knocked back
        if (knockbackTimer >= 0) {
            return;
        }

        // prevent from changing state with states where the timer matters
        if (currentState == State.POSSESS || currentState == State.LAND || currentState == State.DEAD) {
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
                game.currentLevel.getPossessTarget(); // try to get target if possess button pressed
                break;
            default:
                break;
        }

    }

    @Override
    public void update(float timeElapsed, float dt) {

        //System.out.println("x: " + body.getPosition().x + "y: " + body.getPosition().y); // POSITION

        TextureRegion frame;

        final State newState = getState(); // to use in stateTimer check
        if (currentState == newState) { // state has not changed
            stateTimer += dt;
        } else { // state has changed
            stateTimer = 0;
        }
        currentState = newState;

        //final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        if (possessed && currentState != State.WALK) {
            game.currentLevel.sounds.get("walk").stop();
            playWalk = true; // can now play walk sound again after stopped walking
            // this prevents overlapping the sound
        }
        if (!possessed) { // stop any possible looping sounds if not possessed
            game.currentLevel.sounds.get("walk").stop();
            game.currentLevel.sounds.get("glide").stop();
        }

        switch (currentState) {
            case DEAD:
                frame = (animations.get("glidepossess").getKeyFrame(stateTimer,true));
                break;
            case POSSESS:
                if (vy == 0) {
                    frame = (animations.get("standpossess").getKeyFrame(stateTimer, true));
                } else {
                    frame = (animations.get("glidepossess").getKeyFrame(stateTimer, true));
                }
                break;
            case JUMP:
                frame = (animations.get("jump").getKeyFrame(stateTimer, true));
                if (stateTimer < 0.01) {
                    game.currentLevel.sounds.get("jump").play(0.6f);
                }
                break;
            case FALL:
                frame = (animations.get("glide").getKeyFrame(timeElapsed, false));
                if (stateTimer >= 1 && playGlide && possessed) { // need to check possessed here because when unpossessed he is always falling outside screen
                    game.currentLevel.sounds.get("glide").play(0.65f);
                    playGlide = false; // prevent glide sound from overlapping
                }
                break;
            case WALK:
                frame = (animations.get("walk").getKeyFrame(timeElapsed, true));
                if (playWalk) {
                    game.currentLevel.sounds.get("walk").loop(0.6f);
                    playWalk = false;
                }
                break;
            case LAND:
                frame = (animations.get("land").getKeyFrame(timeElapsed, false));
                playGlide = true; // glide sound can now play again next time he glides
                game.currentLevel.sounds.get("glide").stop();
                if (stateTimer < 0.01) {
                    game.currentLevel.sounds.get("land").play(0.4f);
                }
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
    } // used in Level possess()

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

            case FALL: {
                if (vy == 0) { return State.LAND; } // no longer falling
                return State.FALL; // still falling
            }

            case LAND: {
                if (stateTimer <= 0.1) {return State.LAND; }
                return State.IDLE; // state is idle after landing
            }

            default: { return State.IDLE; }

        }

    }

    public void hitEnemy() { // called when Sylvan runs into an enemy

        flashRed = true; // allows Sylvan to flash red when rendered
        knockbackTimer = 0.5f; // reset knockback timer
        health--;
        game.currentLevel.sounds.get("hit").play(0.4f);

        // Sylvan dies if health reaches 0
        if (health <= 0) {
            die();
        }

    }

    public void getAttacked(boolean leftHit) { // called when Sylvan is attacked by Bat

        flashRed = true; // allows Sylvan to flash red when rendered

        game.currentLevel.sounds.get("hit").play(0.4f);

        final float FORCELEFT = -0.15f;
        final float FORCERIGHT = 0.15f;
        final float FORCEUP = 0.25f;

        // apply certain forces depending on whether the hit was from the left or from the right
        if (leftHit) {
            body.applyForceToCenter(FORCERIGHT,FORCEUP,true);
        } else {
            body.applyForceToCenter(FORCELEFT,FORCEUP,true);
        }

        health--;

        // Sylvan dies if health reaches 0
        if (health <= 0) {
            die();
        }

    }

    @Override
    public void aiMove(float dt) {} // this will never be called on sylvan

}
