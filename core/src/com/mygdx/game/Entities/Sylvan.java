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

public class Sylvan extends Entity {

    // sprite variables
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
        super(game,false, 0.36f,0.33f);
        initialPosition = initPos;
        initSprite();
        initBody();
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
        shape.setAsBox(getWidth() / 3.8f, getHeight() / 3.1f);
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
        jump = new Animation<TextureRegion>(1 / 5f, jumpFrames);
        glide = new Animation<TextureRegion>(1 / 9f, glideFrames);
        land = new Animation<TextureRegion>(1 / 9f, landFrames);
        glidepossess = new Animation<TextureRegion>(1 / 9f, glidepossessFrames);
        standpossess = new Animation<TextureRegion>(1 / 9f, standpossessFrames);

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
    public void move(Control control) {

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        switch (control) {
            case UP: {
                if (currentState == State.FALL && Math.abs(vy) > .01f) { // your vertical velocity is not close to 0 (ie jumping or falling)
                    body.setLinearVelocity(vx, 0.1f * vy);
                } else if (currentState != State.FALL && currentState != State.JUMP && Math.abs(vy) < .01f) { // your vertical velocity is close to 0
                    body.setLinearVelocity(vx, 5f);
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
                game.currentLevel.possess();
                break;
            default:
                break;
        }
    }

    @Override
    public void update(float timeElapsed, float dt) { // this was TextureRegion getFrame()

        TextureRegion frame;
        final State newState = getState();

        if (currentState == newState) { // state has not changed
            stateTimer += dt;
        } else {
            stateTimer = 0;
        }

        currentState = newState;

        switch (currentState) {

            case JUMP:
                frame = (animations.get("jump").getKeyFrame(timeElapsed, true));
                break;
            case FALL:
                frame = (animations.get("glide").getKeyFrame(timeElapsed, false));
                // fall has same animation as glide intentionally
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

        if ((left && !frame.isFlipX()) || (!left && frame.isFlipX())) {
            frame.flip(true, false);
        }
        setRegion(frame);
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
                else if (vy < 0) {
                    return State.FALL;
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

    @Override
    public void aiMove(float dt) {} // this will never be called on sylvan
}
