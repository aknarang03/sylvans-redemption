package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

// should this not implement screen since I want to just render in game class?
public class Level implements Screen {

    private SylvansRedemption game;
    private World world;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Array<Entity> enemies;

    private BodyDef bodyDef = new BodyDef();
    private PolygonShape shape = new PolygonShape();
    private FixtureDef fixtureDef = new FixtureDef();
    private Body body;

    // constructor
    public Level() {
        // need to send stuff in here

    }

    // create the level structure in box2d
    public void createStructure() {

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }


}
