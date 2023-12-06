package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.mygdx.game.Entities.Rock;
import com.mygdx.game.Entities.Spider;
import com.mygdx.game.Entities.Sylvan;

public class Level implements Screen {

    // debug renderer vars
    private Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    //private Hud hud; // make Hud class to show the top left interface as seen in game sketch

    final SylvanGame game;
    private World world;
    private GameContactListener contactListener;

    private Array<Entity> enemies; // this will hold the enemies for each level to be drawn
    public Array<Vector2> distances; // tracks distances between currentEntity and each enemy

    // tiled map vars
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private TmxMapLoader mapLoader;

    // body vars
    private BodyDef bodyDef;
    private PolygonShape shape;
    private FixtureDef fixtureDef;
    private Body body;
    private Array<Body> wallBodies; // need this to access friction of wall bodies to change when player is spider

    // camera vars
    public OrthographicCamera camera;
    private Viewport viewport;

    // visuals / sounds
    private Music music;
    private Sprite backgroundSprite;

    ShapeRenderer shapeRenderer; // for the line that's drawn between sylvan and enemy

    Sylvan sylvan;
    Bat bat; // temporary for prototype (will later be in array sent in constructor)
    Spider spider; // temporary for prototype (will later be in array sent in constructor)
    Rock rock; // temporary for prototype (will later be in array sent in constructor)

    Entity currentInhabitedEntity; // track what the player is

    // time vars
    private float timeElapsed;
    private float possessTimer; // how long sylvan has possessed an enemy

    private Vector2 disappearPos; // send sylvan's body here during possession

    public Level(final SylvanGame game, Array<Entity> enemies, String mapFilename) {

        // init HUD in here

        this.game = game;

        // init camera and viewport
        camera = new OrthographicCamera(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM);
        viewport = new FitViewport(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM, camera);

        // load the map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapFilename);

        // init renderers
        renderer = new OrthogonalTiledMapRenderer(map,1/SylvanGame.PPM);
        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();

        // set up world
        Vector2 gravity = new Vector2(0,-10);
        world = new World(gravity,true);

        // set up contact listener
        contactListener = new GameContactListener();
        world.setContactListener(contactListener);

        // init arrays
        this.enemies = enemies;
        wallBodies = new Array<Body>();

        createStructure(); // build level in box2d

        // possess vars
        possessTimer = 0;
        disappearPos = new Vector2(100,100);

    }

    public void changeCurrentInhabitedEntity(Entity entity) { // call when player is now a different body

        if (currentInhabitedEntity!=null) {currentInhabitedEntity.possessed = false;} // current entity not possessed anymore
        currentInhabitedEntity = entity; // change to the passed in entity
        entity.possessed = true;

        // see if walls should have friction (since spider can climb them)
        if (!spider.possessed) {
            for (Body body : wallBodies) {
                body.getFixtureList().get(0).setFriction(0);
            }
        } else {
            for (Body body : wallBodies) {
                body.getFixtureList().get(0).setFriction(1);
            }
        }

    }

    public void createEntities() { // construct each entity
        // THIS WILL LOOP THRU ENTITIES ARRAY. CURRENT IMPLEMENTATION TEMPORARY
        sylvan = new Sylvan(game,new Vector2(5.5f,2.5f));
        bat = new Bat(game,new Vector2(5,1));
        spider = new Spider(game,new Vector2(2.4f,2.5f));
        rock = new Rock(game,new Vector2(1,1));
        changeCurrentInhabitedEntity(sylvan); // on level creation, sylvan is inhabited
    }

    public void createStructure() { // build level in box2d

        bodyDef = new BodyDef();
        shape = new PolygonShape();
        fixtureDef = new FixtureDef();

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
            body.setUserData("ground");
        }

