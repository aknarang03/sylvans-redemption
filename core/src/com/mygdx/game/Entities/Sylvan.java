package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    public Sylvan(SylvanGame game, Vector2 initPos) {
        super(game);
        name = "Sylvan";
        initialPosition = initPos;
        initSprite();
        initBody();
        //System.out.println("width:" + this.getWidth());
        WIDTH_MULTIPLYER = 0.36f;
        HEIGHT_MULTIPLYER = 0.33f;
        //currentState = State.IDLE;
    }

    public void initBody() {

        world = game.currentLevel.getWorld();
        System.out.println("init body");

        bodyDef = new BodyDef();
        setPosition(5/SylvanGame.PPM, 5/SylvanGame.PPM);
        System.out.println(getX());
        bodyDef.position.set(5 + getWidth() / 2,5 + getHeight() / 2); // when I remove the 5+ he falls
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        body.setFixedRotation(true);
        shape = new PolygonShape();
        //shape.setAsBox(0.3f,0.3f); // temp values
        shape.setAsBox(getWidth()/3.8f,getHeight()/3.1f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.009f;
        fixtureDef.friction = 0.5f;
        //fixtureDef.friction = 0;
        fixtureDef.restitution = 0.1f; // to prevent sticking to platforms

        body.createFixture(fixtureDef);
        //body.setUserData(this);
        shape.dispose();
        //System.out.println(body.getPosition());
    }

    @Override
    public void initSprite() {

        System.out.println("sprite init");

        atlas = new TextureAtlas(Gdx.files.internal("sylvan/sylvan.atlas"));

        idleFrames = atlas.findRegions("idle");
        walkFrames = atlas.findRegions("walk");
        jumpFrames = atlas.findRegions("jump");
        glideFrames = atlas.findRegions("glide");
        landFrames = atlas.findRegions("land");
        glidepossessFrames = atlas.findRegions("glidepossess");
        standpossessFrames = atlas.findRegions("standpossess");

        idle = new Animation<TextureRegion>(1/9f, idleFrames);
        walk = new Animation<TextureRegion>(1/9f, walkFrames);
        jump = new Animation<TextureRegion>(1/9f, jumpFrames);
        glide = new Animation<TextureRegion>(1/9f, glideFrames);
        land = new Animation<TextureRegion>(1/9f, landFrames);
        glidepossess = new Animation<TextureRegion>(1/9f, glidepossessFrames);
        standpossess = new Animation<TextureRegion>(1/9f, standpossessFrames);

        animations.put("idle",idle);
        animations.put("walk",walk);
        animations.put("jump",jump);
        animations.put("glide",glide);
        animations.put("land",land);
        animations.put("glidepossess",glidepossess);
        animations.put("standpossess",standpossess);

        setBounds(0,0, idleFrames.get(0).getRegionWidth() / SylvanGame.PPM, idleFrames.get(0).getRegionHeight() / SylvanGame.PPM);
        // maybe this is wrong?
        setScale(0.7f);
        setRegion(idleFrames.get(0));

    }

    @Override
    public void move(Control control) {

        //currentState = getState();

        float vx = body.getLinearVelocity().x;
        float vy = body.getLinearVelocity().y;

        // NOTE: currentState != State.JUMP makes it so that you can't wall climb
        if (Math.abs(vy) < .01f) {
            switch (control) {
                case UP:
                    if (currentState != State.FALL && currentState != State.JUMP) {
                        //body.applyForceToCenter(0f, 1f, true);
                        body.setLinearVelocity(vx,5f);
                        //currentState = State.JUMP;
                    }
                    break;
                case LEFT:
                    body.setLinearVelocity(-1f, 0);
                    break;
                case RIGHT:
                    body.setLinearVelocity(1f, 0);
                    break;
                case POSSESS:
                    game.currentLevel.possess();
                    break;
            }
        }
    }

    @Override
    public void updateFrame(float timeElapsed, float dt) { // this was TextureRegion getFrame()

        TextureRegion frame;
        //currentState = getState();
        final State newState = getState();

        if (currentState == newState) { // state has not changed
            stateTimer = stateTimer + dt;
        } else {
            stateTimer = 0;
        }
        currentState = newState;

        switch (currentState) {

            case JUMP:
                frame = (animations.get("jump").getKeyFrame(timeElapsed, false));
                break;
            case FALL:
                frame = (animations.get("glide").getKeyFrame(timeElapsed,false));
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

        // flip frame if it's facing the wrong way
        // doesn't work
        if ((body.getLinearVelocity().x < 0 && !frame.isFlipX()) || (body.getLinearVelocity().x > 0 && frame.isFlipX())) {
            frame.flip(true, false);
        }

        /*

        */


        setRegion(frame);

    }

}
