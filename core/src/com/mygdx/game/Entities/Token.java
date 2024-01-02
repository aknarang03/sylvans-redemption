package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.SylvanGame;

/*
Anjali Narang
Aaila Arif
Jenna Esposito
 */

public class Token extends Sprite {

    // SETUP VARS
    private SylvanGame game;
    private World world;

    // BODY VARS
    private Vector2 position;
    private BodyDef bodyDef;
    public Body body;
    private PolygonShape shape;
    private FixtureDef fixtureDef;

    private Texture tokenImg;
    final public float MULTIPLYER = 0.1f; // multiplyer for set bounds
    public boolean shouldCollect; // whether the token should be collected by Sylvan or not

    public Token(SylvanGame game, Vector2 position) {
        this.game = game;
        this.position = position;
        shouldCollect = false;
    }

    public void initBody() {

        world = game.currentLevel.getWorld();

        // set up body
        bodyDef = new BodyDef();
        bodyDef.position.set(position.x,position.y);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bodyDef);

        // set up fixture
        shape = new PolygonShape();
        shape.setAsBox(0.05f,0.05f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        shape.dispose();

    }

    public void initImg() {
        tokenImg = new Texture(Gdx.files.internal("token/soultoken.png"));
        setScale(0.2f);
        setBounds(0, 0, tokenImg.getWidth() / SylvanGame.PPM, tokenImg.getHeight() / SylvanGame.PPM);
        setPosition(position.x,position.y);
        setRegion(tokenImg);
    }

}