        // use wall object layer to draw walls
        for (MapObject mapObject : map.getLayers().get("Wall Objects").getObjects().getByType(RectangleMapObject.class)) {
            System.out.println("got object");
            Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / SylvanGame.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / SylvanGame.PPM);
            body = world.createBody(bodyDef);
            shape.setAsBox(rectangle.getWidth() / 2 / SylvanGame.PPM, rectangle.getHeight() / 2 / SylvanGame.PPM);
            fixtureDef.shape = shape;
            fixtureDef.filter.groupIndex = SylvanGame.GROUND_GROUP;
            fixtureDef.friction = 0;
            body.createFixture(fixtureDef);
            body.setUserData("wall");
            wallBodies.add(body);
        }

    }

    public void processInput() { // pass the input to current entity's move() function

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

        // UPDATE DISTANCES ARRAY IN HERE (or in possess? depends if I implement the highlight when enemy is close enough)

        processInput(); // get user input

        // move the sprites to where the bodies are (they have moved from move() or aiMove())
        sylvan.setBounds(sylvan.body.getPosition().x - sylvan.getWidth() * sylvan.WIDTH_MULTIPLIER, sylvan.body.getPosition().y - sylvan.getHeight() * sylvan.HEIGHT_MULTIPLIER, sylvan.getWidth(), sylvan.getHeight());
        bat.setBounds(bat.body.getPosition().x - bat.getWidth() * bat.WIDTH_MULTIPLIER, bat.body.getPosition().y - bat.getHeight() * bat.HEIGHT_MULTIPLIER, bat.getWidth(), bat.getHeight());
        spider.setBounds(spider.body.getPosition().x - spider.getWidth() * spider.WIDTH_MULTIPLIER, spider.body.getPosition().y - spider.getHeight() * spider.HEIGHT_MULTIPLIER, spider.getWidth(), spider.getHeight());
        rock.setBounds(rock.body.getPosition().x - rock.getWidth() * rock.WIDTH_MULTIPLIER, rock.body.getPosition().y - rock.getHeight() * rock.HEIGHT_MULTIPLIER, rock.getWidth(), rock.getHeight());


        if (!sylvan.possessed) { possessTimer += delta; } // increment possess timer if sylvan is possessing someone

        // NOTE: later I will get currentInhabitedEntity.getTimer (which will be a float) since it'll differ per enemy type
        if (possessTimer >= 5) {
            Vector2 pos = currentInhabitedEntity.getBody().getPosition();
            changeCurrentInhabitedEntity(sylvan);
            sylvan.body.setTransform(pos.x,pos.y+1,0);
            possessTimer = 0;
        }

        world.step(1/60f,6,2); // physics step

        // this function will loop thru all entities to update frame
        sylvan.update(timeElapsed,delta);
        bat.update(timeElapsed,delta);
        spider.update(timeElapsed,delta);
        rock.update(timeElapsed,delta);
        camera.update();
        renderer.setView(camera);

    }

    @Override
    public void render(float delta) { // called in SylvanGame.render()

        update(delta); // update screen

        // clear screen
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render(); // render level

        camera.position.set(currentInhabitedEntity.getBody().getPosition().x, currentInhabitedEntity.getBody().getPosition().y, 0); // set camera pos to player

        // render debug boxes
        debugMatrix = game.batch.getProjectionMatrix().cpy().scale(viewport.getScreenWidth(),viewport.getScreenHeight(), 0);
        debugRenderer.render(world,camera.combined);

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin(); // BATCH BEGIN
        // NOTE: this will draw all entities by looping thru array
        if (sylvan.possessed) { // only draw sylvan if possessed
            sylvan.draw(game.batch);
        }
        // draw the prototype enemies
        bat.draw(game.batch);
        spider.draw(game.batch);
        rock.draw(game.batch);
        game.batch.end(); // BATCH END

        /* // SHAPE RENDERER TEST (doesn't work properly)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1,0,0,1);
        shapeRenderer.line(sylvan.getBody().getPosition().x,sylvan.getBody().getPosition().y,bat.getBody().getPosition().x, bat.getBody().getPosition().y);
        System.out.println(shapeRenderer.isDrawing());
        shapeRenderer.end();
         */

        timeElapsed += delta; // update timeElapsed for animations

    }

    public void possess() {

        if (sylvan.possessed) { // if sylvan is currently not possessing anyone

            // get distances for prototype enemies (temp code for prototype)
            double batDistance = getDistance(sylvan.body.getPosition(),bat.body.getPosition());
            double spiderDistance = getDistance(sylvan.body.getPosition(),spider.body.getPosition());
            double rockDistance = getDistance(sylvan.body.getPosition(),rock.body.getPosition());

            // check if the possess is valid. (temp code for prototype; this will loop thru enemies array)
            // here bat has priority
            if (batDistance <= 1.5) {
                changeCurrentInhabitedEntity(bat);
                sylvan.body.setTransform(disappearPos,0);
            } else if (spiderDistance <= 1.5) {
                changeCurrentInhabitedEntity(spider);
                sylvan.body.setTransform(disappearPos,0);
            } else if (rockDistance <= 1.5) {
                changeCurrentInhabitedEntity(rock);
                sylvan.body.setTransform(disappearPos,0);
            }

        }

    }

    public double getDistance (Vector2 entity1, Vector2 entity2) { // get distance between two entities
        return Math.sqrt(Math.pow((entity2.x - entity1.x), 2) + Math.pow((entity2.y - entity1.y), 2));
    }

    public World getWorld() { return world; }

    @Override
    public void resize(int width, int height) { // handles window resize
        viewport.update(width,height,true);
    }
    @Override
    public void dispose() {} // IMPLEMENT

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void show() {}
}
