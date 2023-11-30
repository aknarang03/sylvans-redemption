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
        super(game); // set the game
        initialPosition = initPos;
        initSprite();
        System.out.println("width:" + this.getWidth());
    }

    @Override
    public void move(Control control) {

        currentState = getState();

        float vy = body.getLinearVelocity().y;
        //System.out.println(Math.abs(vy));

        // This also allows wall climbing.. probably have a jump boolean or something
        if (Math.abs(vy) < .01f) {
            switch (control) {
                case LEFT:
                    body.setLinearVelocity(-1f, 0);
                    break;
                case RIGHT:
                    body.setLinearVelocity(1f, 0);
                    break;
                case UP:
                    body.applyForceToCenter(0f, 1f, true);
                    break;
            }
        }

        /*
        if (Math.abs(vy) < .01f ) { // not currently jumping
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                body.setLinearVelocity(1f, 0);
            } if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                body.setLinearVelocity(-1f, 0);;
            } if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
                //if (currentState != State.JUMP && currentState != State.FALL)
                body.applyForceToCenter(0f, 1f, true);
            }
        }
        */



        /*
        // this allowed wall climbing; keep code for the climbing enemy.
        float f = body.getLinearVelocity().y;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(1f, f);
        } if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-1f, f);;
        } if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (currentState != State.JUMP && currentState != State.FALL)
                body.applyForceToCenter(0f, 1f, true);
        } //else if (keycode == Input.Keys.SHIFT_RIGHT || keycode == Input.Keys.E) {
            // THIS WILL HAVE POSSESS CODE / CALL A POSSESS FUNCTION
        //}
         */

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



        setRegion(idleFrames.get(0));
        //setScale(1.3f);
        setSize(getWidth() / SylvanGame.PPM, getHeight() / SylvanGame.PPM);
        setBounds(0,0, idleFrames.get(0).getRegionWidth(), idleFrames.get(0).getRegionHeight());
        //setBounds(0,0, 0.5f, 0.5f);
        // NOTE: bounds seems to change whether he flickers onto screen or not

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

        setRegion(frame);
        // idk if this set position is right
        //setPosition(body.getPosition().x - getWidth(), body.getPosition().y - getHeight());
        setPosition((body.getPosition().x * SylvanGame.PPM) - getWidth() / 2, (body.getPosition().y * SylvanGame.PPM) - getHeight() / 2);


    }

}
