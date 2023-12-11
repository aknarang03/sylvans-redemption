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

// Flying Enemy

public class Bat extends Entity {

    private Animation attack;
    private Animation fly;
    private Array<TextureAtlas.AtlasRegion> attackFrames;
    private Array<TextureAtlas.AtlasRegion> flyFrames;

    private float moveTimer = 0; // for "ai" move
    private float flapTimer = 0;

    public Bat(SylvanGame game, Vector2 initPos) {
        super(game,true,0.36f,0.33f);
        initialPosition = initPos;
        ability = "Fly";
    }

    @Override
    public void aiMove(float dt) {
        // swap which way it's moving after move timer goes off
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

        bodyDef = new BodyDef();
        bodyDef.position.set(initialPosition.x,initialPosition.y);
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

    @Override
    public State getState() {

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        if (dead) {
            return State.DEAD;
        }

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
                if (vy == 0) { return State.LAND;
                } else if (vy > 0) { return State.JUMP; } // allows jump in midair (flight)
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

    @Override
    public void move(Control control) {

        final float vx = body.getLinearVelocity().x;
        final float vy = body.getLinearVelocity().y;

        switch (control) {
            case UP:
                //if (vy <= 0) { body.setLinearVelocity(vx,3f); } // jump flight option
                body.setLinearVelocity(vx,1f); // smoother flight option
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

        flapTimer+=dt;

        final State newState = getState();
        if (currentState == newState) { // state has not changed
            stateTimer = stateTimer + dt;
        } else {
            stateTimer = 0;
        }
        currentState = newState;

        switch (currentState) { // animations
            case JUMP:
                frame = (animations.get("fly").getKeyFrame(timeElapsed, true));
                if (flapTimer >= 0.8) {
                //if (animations.get("fly").getKeyFrameIndex(timeElapsed) == 1){
                    game.currentLevel.sounds.get("flap").play(0.5f);
                    flapTimer = 0;
                }
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

        if (!possessed) {
            aiMove(dt);
        }

    }

}
