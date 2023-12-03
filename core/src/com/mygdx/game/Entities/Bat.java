package com.mygdx.game.Entities;

// Flying Enemy

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

public class Bat extends Entity {

    private Animation attack;
    private Animation fly;

    private Array<TextureAtlas.AtlasRegion> attackFrames;
    private Array<TextureAtlas.AtlasRegion> flyFrames;

    // vars for "AI" movement
    private float moveTimer = 0;
    boolean left = true;

    public Bat(SylvanGame game, Vector2 initPos) {
        super(game);
        name = "Bat";
        initSprite();
        initBody();
        initialPosition = initPos;

        // have not changed these yet
        WIDTH_MULTIPLYER = 0.36f;
        HEIGHT_MULTIPLYER = 0.33f;
        left = true;
    }

    @Override
    public void aiMove(float dt) {
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
    public void initBody() {

        world = game.currentLevel.getWorld();
        System.out.println("init body");

        bodyDef = new BodyDef();
        System.out.println(getX());
        //bodyDef.position.set(5 + getWidth() / 2,5 + getHeight() / 2);
        bodyDef.position.set(5,1);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setUserData("bat"); // this may not work when there's a bunch of bats

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
    public State getState() {
        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        switch (currentState) {

            case IDLE: {
                if (vy > 0) { // if you press jump while idling
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

    @Override
    public void move(Control control) {
        //System.out.println(currentState);

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        switch (control) {
            case UP:
                if (vy <= 0) {
                    body.setLinearVelocity(vx,3f);
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
        }

    }

    @Override
    public void initSprite() {

        atlas = new TextureAtlas(Gdx.files.internal("bat/bat.atlas"));

        attackFrames = atlas.findRegions("BatAttack");
        flyFrames = atlas.findRegions("BatMovement");

        attack = new Animation<TextureRegion>(1/9f, attackFrames);
        fly = new Animation<TextureRegion>(1/9f, flyFrames);

        animations.put("attack",attack);
        animations.put("fly",fly);

        setBounds(0,0, attackFrames.get(0).getRegionWidth() / SylvanGame.PPM, attackFrames.get(0).getRegionHeight() / SylvanGame.PPM);
        setScale(0.7f);
        setRegion(attackFrames.get(0));

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
            case JUMP:
                frame = (animations.get("fly").getKeyFrame(timeElapsed, true));
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

        if (!possessed) { // if this is being called in wrong spot I can call above and do return; in aiMove()
            aiMove(dt);
        }
    }

}
