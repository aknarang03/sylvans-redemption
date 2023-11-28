package com.mygdx.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

// should it extend sprite??
public abstract class Entity extends Sprite implements InputProcessor {

    // vars for sprite (there will be more in each entity)

    // vars for body
    SylvanGame game;
    protected Vector2 initialPosition;
    protected BodyDef bodyDef;
    protected Body body;
    PolygonShape shape;
    FixtureDef fixtureDef;
    protected World world;

    protected boolean possessed;


    public Entity(SylvanGame game) {
        this.game = game;
    }

    public void setPossessed(boolean possessed) {
        this.possessed = possessed;
    }

    public void initBody() {
        System.out.println("init body");
        world = game.currentLevel.getWorld();
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(initialPosition);
        body = world.createBody(bodyDef);
        body.setFixedRotation(true);
        shape = new PolygonShape();
        shape.setAsBox(0.35f,0.35f); // temp values; this will be sprite.getWidth() and sprite.getHeight()
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.01f;
        body.createFixture(fixtureDef);
        System.out.println(body.getPosition());
    }

    public Body getBody() {
        return body;
    }

    // implementation of these will differ based on the entity
    @Override
    public abstract boolean keyDown(int keycode);
    @Override
    public abstract boolean keyUp(int keycode);
    public abstract void initSprite();







    // JUNK
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
