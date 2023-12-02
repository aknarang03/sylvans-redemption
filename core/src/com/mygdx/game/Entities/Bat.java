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

    public Bat(SylvanGame game, Vector2 initPos) {
        super(game);
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
        setPosition(1/SylvanGame.PPM, 1/SylvanGame.PPM); // for testing try hardcoding if it doesn't work
        System.out.println(getX());
        bodyDef.position.set(5 + getWidth() / 2,5 + getHeight() / 2);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        body.setFixedRotation(true);
        shape = new PolygonShape();
        shape.setAsBox(getWidth()/2,getHeight()/2);
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
    public void move(Control control) {
        currentState = getState();
        //System.out.println(currentState);

        float vy = body.getLinearVelocity().y;
        float vx = body.getLinearVelocity().x;

        //if (vy < 0) {
            switch (control) {
                case UP:
                    if (vy <= 0) {
                        body.applyForceToCenter(0f, 0.5f, true);
                        currentState = State.JUMP;
                    }
                    break;
                case LEFT:
                    body.setLinearVelocity(-1f, vy);
                    break;
                case RIGHT:
                    body.setLinearVelocity(1f, vy);
                    break;
            }
       // }
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
    public void updateFrame(float timeElapsed, float dt) {

        TextureRegion frame;
        previousState = currentState;
        currentState = getState();

        switch (currentState) {

            case JUMP:
                frame = (animations.get("fly").getKeyFrame(timeElapsed, true));
                break;
            default:
                frame = (animations.get("fly").getKeyFrame(timeElapsed, true));
                break;

        }

        // flip frame if it's facing the wrong way
        // doesn't work
        if ((body.getLinearVelocity().x < 0 && frame.isFlipX()) || (body.getLinearVelocity().x > 0 && !frame.isFlipX())) {
            frame.flip(true, false);
        }

        if (currentState == previousState) { // state has not changed
            stateTimer = stateTimer + dt;
        } else {
            stateTimer = 0;
        }

        previousState = currentState;

        setRegion(frame);
    }

}
