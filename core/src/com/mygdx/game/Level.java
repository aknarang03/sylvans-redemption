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

public class Level implements Screen {

    private Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    //private Hud hud; // make Hud class

    final SylvanGame game;

    private World world;
    private GameContactListener contactListener;

    private Array<Entity> enemies;

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

        camera = new OrthographicCamera();
        viewport = new FitViewport(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM, camera);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapFilename);
        renderer = new OrthogonalTiledMapRenderer(map,1 / SylvanGame.PPM);

        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight()/2, 0);

        Vector2 gravity = new Vector2(0,-10);
        world = new World(gravity,true);

        world.setContactListener(contactListener);
        contactListener = new GameContactListener();

        this.enemies = enemies;
        createStructure();

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

        for (MapObject mapObject : map.getLayers().get("Ground Objects").getObjects().getByType(RectangleMapObject.class)) {
            System.out.println("got object");
            Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / SylvanGame.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / SylvanGame.PPM);
            body = world.createBody(bodyDef);
            shape.setAsBox((rectangle.getWidth()/ 2) / SylvanGame.PPM, (rectangle.getHeight() / 2) / SylvanGame.PPM);
            fixtureDef.shape = shape;
            fixtureDef.filter.groupIndex = SylvanGame.GROUND_GROUP;
            body.createFixture(fixtureDef);
        }

    }

    @Override
    public void show() {

    }

    // FIX VIEWPORT

    @Override
    public void render(float delta) {

        //System.out.println("level render");
        //renderer.setView(camera);
        //camera.update();
        debugMatrix = game.batch.getProjectionMatrix().cpy().scale(SylvanGame.PPM, SylvanGame.PPM, 0);
        debugRenderer.render(world,debugMatrix);


        // ADD CODE TO ATTACH CAMERA TO CURRENTLY INHABITED ENTITY BODY

        renderer.setView(camera);
        camera.update();

        renderer.render();

    }

    @Override
    public void resize(int width, int height) {
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
    public void dispose() {

    }


}
