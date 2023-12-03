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

public class Spider extends Entity {

    private Animation jump;
    private Animation fall;
    private Animation walk;
    private Animation idle;

    private Array<TextureAtlas.AtlasRegion> jumpFrames;
    private Array<TextureAtlas.AtlasRegion> fallFrames;
    private Array<TextureAtlas.AtlasRegion> walkFrames;
    private Array<TextureAtlas.AtlasRegion> idleFrames;

    // vars for "AI" movement
    private float moveTimer = 0;
    boolean left = true;
    boolean wall = false;

    @Override
    public void aiMove(float dt) { // MODIFY SO IT STAYS ON PLATFORM
        moveTimer += dt;
        if (moveTimer >= 1) {
            left = !left;
            moveTimer = 0;
        }
        if (left) {
            body.setLinearVelocity(-1.5f,-3); // move left
        } else {
            body.setLinearVelocity(1.5f,-3); // move right
        }
    }

    public Spider(SylvanGame game, Vector2 initPos) {
        super(game);
        name = "Spider";
        initSprite();
        initBody();
        initialPosition = initPos;

        // have not changed these yet
        WIDTH_MULTIPLYER = 0.36f;
        HEIGHT_MULTIPLYER = 0.33f;
    }

    @Override
    public void initBody() {

        world = game.currentLevel.getWorld();
        System.out.println("init body");

        bodyDef = new BodyDef();
        bodyDef.position.set(2.4f,2.5f);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setUserData("spider"); // this may not work when there's a bunch of spiders

        body.setFixedRotation(true);
        shape = new PolygonShape();
        shape.setAsBox(getWidth()/2.5f,getHeight()/2.5f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.009f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f; // to prevent sticking to platforms

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void setPossessed(boolean possessed) {
        this.possessed = possessed;
    }

    @Override
    public void move(Control control) { // MODIFY TO ALLOW CLIMBING

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        switch (control) {
            case UP:
                if (currentState != State.FALL && currentState != State.JUMP && Math.abs(vy) < .01f) {
                    //body.applyForceToCenter(0f, 1f, true);
                    body.setLinearVelocity(vx, 5f);
                    //currentState = State.JUMP;
                }
                break;
            case LEFT:
                body.setLinearVelocity(-1f, vy);
                break;
            case RIGHT:
                body.setLinearVelocity(1f, vy);
                break;
            case POSSESS:
                game.currentLevel.possess();
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

        switch (currentState) {
            case WALK:
                frame = (animations.get("walk").getKeyFrame(timeElapsed, true));
                break;
            case FALL:
                frame = (animations.get("fall").getKeyFrame(timeElapsed, false));
                break;
            case JUMP:
                frame = (animations.get("jump").getKeyFrame(timeElapsed, false));
                break;
            case IDLE:
            default:
                frame = (animations.get("idle").getKeyFrame(timeElapsed, false));
                break;
        }

        // flip frame if it's facing the wrong way
        if ((body.getLinearVelocity().x < 0 && frame.isFlipX()) || (body.getLinearVelocity().x > 0 && !frame.isFlipX())) {
            frame.flip(true, false);
        }

        setRegion(frame);

        if (!possessed) { // if this is being called in wrong spot I can call above and do return; in aiMove()
            aiMove(dt);
        }

    }

    @Override
    public State getState() {
        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        switch (currentState) {

            case IDLE: {
                if (vy > 0) {
                    return State.JUMP;
                } else if (Math.abs(vx) > .01f) {
                    return State.WALK;
                }
                return State.IDLE;
            }

            case WALK: {
                if (vy > 0) {
                    return State.JUMP;
                } else if (Math.abs(vx) <= .01f) {
                    return State.IDLE;
                }
                return State.WALK;
            }

            case JUMP: {
                if (vy <= 0) {
                    return State.FALL;
                }
                return State.JUMP;
            }

            case FALL: {
                if (vy == 0) {
                    return State.LAND;
                } else if (vy > 0) {
                    return State.JUMP; // since bat can jump in midair
                }
                return State.FALL;
            }

            case LAND: {
                return State.IDLE;
            }

            default:
                return State.IDLE;
        }
    }

    /*
    @Override
    public boolean shouldFlip() {
        return false;
    }
     */
}
