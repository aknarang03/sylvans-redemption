package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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
        initialPosition = initPos;
        initSprite();
        initBody();
        System.out.println("width:" + this.getWidth());
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
        setScale(0.7f);
        setRegion(idleFrames.get(0));

    }

    @Override
    public void move(Control control) {

        currentState = getState();
        System.out.println(currentState);

        float vy = body.getLinearVelocity().y;

        // NOTE: currentState != State.JUMP makes it so that you can't wall climb
        if (Math.abs(vy) < .01f && currentState != State.JUMP) {
            switch (control) {
                case UP:
                    if (previousState != State.FALL ) {
                        body.applyForceToCenter(0f, 1f, true);
                        currentState = State.JUMP;
                    }
                    break;
                case LEFT:
                    body.setLinearVelocity(-1f, 0);
                    break;
                case RIGHT:
                    body.setLinearVelocity(1f, 0);
                    break;
            }
        }
    }

    @Override
    public void updateFrame(float timeElapsed, float dt) { // this was TextureRegion getFrame()

        TextureRegion frame;
        previousState = currentState;
        currentState = getState();

        switch (currentState) {

            case JUMP:
                frame = (animations.get("jump").getKeyFrame(timeElapsed, true));
                break;
            case FALL:
                frame = (animations.get("glide").getKeyFrame(timeElapsed,true));
                break;
            case WALK:
                frame = (animations.get("walk").getKeyFrame(timeElapsed, true));
                break;
            case LAND:
                frame = (animations.get("land").getKeyFrame(timeElapsed, true));
                break;
            default:
                frame = (animations.get("idle").getKeyFrame(timeElapsed, true));
                break;

        }

        // flip frame if it's facing the wrong way
        // doesn't work
        if ((body.getLinearVelocity().x < 0 && !frame.isFlipX()) || (body.getLinearVelocity().x > 0 && frame.isFlipX())) {
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
