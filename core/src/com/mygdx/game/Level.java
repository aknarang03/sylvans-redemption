package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.mygdx.game.Entities.Bat;
import com.mygdx.game.Entities.Sylvan;

public class Level implements Screen {

    private Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    //private Hud hud; // make Hud class to show the top left interface as seen in game sketch

    final SylvanGame game;
    private World world;
    private GameContactListener contactListener;
    private Array<Entity> enemies; // this will hold the enemies for each level to be drawn
    public Array<Vector2> distances; // tracks distances between currentEntity and each enemy

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private TmxMapLoader mapLoader;

    private BodyDef bodyDef;
    private PolygonShape shape;
    private FixtureDef fixtureDef;
    private Body body;

    public OrthographicCamera camera;
    private Viewport viewport;

    private Music music;
    private Texture backgroundImage;
    private Sprite backgroundSprite;

    Sylvan sylvan;
    Bat bat; // temporary for prototype
    Entity currentInhabitedEntity;
    private float timeElapsed;


    // constructor
    public Level(final SylvanGame game, Array<Entity> enemies, String mapFilename, String backgroundImgFilename) {

        // init HUD here

        this.game = game;
        debugRenderer = new Box2DDebugRenderer();

        // init camera and viewport
        camera = new OrthographicCamera(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM);
        viewport = new FitViewport(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM, camera);

        // load the map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapFilename);
        renderer = new OrthogonalTiledMapRenderer(map,1/SylvanGame.PPM);

        //camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight()/2, 0);

        // set up world
        Vector2 gravity = new Vector2(0,-10);
        world = new World(gravity,true);

        // set up contact listener
        contactListener = new GameContactListener();
        world.setContactListener(contactListener);

        this.enemies = enemies; // init enemies array

        createStructure(); // build level in box2d

    }

    public void changeCurrentInhabitedEntity(Entity entity) { // call when player is now a different body
        if (currentInhabitedEntity!=null) {currentInhabitedEntity.possessed = false;}
        currentInhabitedEntity = entity;
        entity.possessed = true;
    }

    public World getWorld() {
        return world;
    }

    public void createEntities() { // construct each entity
        // will loop thru enemies array
        Vector2 sylvanPos = new Vector2(1,1.7f);
        //sylvan.setPosition(1/SylvanGame.PPM,1.7f/SylvanGame.PPM);
        sylvan = new Sylvan(game,sylvanPos);
        bat = new Bat(game,new Vector2(2,2));
        changeCurrentInhabitedEntity(sylvan); // on level creation
    }

    // build level in box2d
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

    public void processInput() {

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            currentInhabitedEntity.move(Control.RIGHT);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            currentInhabitedEntity.move(Control.LEFT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            currentInhabitedEntity.move(Control.UP);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) || Gdx.input.isKeyPressed(Input.Keys.E)) {
            currentInhabitedEntity.move(Control.POSSESS);
        }

    }

    public void update(float delta) {

        System.out.println(currentInhabitedEntity.currentState);

        processInput();
        //currentInhabitedEntity.setBounds(currentInhabitedEntity.body.getPosition().x - currentInhabitedEntity.getWidth() * currentInhabitedEntity.WIDTH_MULTIPLYER, currentInhabitedEntity.body.getPosition().y - currentInhabitedEntity.getHeight() * currentInhabitedEntity.HEIGHT_MULTIPLYER, currentInhabitedEntity.getWidth(), currentInhabitedEntity.getHeight());
        sylvan.setBounds(sylvan.body.getPosition().x - sylvan.getWidth() * sylvan.WIDTH_MULTIPLYER, sylvan.body.getPosition().y - sylvan.getHeight() * sylvan.HEIGHT_MULTIPLYER, sylvan.getWidth(), sylvan.getHeight());
        bat.setBounds(bat.body.getPosition().x - bat.getWidth() * bat.WIDTH_MULTIPLYER, bat.body.getPosition().y - bat.getHeight() * bat.HEIGHT_MULTIPLYER, bat.getWidth(), bat.getHeight());

        // UPDATE DISTANCES ARRAY
        /*
        TEMP CODE FOR PROTOTYPE
        if (sylvan.possessed) { // if player is currently not possessing anyone
            double distance = getDistance(sylvan.body.getPosition(),bat.body.getPosition());
            if (distance <= 3) {
                // HIGHLIGHT THE BAT
            }
        }
        */

        // unless I fix this to be more precise we may have to move the arbitrary values to be constants in each Entity class so that we can do this update function on any entity
        // either that or do checks for what type of entity it is and then use the multiplication values accordingly.
        world.step(1/60f,6,2);
        // this function will loop thru all entities to update frame
        sylvan.updateFrame(timeElapsed,delta);
        bat.updateFrame(timeElapsed,delta);
        camera.update();
        renderer.setView(camera);
    }

    @Override
    public void render(float delta) { // called in SylvanGame.render()

        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        camera.position.set(currentInhabitedEntity.getBody().getPosition().x, currentInhabitedEntity.getBody().getPosition().y, 0);

        debugMatrix = game.batch.getProjectionMatrix().cpy().scale(viewport.getScreenWidth(),viewport.getScreenHeight(), 0);
        debugRenderer.render(world,camera.combined);

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        // this will draw all entities by looping thru array
        if (sylvan.possessed) {
            sylvan.draw(game.batch);
        }
        bat.draw(game.batch);
        game.batch.end();

        timeElapsed += delta;
    }

    public void possess() {
        if (sylvan.possessed) { // if player is currently not possessing anyone
            double distance = getDistance(sylvan.body.getPosition(),bat.body.getPosition());
            if (distance <= 3) {
                changeCurrentInhabitedEntity(bat);
            }
        }
    }

    public double getDistance(Vector2 entity1, Vector2 entity2){
        return Math.sqrt(Math.pow((entity2.x - entity1.x), 2) + Math.pow((entity2.y - entity1.y), 2));
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
