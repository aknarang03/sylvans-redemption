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

// Climbing Enemy

public class Spider extends Entity {

    private Animation jump;
    private Animation fall;
    private Animation walk;
    private Animation idle;

    private Array<TextureAtlas.AtlasRegion> jumpFrames;
    private Array<TextureAtlas.AtlasRegion> fallFrames;
    private Array<TextureAtlas.AtlasRegion> walkFrames;
    private Array<TextureAtlas.AtlasRegion> idleFrames;

    private float moveTimer = 0; // for "ai" movement
    boolean playWalk;

    public Spider(SylvanGame game, Vector2 initPos) {
        super(game,true,0.5f,0.33f);
        initialPosition = initPos;
        ability = "Climb";
        playWalk = true;
    }

    @Override
    public void initBody() {

        world = game.currentLevel.getWorld();
        System.out.println("init body");

        bodyDef = new BodyDef();
        bodyDef.position.set(initialPosition.x,initialPosition.y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setUserData("spider");
        body.setFixedRotation(true);

        shape = new PolygonShape();
        shape.setAsBox(getWidth()/4f,getHeight()/4f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.009f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.01f;

        body.createFixture(fixtureDef);

        shape.dispose();

    }

    @Override
    public void move(Control control) {

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
        setScale(1f);
        setRegion(walkFrames.get(0));

    }

    @Override
    public void update(float timeElapsed, float dt) {

        TextureRegion frame;

        final State newState = getState();
        if (currentState == newState) { // state has not changed
            stateTimer = stateTimer + dt;
        } else {
            stateTimer = 0;
        }
        currentState = newState;

        if (currentState != State.WALK || !possessed) {
            game.currentLevel.sounds.get("skitter").stop();
            playWalk = true;
        }

        switch (currentState) {
            case WALK:
                frame = (animations.get("walk").getKeyFrame(timeElapsed, true));
                if (playWalk && possessed) {
                    game.currentLevel.sounds.get("skitter").loop(0.6f);
                    playWalk = false;
                }
                break;
            case FALL:
                frame = (animations.get("fall").getKeyFrame(timeElapsed, false));
                break;
            case JUMP:
                frame = (animations.get("jump").getKeyFrame(timeElapsed, false));
                if (stateTimer < 0.01) { game.currentLevel.sounds.get("jump").play(0.5f); }
                break;
            case LAND:
                if (stateTimer < 0.01) { game.currentLevel.sounds.get("land").play(0.1f); }
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
        if (!possessed) {
            aiMove(dt);
        }

    }

    @Override
    public void aiMove(float dt) {
        moveTimer += dt;
        if (moveTimer >= 1) {
            left = !left;
            moveTimer = 0;
        }
        if (left) { body.setLinearVelocity(-1.5f,-3); } // move left
        else { body.setLinearVelocity(1.5f,-3); } // move right
    }

    @Override
    public State getState() { // nearly the same as sylvan's

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        if (dead) {
            return State.DEAD;
        }

        if (!possessed) { return State.WALK; } // with "ai" movement it will always be walking

        switch (currentState) {

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
                else if (vy > 0) { return State.JUMP; } // allows climb (along with some code in move)
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
