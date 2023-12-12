package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Entities.Bat;
import com.mygdx.game.Entities.Sylvan;
import com.mygdx.game.Entities.Token;

import java.util.HashMap;

public class Level implements Screen {

    boolean playCompleted;
    boolean playStart;

    public HashMap<String, Sound> sounds = new HashMap();

    // debug renderer vars
    private Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    //private Hud hud; // make Hud class to show the top left interface as seen in game sketch

    // setup vars
    final SylvanGame game;
    private World world;
    private GameContactListener contactListener;

    // enemy vars
    private Array<Entity> enemies; // this will hold the enemies for each level to be drawn
    public Array<Double> distances; // tracks distances between currentEntity and each enemy

    // token vars
    public Array<Token> tokens; // holds the tokens for each level to be drawn
    final int TOKEN_COUNT; // token count to be decided by user
    int numTokensCollected; // amount of tokens collected by user

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

    public Sylvan sylvan;

    Entity currentInhabitedEntity; // track what the player is
    Entity targetEntity; // entity to possess

    // time vars
    private float timeElapsed;
    private float possessTimer; // how long sylvan has possessed an enemy
    public float cooldown; // so that sylvan doesnt possess after unpossess

    private Vector2 disappearPos; // send sylvan's body here during possession

    private Sprite indicator;

    public int id;

    // SOUNDS
    Sound possessSound;
    Sound landSound;
    Sound hitSound;
    Sound collectSound;
    Sound jumpSound;
    Sound glideSound;
    Sound flapSound;
    Sound walkSound;
    Sound skitterSound;

    private boolean pause;

    public Level(final SylvanGame game, Array<Entity> enemies, Array<Token> tokens, String mapFilename, int tokenCount, int id) {

        // init HUD in here

        this.game = game;
        this.id = id;

        // init camera and viewport
        camera = new OrthographicCamera(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM);
        viewport = new FitViewport(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM, camera);
        //viewport = new ExtendViewport(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM, camera);

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
        contactListener = new GameContactListener(this);
        world.setContactListener(contactListener);

        // init arrays
        this.enemies = enemies;
        this.tokens = tokens;
        distances = new Array<Double>();
        wallBodies = new Array<Body>();

        createStructure(); // build level in box2d

        sylvan = new Sylvan(game,new Vector2(5.5f,2.5f)); // create Sylvan

        // possess vars
        possessTimer = 0;
        disappearPos = new Vector2(100,100);
        cooldown = 0;

        TOKEN_COUNT = tokenCount;

        numTokensCollected = 0;

        targetEntity = null;

        createIndicator();
        initSounds();

        playCompleted = true;
        playStart = true;

        pause = false;

    }

    public void createIndicator() {
        indicator = new Sprite();
        Texture indicatorImg = new Texture(Gdx.files.internal("indicator.png"));
        indicator.setRegion(indicatorImg);
    }

    public void initSounds() {
        // maybe move sounds to game instead?

        possessSound = Gdx.audio.newSound(Gdx.files.internal("sounds/possess.mp3"));
        landSound = Gdx.audio.newSound(Gdx.files.internal("sounds/land.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit.mp3"));
        collectSound = Gdx.audio.newSound(Gdx.files.internal("sounds/collect.wav"));
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.mp3"));
        glideSound = Gdx.audio.newSound(Gdx.files.internal("sounds/glide.mp3"));
        flapSound = Gdx.audio.newSound(Gdx.files.internal("sounds/flap.mp3"));
        walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/walk.mp3"));
        skitterSound = Gdx.audio.newSound(Gdx.files.internal("sounds/skitter.mp3"));

        sounds.put("land",landSound);
        sounds.put("possess",possessSound);
        sounds.put("hit",hitSound);
        sounds.put("collect",collectSound);
        sounds.put("jump",jumpSound);
        sounds.put("glide",glideSound);
        sounds.put("flap",flapSound);
        sounds.put("walk",walkSound);
        sounds.put("skitter",skitterSound);

    }

