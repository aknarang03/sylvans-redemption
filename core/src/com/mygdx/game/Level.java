package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Entities.Sylvan;

public class Level implements Screen {

    private Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;
    //private Hud hud; // make Hud class to show the top left interface as seen in game sketch

    final SylvanGame game;
    private World world;
    private GameContactListener contactListener;

    private Array<Entity> enemies; // this will hold the enemies for each level to be drawn

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private TmxMapLoader mapLoader;

    private BodyDef bodyDef;
    private PolygonShape shape;
    private FixtureDef fixtureDef;
    private Body body;

    private OrthographicCamera camera;
    private Viewport viewport;

    private Music music;

    // constructor
    public Level(final SylvanGame game, Array<Entity> enemies, String mapFilename) {

        // init HUD

        this.game = game;

        debugRenderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM);
        viewport = new FitViewport(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM, camera);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapFilename);
        renderer = new OrthogonalTiledMapRenderer(map,1 / SylvanGame.PPM); // TRY EDITING

        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight()/2, 0);

        Vector2 gravity = new Vector2(0,-10);
        world = new World(gravity,true);

        contactListener = new GameContactListener();
        world.setContactListener(contactListener);

        this.enemies = enemies;
        createStructure();

    }

    public World getWorld() {
        return world;
    }

    // create the level structure in box2d
    public void createStructure() {

        bodyDef = new BodyDef();
        shape = new PolygonShape();
        fixtureDef = new FixtureDef();

        System.out.println(map.getTileSets().getTileSet(0).getName());
        System.out.println(map.getLayers().get(0).getName());
        System.out.println(map.getLayers().getCount());
        System.out.println(map.getLayers().get(1).getName());

        // uses the ground object layer in tmx file to draw the boxes in the correct places
        for (MapObject mapObject : map.getLayers().get("Ground Objects").getObjects().getByType(RectangleMapObject.class)) {
            System.out.println("got object");
            Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / SylvanGame.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / SylvanGame.PPM);
            body = world.createBody(bodyDef);
            shape.setAsBox(rectangle.getWidth() / 2 / SylvanGame.PPM, rectangle.getHeight() / 2 / SylvanGame.PPM);
            fixtureDef.shape = shape;
            fixtureDef.filter.groupIndex = SylvanGame.GROUND_GROUP;
            body.createFixture(fixtureDef);
        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) { // called in SylvanGame.render()

        //System.out.println("level render");

        debugMatrix = game.batch.getProjectionMatrix().cpy().scale(viewport.getScreenWidth(),viewport.getScreenHeight(), 0);
        debugRenderer.render(world,camera.combined);

        // CODE TO ATTACH CAMERA TO CURRENTLY INHABITED ENTITY BODY
        //camera.position.set(game.getCurrentInhabitedEntity().getBody().getPosition().x, 0, 0);

        renderer.setView(camera);
        camera.update();

        world.step(1f / 60f, 6, 2); // physics step
        renderer.render(); // this is the map renderer

    }

    @Override
    public void resize(int width, int height) { // handles window resize
        viewport.update(width,height,true);
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
    public void dispose() { // IMPLEMENT

    }


}
