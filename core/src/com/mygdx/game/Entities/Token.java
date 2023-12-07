package com.mygdx.game.Entities;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.SylvanGame;

public class Token extends Sprite {

    // setup vars
    private SylvanGame game;
    private World world;

    // body vars
    private Vector2 position;
    private BodyDef bodyDef;
    public Body body;
    private PolygonShape shape;
    private FixtureDef fixtureDef;

    // sprite vars
    private Texture tokenImg;


    public Token(SylvanGame game, Vector2 position) {
        this.game = game;
        this.position = position;
    }


    public void initBody() {

        world = game.currentLevel.getWorld();

        // set up body
        bodyDef = new BodyDef();
        bodyDef.position.set(position.x,position.y);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bodyDef);
        //body.setUserData("token");

        // set up fixture
        shape = new PolygonShape();
        shape.setAsBox(0.2f,0.2f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);

        shape.dispose();

    }

    public void initSprite() {

        tokenImg = new Texture(Gdx.files.internal("token/soultoken.png"));
        setScale(20f);
        setTexture(tokenImg);
        setPosition(position.x,position.y);
        setRegion(tokenImg);

    }


}