    public void changeCurrentInhabitedEntity(Entity entity) { // call when player is now a different body

        if (currentInhabitedEntity!=null) {currentInhabitedEntity.possessed = false;} // current entity not possessed anymore
        currentInhabitedEntity = entity; // change to the passed in entity
        entity.possessed = true;

        // see if walls should have friction (since spider can climb them)
        if (!(currentInhabitedEntity.body.getUserData() == "spider")) {
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
        sylvan.initSprite();
        sylvan.initBody();
        for (Entity enemy : enemies) {
            enemy.initSprite();
            enemy.initBody();
        }
        int count = 0;
        for (Token token : tokens) {
            token.initSprite();
            token.initBody();
            token.setBounds(token.body.getPosition().x - token.getWidth() * token.MULTIPLYER, token.body.getPosition().y - token.getHeight() * token.MULTIPLYER, token.getWidth(), token.getHeight());
            token.body.setUserData("token"+count);
            count++;
        }
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

        // use wall object layer to put wall bodies
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
        /*
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            pause = !pause;
        }
         */

    }

    public void processPause() { // CHANGE SO THAT IT RECOGNIZES IT AS A PAUSE CONTROL AND A RESTART CONTROL
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!pause) {game.uiSounds.get("pause").play(1f); }
            else {game.uiSounds.get("select").play(1f); }
            pause = !pause;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (pause) {
                game.uiSounds.get("select").play(1f);
                game.restartLevel(0);
            }
        }
    }

    public void checkCollect() {

        int counter = 0;
        for (Token token : tokens) {
            if (token.shouldCollect) {
                world.destroyBody(token.body);
                tokens.removeIndex(counter);
                numTokensCollected++;
                System.out.println(numTokensCollected);
                sounds.get("collect").play(0.2f);
            }
            counter++;
        }

        // set new user data
        for (int i = 0; i < tokens.size; i++) {
            tokens.get(i).body.setUserData("token" + i);
        }

    }

    /*
    public void checkDie() {
        if (sylvan.dead) {
            world.destroyBody(sylvan.body);
        }
    }
     */

    public void update(float delta) {

        // USE pause() AND resume() FOR PAUSE MENU

        if (playStart) {
            game.uiSounds.get("start level").play(1f);
            playStart = false;
        }
        if (endLevel()) {
            // SET SCREEN TO GAME OVER
            game.setScreen(game.gameOver);
        }
        if (completeLevel()) {
            if (playCompleted) {game.uiSounds.get("completed level").play(1f); playCompleted=false;}
            // SET SCREEN TO LEVEL COMPLETED SCREEN
            // DESTROY THIS LEVEL
        }
        /*
        if (pause) {
            pauseLevel();
        }
         */

        // UPDATE DISTANCES ARRAY IN HERE (or in possess? depends if I implement the highlight when enemy is close enough)
        distances.clear();
        for (Entity enemy : enemies) {
            distances.add(getDistance(sylvan.body.getPosition(),enemy.body.getPosition()));
        }

        processInput(); // get user input

        // move the sprites to where the bodies are (they have moved from move() or aiMove())
        sylvan.setBounds(sylvan.body.getPosition().x - sylvan.getWidth() * sylvan.WIDTH_MULTIPLIER, sylvan.body.getPosition().y - sylvan.getHeight() * sylvan.HEIGHT_MULTIPLIER, sylvan.getWidth(), sylvan.getHeight());
        for (Entity enemy : enemies) {
            enemy.setBounds(enemy.body.getPosition().x - enemy.getWidth() * enemy.WIDTH_MULTIPLIER, enemy.body.getPosition().y - enemy.getHeight() * enemy.HEIGHT_MULTIPLIER, enemy.getWidth(), enemy.getHeight());
        }

        indicator.setBounds(currentInhabitedEntity.body.getPosition().x - 0.26f, currentInhabitedEntity.body.getPosition().y + (currentInhabitedEntity.getHeight()/4), 0.5f, 0.5f);

        if (!sylvan.possessed) { possessTimer += delta; } // increment possess timer if sylvan is possessing someone
        sylvan.knockbackTimer -= delta;

        if (shouldPossess()) {
            possess();
        }

        // NOTE: later I will get currentInhabitedEntity.getTimer (which will be a float) since it'll differ per enemy type
        if (possessTimer >= 5) {
            unpossess();
        }

        world.step(1/60f,6,2); // physics step
        if (!world.isLocked()) {
            checkCollect();
        }

        // this function will loop thru all entities to update frame
        sylvan.update(timeElapsed,delta);
        for (Entity enemy : enemies) {
            enemy.update(timeElapsed,delta);
        }

        camera.update();
        renderer.setView(camera);

        cooldown-=delta;
        timeElapsed += delta; // update timeElapsed for animations

    }

    public void unpossess() {
        Vector2 pos = currentInhabitedEntity.getBody().getPosition();
        changeCurrentInhabitedEntity(sylvan);
        sylvan.body.setLinearVelocity(0,0);
        sylvan.body.setTransform(pos.x,pos.y+0.9f,0);
        sylvan.body.applyForceToCenter(0,0.6f,true);
        possessTimer = 0;
        cooldown = 1;
    }

    @Override
    public void render(float delta) { // called in SylvanGame.render()

        // check if user has just pressed pause. if so, do not update level
        processPause();
        if (!pause) {
            // resume any possibly paused long sounds
            sounds.get("walk").resume();
            sounds.get("glide").resume();
            update(delta);
        } else {
            // pause any possible long sounds
            sounds.get("walk").pause();
            sounds.get("glide").pause();
        }

        //update(delta); // update screen

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

        if (sylvan.possessed) { // only draw sylvan if possessed
            sylvan.draw(game.batch);
        }

        // draw the prototype enemies
        // change color if sylvan is close
        int iter = 0;
        for (Entity enemy : enemies) {
            enemy.draw(game.batch);
            if (distances.get(iter) <= 1.5) {
                enemy.setColor(Color.CYAN);
            } else {
                enemy.setColor(Color.WHITE);

            }
            iter++;
        }

        // draw the tokens
        for (Token token : tokens) {

            token.draw(game.batch);

        }

        indicator.draw(game.batch);

        game.batch.end(); // BATCH END

        // SHAPE RENDERER
        if (shouldPossess()) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.rectLine(sylvan.body.getPosition(), targetEntity.body.getPosition(), 0.05f);
            System.out.println(shapeRenderer.isDrawing());
            shapeRenderer.end();
        }

        if (pause) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            //Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            Color pauseColor = new Color(0, 0, 0, 0.7f);
            shapeRenderer.setColor(pauseColor);
            shapeRenderer.rect(0,0, SylvanGame.SCREEN_WIDTH,SylvanGame.SCREEN_HEIGHT);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            game.batch.begin();
            game.font.getData().setScale(1f);
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch,"Press R to restart the level",3,3);
            game.batch.end();
        }

        //timeElapsed += delta; // update timeElapsed for animations

    }

    public boolean shouldPossess() {

        final boolean hasTarget = (targetEntity != null);
        final boolean animationDone = (sylvan.stateTimer >=0.4f);

        if (hasTarget && animationDone) {
            return true;
        }
        return false;
    }

    public void possess() {
        changeCurrentInhabitedEntity(targetEntity);
        sylvan.body.setTransform(disappearPos,0);
        targetEntity = null;
        sylvan.resetState(); // set Sylvan's state back to IDLE from POSSESS
        possessSound.play(0.5f);
    }

    public void getPossessTarget() {

        if (sylvan.possessed && cooldown <= 0) { // if sylvan is currently not possessing anyone

            // get shortest distance
            double shortest = distances.get(0);
            int idx = 0;
            int iter = 0;
            for (double distance : distances) {
                if (distance < shortest) {
                    shortest = distance;
                    idx = iter;
                }
                iter++;
            }

            // check if the possess is valid
            if (shortest <= 1.5) {
                sylvan.currentState = Entity.State.POSSESS;
                sylvan.stateTimer = 0;
                targetEntity = enemies.get(idx);
            }

        }

    }

    public boolean endLevel(){
        if(sylvan.currentState == Entity.State.DEAD && sylvan.stateTimer > 1.5){
            return true;
        }
        return false;
    }

    public boolean completeLevel(){
        if (numTokensCollected == TOKEN_COUNT) {
            return true;
        }
        return false;
    }

    public void pauseLevel() {

    }

    public void getToken(int idx) {
        System.out.println("got token " + idx);
        Token token = tokens.get(idx);
        token.shouldCollect = true;
        System.out.println(token.body.getUserData());
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