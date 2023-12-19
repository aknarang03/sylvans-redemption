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

public class Rock extends Entity {

    // sprite variables
    private Animation rockreturn;
    private Animation rockwalk;
    private Animation rockidle;
    private Animation rockrise;

    private Array<TextureAtlas.AtlasRegion> returnFrames;
    private Array<TextureAtlas.AtlasRegion> walkFrames;
    private Array<TextureAtlas.AtlasRegion> idleFrames;
    private Array<TextureAtlas.AtlasRegion> riseFrames;

    boolean playWalk;
    boolean playReturn;

    public Rock(SylvanGame game, Vector2 initPos) {
        super(game,true, 0.25f,0.25f);
        initialPosition = initPos;
        ability = "Movable platform";
        posTime = 10;
    }

    public void initBody() {

        world = game.currentLevel.getWorld();

        // set up body
        bodyDef = new BodyDef();
        bodyDef.position.set(initialPosition.x,initialPosition.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setUserData("rock");
        body.setFixedRotation(true);

        // set up fixture
        shape = new PolygonShape();
        shape.setAsBox(getWidth() / 6.5f, getHeight() / 10f);
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

        atlas = new TextureAtlas(Gdx.files.internal("rock/rock.atlas"));

        returnFrames = atlas.findRegions("rockreturn");
        walkFrames = atlas.findRegions("rockwalk");
        idleFrames = atlas.findRegions("rockidle");
        riseFrames = atlas.findRegions("rockrise");

        rockreturn = new Animation<TextureRegion>(1 / 7f, returnFrames);
        rockwalk = new Animation<TextureRegion>(1 / 4f, walkFrames);
        rockidle = new Animation<TextureRegion>(1 / 4f, idleFrames);
        rockrise = new Animation<TextureRegion>(1 / 7f, riseFrames);

        animations.put("return", rockreturn);
        animations.put("walk", rockwalk);
        animations.put("idle", rockidle);
        animations.put("rise", rockrise);

        setScale(0.5f);
        setBounds(0, 0, idleFrames.get(0).getRegionWidth() / SylvanGame.PPM, idleFrames.get(0).getRegionHeight() / SylvanGame.PPM);
        setRegion(idleFrames.get(0));

    }

    @Override
    public void move(Control control) {

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        switch (control) { // no UP control for rock
            case LEFT:
                body.setLinearVelocity(-0.8f, vy);
                left = true;
                break;
            case RIGHT:
                body.setLinearVelocity(0.8f, vy);
                left = false;
                break;
            case POSSESS:
                game.currentLevel.unpossess();
                break;
            default:
                break;
        }
    }

    @Override
    public void update(float timeElapsed, float dt) {

        detectTouch();

        TextureRegion frame;
        final State newState = getState(); // to use in stateTimer check

        // NOTE: currently not useful
        if (currentState == newState) { // state has not changed
            stateTimer += dt;
        } else {
            stateTimer = 0;
        }

        currentState = newState; // set currentState to new state

        if (possessed && currentState != State.WALK) {
            game.currentLevel.sounds.get("walk").stop();
            playWalk = true;
        }

        switch (currentState) { // NOTE: this is currently incorrect
            case FALL:
                frame = (animations.get("return").getKeyFrame(timeElapsed, false));
                break;
            case WALK:
                if (stateTimer < 0.1) {
                    frame = animations.get("rise").getKeyFrame(stateTimer,false);
                } else {
                    frame = (animations.get("walk").getKeyFrame(timeElapsed, true));
                    if (playWalk && possessed) {
                        game.currentLevel.sounds.get("walk").loop(0.6f);
                        playWalk = false;
                    }
                    playReturn = true;
                }
                break;
            case LAND:
                frame = (animations.get("idle").getKeyFrame(timeElapsed, false));
                if (stateTimer < 0.01) {
                    game.currentLevel.sounds.get("land").play(0.1f);
                }
                break;
            default:
                if (stateTimer < 0.1 && playReturn) {
                    frame = animations.get("return").getKeyFrame(stateTimer,false);
                    game.currentLevel.sounds.get("land").play(0.1f);
                    playReturn = false;
                } else {
                    frame = (animations.get("idle").getKeyFrame(timeElapsed, false));
                }
                break;
        }

        // make sprite face correct way
        if ((left && frame.isFlipX()) || (!left && !frame.isFlipX())) {
            frame.flip(true, false);
        }

        setRegion(frame);
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

            case IDLE: {
                if (Math.abs(vx) > .01f) { return State.WALK; } // if left / right pressed
                return State.IDLE; // nothing pressed
            }

            case WALK: {
                if (Math.abs(vx) <= .01f) { return State.IDLE; } // stopped walking
                else if (vy < 0) { return State.FALL; } // walked off platform
                return State.WALK; // still walking
            }

            // is it an issue that I'm checking exactly 0?
            case FALL: {
                if (vy == 0) { return State.LAND; } // no longer falling
                return State.FALL; // still falling
            }

            case LAND: {
                return State.IDLE; // state is idle after landing
            }

            default: { return State.IDLE; }

        }

    }

    @Override
    public void aiMove(float dt) {} // this will never be called on rock

}
