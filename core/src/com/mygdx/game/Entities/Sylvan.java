package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Entity;
import com.mygdx.game.SylvanGame;

import java.util.HashMap;

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
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!possessed) { return false; }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!possessed) { return false; }
        return false;
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

    }

}
