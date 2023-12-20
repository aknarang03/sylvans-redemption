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
import com.mygdx.game.Entities.Sylvan;
import com.mygdx.game.Entities.Token;

import java.util.HashMap;

/*
Anjali Narang
Aaila Arif
Jenna Esposito
 */

public class Level implements Screen {

    public int id; // level ID

    // SETUP
    final SylvanGame game;
    private World world;
    private InfoDisplay infoDisplay; // HUD
    private GameContactListener contactListener;

    // ENTITY
    public Sylvan sylvan; // this level's instance of Sylvan
    private Vector2 disappearPos; // send sylvan's body here during possession
    public Entity currentInhabitedEntity; // track which Entity is being controlled by player
    private Sprite indicator; // indicates currentInhabitedEntity
    private Entity targetEntity; // Entity to possess

    // RENDERERS
    //private Box2DDebugRenderer debugRenderer;
    //Matrix4 debugMatrix; // matrix for debug renderer
    ShapeRenderer shapeRenderer; // for the line that's drawn between sylvan and enemy and the pause box
    private OrthogonalTiledMapRenderer mapRenderer;

    // TIMERS
    private double redTimer; // timer for Sylvan flashing red
    private float timeElapsed; // time elapsed since level start
    public float possessTimer; // how long Sylvan has possessed an enemy
    public float possessCooldown; // so that Sylvan doesnt possess after unpossess

    // BOOLS
    private boolean playCompleted; // whether to play level completed sound
    private boolean playStart; // whether to play level start sound
    private boolean pause; // whether the level is paused

    // ARRAYS
    private Array<Entity> enemies; // holds the level's enemies
    public Array<Double> distances; // tracks distances between currentEntity and each enemy

    // TOKEN
    public Array<Token> tokens; // holds the level's tokens
    public final int TOKEN_COUNT; // total number of tokens in the level
    public int numTokensCollected; // amount of tokens collected by player

    // TILED MAP
    private TiledMap map;
    private TmxMapLoader mapLoader;

    // BODY
    private BodyDef bodyDef;
    private PolygonShape shape;
    private FixtureDef fixtureDef;
    private Body body;
    private Array<Body> wallBodies; // need this to access friction of wall bodies to change when player is spider

    // CAMERA
    public OrthographicCamera camera;
    public OrthographicCamera textCam; // camera for pause overlay
    public Viewport viewport;

    // SOUNDS
    public Music music; // level music
    public HashMap<String, Sound> sounds = new HashMap(); // sound storage
    private Sound possessSound;
    private Sound landSound;
    private Sound hitSound;
    private Sound collectSound;
    private Sound jumpSound;
    private Sound glideSound;
    private Sound flapSound;
    private Sound walkSound;
    private Sound skitterSound;

    public Level(final SylvanGame game, Array<Entity> enemies, Array<Token> tokens, String mapFilename, int tokenCount, int id, Music music) {

        // declare camera after viewport and use viewport width and height??

        this.game = game;
        this.id = id;

        // INIT TIMERS
        redTimer = 1;
        possessCooldown = 0;
        possessTimer = 0;

        // init camera and viewport
        camera = new OrthographicCamera(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM);
        viewport = new FitViewport(SylvanGame.SCREEN_WIDTH / SylvanGame.PPM, SylvanGame.SCREEN_HEIGHT / SylvanGame.PPM, camera);

        // init camera for pause overlay
        float aspectRatio = (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
        textCam = new OrthographicCamera(1000, 1000 * aspectRatio);

        // init HUD
        infoDisplay = new InfoDisplay(this.game);

        // load the map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapFilename);

        // init renderers
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / SylvanGame.PPM);
        shapeRenderer = new ShapeRenderer();
        //debugRenderer = new Box2DDebugRenderer();

        // set up world
        Vector2 gravity = new Vector2(0, -10);
        world = new World(gravity, true);

        // set up contact listener
        contactListener = new GameContactListener(this);
        world.setContactListener(contactListener);

        // init arrays
        this.enemies = enemies;
        this.tokens = tokens;
        distances = new Array<Double>();
        wallBodies = new Array<Body>();

        createStructure(); // build level in box2d

        sylvan = new Sylvan(game, new Vector2(1, 1)); // create Sylvan
        disappearPos = new Vector2(100, 100); // set a position for Sylvan to disappear to to give illusion that he is gone while possessing an enemy

        // init vars for token count
        TOKEN_COUNT = tokenCount;
        numTokensCollected = 0;

        targetEntity = null; // no entity is being targeted for possession at first

        createIndicator(); // init indicator sprite
        initSounds(); // init sounds and put in map

        // init booleans
        playCompleted = true;
        playStart = true;
        pause = false;

        this.music = music; // passed in music is level's music

    }

    public void createIndicator() {
        indicator = new Sprite();
        Texture indicatorImg = new Texture(Gdx.files.internal("indicator.png"));
        indicator.setRegion(indicatorImg);
    }

    public void initSounds() {

        possessSound = Gdx.audio.newSound(Gdx.files.internal("sounds/possess.mp3"));
        landSound = Gdx.audio.newSound(Gdx.files.internal("sounds/land.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit.mp3"));
        collectSound = Gdx.audio.newSound(Gdx.files.internal("sounds/collect.wav"));
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.mp3"));
        glideSound = Gdx.audio.newSound(Gdx.files.internal("sounds/glide.mp3"));
        flapSound = Gdx.audio.newSound(Gdx.files.internal("sounds/flap.mp3"));
        walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/walk.mp3"));
        skitterSound = Gdx.audio.newSound(Gdx.files.internal("sounds/skitter.mp3"));

        sounds.put("land", landSound);
        sounds.put("possess", possessSound);
        sounds.put("hit", hitSound);
        sounds.put("collect", collectSound);
        sounds.put("jump", jumpSound);
        sounds.put("glide", glideSound);
        sounds.put("flap", flapSound);
        sounds.put("walk", walkSound);
        sounds.put("skitter", skitterSound);

    }

    public void changeCurrentInhabitedEntity(Entity entity) { // call when player is now a different body

        if (currentInhabitedEntity != null) {
            currentInhabitedEntity.possessed = false;
        } // current entity not possessed anymore

        currentInhabitedEntity = entity; // change to the passed in entity
        entity.possessed = true;

        // see if walls should have friction (since spider can climb them)
        if (!(currentInhabitedEntity.body.getUserData() == "spider")) {
            // if player is not a spider, wall friction should be 0 (unclimbable)
            for (Body body : wallBodies) {
                body.getFixtureList().get(0).setFriction(0);
            }
        } else {
            // if player is a spider, wall friction should be 1 (climbable)
            for (Body body : wallBodies) {
                body.getFixtureList().get(0).setFriction(1);
            }
        }

    }

    public void createEntities() { // construct each entity

        sylvan.initSprite();
        sylvan.initBody();

        for (Entity enemy : enemies) {
            enemy.initSprite();
            enemy.initBody();
        }

        int count = 0; // count will be added to each token body's user data for use in the contact listener
        for (Token token : tokens) {
            token.initImg();
            token.initBody();
            token.setBounds(token.body.getPosition().x - token.getWidth() * token.MULTIPLYER, token.body.getPosition().y - token.getHeight() * token.MULTIPLYER, token.getWidth(), token.getHeight());
            token.body.setUserData("token" + count);
            count++;
        }

        changeCurrentInhabitedEntity(sylvan); // on level creation, sylvan is inhabited

    }

    public void createStructure() { // build level in box2d

        bodyDef = new BodyDef();
        shape = new PolygonShape();
        fixtureDef = new FixtureDef();

        // wall objects
        for (MapObject mapObject : map.getLayers().get("Wall Objects").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / SylvanGame.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / SylvanGame.PPM);
            body = world.createBody(bodyDef);
            shape.setAsBox(rectangle.getWidth() / 2 / SylvanGame.PPM, rectangle.getHeight() / 2 / SylvanGame.PPM);
            fixtureDef.shape = shape;
            fixtureDef.friction = 0; // friction for walls starts at 0
            body.createFixture(fixtureDef);
            body.setUserData("wall");
            wallBodies.add(body); // add to array so that they can be accessed later and friction can be changed
        }

        // ground objects
        for (MapObject mapObject : map.getLayers().get("Ground Objects").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / SylvanGame.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / SylvanGame.PPM);
            body = world.createBody(bodyDef);
            shape.setAsBox(rectangle.getWidth() / 2 / SylvanGame.PPM, rectangle.getHeight() / 2 / SylvanGame.PPM);
            fixtureDef.shape = shape;
            fixtureDef.friction = 1;
            body.createFixture(fixtureDef);
            body.setUserData("ground");
        }

        // damage objects (right now, just water)
        for (MapObject mapObject : map.getLayers().get("Damage Objects").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / SylvanGame.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / SylvanGame.PPM);
            body = world.createBody(bodyDef);
            shape.setAsBox(rectangle.getWidth() / 2 / SylvanGame.PPM, rectangle.getHeight() / 2 / SylvanGame.PPM);
            fixtureDef.shape = shape;
            fixtureDef.friction = 1;
            body.createFixture(fixtureDef);
            body.setUserData("damage");
        }

        // boundary objects (level bounds)
        for (MapObject mapObject : map.getLayers().get("Boundary Objects").getObjects().getByType(RectangleMapObject.class)) {
            System.out.println("got object");
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / SylvanGame.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / SylvanGame.PPM);
            body = world.createBody(bodyDef);
            shape.setAsBox(rectangle.getWidth() / 2 / SylvanGame.PPM, rectangle.getHeight() / 2 / SylvanGame.PPM);
            fixtureDef.shape = shape;
            fixtureDef.friction = 0;
            body.createFixture(fixtureDef);
            body.setUserData("boundary");
        }

    }

    public void processInput() { // pass the keyboard input to current entity's move() function

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            currentInhabitedEntity.move(Control.RIGHT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            currentInhabitedEntity.move(Control.LEFT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            currentInhabitedEntity.move(Control.UP);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            currentInhabitedEntity.move(Control.POSSESS);
        }

    }

    public void processPause() {

        // change whether level is paused based on current pause value on press ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!pause) {
                game.uiSounds.get("pause").play(1f);
            } else {
                game.uiSounds.get("select").play(1f);
            }
            pause = !pause;
        }

        // if level is paused and ENTER is pressed, restart the level
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (pause) {
                game.uiSounds.get("select").play(1f);
                game.restartLevel(id);
            }
        }

        // if level is paused and R is pressed, restart the game
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (pause) {
                game.uiSounds.get("select").play(1f);
                game.restartGame();
            }
        }

    }

    public void checkCollect() { // check whether to collect each token

        int counter = 0; // used to figure out which token should be removed from tokens array

        for (Token token : tokens) {

            if (token.shouldCollect) {
                world.destroyBody(token.body);
                tokens.removeIndex(counter); // remove collected token from array
                numTokensCollected++;
                sounds.get("collect").play(0.2f);

                // set new user data to reflect new indices
                for (int i = 0; i < tokens.size; i++) {
                    tokens.get(i).body.setUserData("token" + i);
                }

            }
            counter++;
        }

    }

    public void checkDie() { // check if any Entity should die
        // state timer is checked as well so that death animation can play fully

        // check if Sylvan should die
        if (sylvan.dead && sylvan.stateTimer >= 0.3) {
            sylvan.shouldDraw = false; // don't draw sylvan if dead
        }

        int counter = 0;
        for (Entity enemy : enemies) {
            if (enemy.dead && enemy.stateTimer >= 0.5) {
                world.destroyBody(enemy.body);
                enemies.removeIndex(counter); // remove enemy from enemies array upon death
            }
            counter++;
        }

    }

    public void checkForScreenChange() {

        if (endLevel()) {
            game.setScreen(game.gameOver);
        }

        if (completeLevel()) {
            if (playCompleted) {
                game.uiSounds.get("completed level").play(1f);
                playCompleted = false; // completed sound plays once upon level complete
                if (id < 2) {
                    game.setScreen(game.levelWin);
                } else { // there are 2 levels so if this is level 2 the game is completed
                    game.setScreen(game.gameComplete);
                }
            }
        }

    }

    public void updateDistancesArray() {
        distances.clear();
        for (Entity enemy : enemies) {
            distances.add(getDistance(sylvan.body.getPosition(), enemy.body.getPosition()));
        }
    }

    public void updatePositions() { // move the sprites to where the bodies are (they have moved from move() or aiMove())
        sylvan.setBounds(sylvan.body.getPosition().x - sylvan.getWidth() * sylvan.WIDTH_MULTIPLIER, sylvan.body.getPosition().y - sylvan.getHeight() * sylvan.HEIGHT_MULTIPLIER, sylvan.getWidth(), sylvan.getHeight());
        for (Entity enemy : enemies) {
            enemy.setBounds(enemy.body.getPosition().x - enemy.getWidth() * enemy.WIDTH_MULTIPLIER, enemy.body.getPosition().y - enemy.getHeight() * enemy.HEIGHT_MULTIPLIER, enemy.getWidth(), enemy.getHeight());
            enemy.possessIndicator.setBounds(enemy.body.getPosition().x - 0.26f,enemy.body.getPosition().y + (enemy.getHeight() / 10),0.5f,0.5f);
        }
        indicator.setBounds(currentInhabitedEntity.body.getPosition().x - 0.26f, currentInhabitedEntity.body.getPosition().y + (currentInhabitedEntity.getHeight() / 10), 0.5f, 0.5f);
    }

    public void update(float delta) {

        if (playStart) { // if level start sound should play
            game.uiSounds.get("start level").play(1f);
            playStart = false;
        }

        checkForScreenChange(); // see if screen should be switched
        updateDistancesArray(); // get distances between Sylvan and each enemy
        processInput(); // get user input
        updatePositions(); // update positions of sprites

        // UPDATE TIMERS
        timeElapsed += delta; // update timeElapsed for animations
        if (!sylvan.possessed) { possessTimer += delta; } // increment possess timer if sylvan is possessing someone
        sylvan.knockbackTimer -= delta; // decrement knockback dmg cooldown
        possessCooldown -= delta; // decrement possess cooldown

        if (shouldPossess() && !sylvan.dead) {
            possess();
        }

        if (possessTimer >= currentInhabitedEntity.posTime && !sylvan.possessed) { // if time has run out, unpossess
            if ((currentInhabitedEntity.body.getUserData() == "bat" || currentInhabitedEntity.body.getUserData() == "spider") && sylvan.health < 3)  {sylvan.health++;} // Sylvan gets 1 HP back if he possesses enemy for 5 seconds
            unpossess();
        }

        world.step(1 / 60f, 6, 2); // physics step

        // checks involving body destroy
        if (!world.isLocked()) { // needed to avoid error when destroying bodies
            checkCollect();
            checkDie();
        }

        // update Entities using their update functions
        sylvan.update(timeElapsed, delta);
        for (Entity enemy : enemies) {
            enemy.update(timeElapsed, delta);
        }

        if (!sylvan.dead) {camera.update();
        mapRenderer.setView(camera); // has to be called anytime camera updates
        }

        infoDisplay.updateLabels(); // update what displays in top left of screen based on any new data

        // if Sylvan flashes red, reset related values
        if (sylvan.flashRed) {
            redTimer = 0;
            sylvan.flashRed = false;
        }
        redTimer+=delta;

    }

    public void unpossess() {

        Vector2 pos = currentInhabitedEntity.getBody().getPosition(); // save position of current entity

        // enemy dies upon unpossess if it's a Bat or Spider
        if (currentInhabitedEntity.getBody().getUserData() != "rock") {
            currentInhabitedEntity.die();
        }

        changeCurrentInhabitedEntity(sylvan); // change back to Sylvan
        sylvan.body.setLinearVelocity(0, 0); // reset his velocity; otherwise next two lines won't work exactly as intended b/c he is not going in with 0 velocity
        sylvan.body.setTransform(pos.x, pos.y + 0.5f, 0); // set Sylvan's position to the enemy's position plus a bit higher
        sylvan.body.applyForceToCenter(0, 0.35f, true); // Sylvan jumps a little

        // reset possess-related timers
        possessTimer = 0;
        possessCooldown = 1;

    }

    @Override
    public void render(float delta) { // called in SylvanGame.render()

        processPause(); // check if user has pressed pause

        if (!pause) {

            // resume any possibly paused long sounds
            if (sylvan.possessed) {
                sounds.get("walk").resume();
                sounds.get("glide").resume();
            } else if (currentInhabitedEntity.body.getUserData() == "spider") {
                sounds.get("skitter").resume();
            }

            music.setVolume(0.38f); // change music volume back to normal
            update(delta); // only update level if level is not paused

        } else { // level is paused

            // pause any possible long sounds
            sounds.get("walk").pause();
            sounds.get("glide").pause();
            sounds.get("skitter").pause();

            music.setVolume(0.2f); // music temporarily

        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // clear screen
        mapRenderer.render(); // render level
        camera.position.set(currentInhabitedEntity.getBody().getPosition().x, currentInhabitedEntity.getBody().getPosition().y, 0); // set camera pos to player

        // render debug boxes
        //debugMatrix = game.batch.getProjectionMatrix().cpy().scale(viewport.getScreenWidth(), viewport.getScreenHeight(), 0);
        //debugRenderer.render(world, camera.combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (sylvan.possessed) { // only draw sylvan if possessed
            if (sylvan.shouldDraw) {sylvan.draw(game.batch);} // draw Sylvan if alive
            if (redTimer <= 0.5) { // Sylvan flashes red if he was recently damaged
                sylvan.setColor(new Color( 1, 0.5f,0.5f,1));
            } else {
                sylvan.setColor(Color.WHITE); // back to regular color
            }
        }

        // draw the enemies
        int iter = 0;
        for (Entity enemy : enemies) {
            enemy.draw(game.batch);
            if (distances.get(iter) <= 1.5) {
                enemy.possessIndicator.draw(game.batch);
            }
            iter++;
        }

        // draw the tokens
        for (Token token : tokens) {
            token.draw(game.batch);
        }

        indicator.draw(game.batch); // draw the arrow indicator above player

        game.batch.end();

        // DRAW HUD
        game.batch.setProjectionMatrix(infoDisplay.stage.getCamera().combined);
        infoDisplay.stage.draw();
        game.batch.begin();
        game.batch.draw(infoDisplay.tokenTexture,0,460);
        int drawX = 60;
        for (int i = 0; i < sylvan.health; i++) {
            game.batch.draw(infoDisplay.heartTexture,drawX,460);
            drawX += 20;
        }
        game.batch.end();

        if (shouldPossess()) { // render blue line if Sylvan possesses an enemy
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.rectLine(sylvan.body.getPosition(), targetEntity.body.getPosition(), 0.05f);
            System.out.println(shapeRenderer.isDrawing());
            shapeRenderer.end();
        }

        if (pause) { // draw pause overlay if level is paused
            Gdx.gl.glEnable(GL20.GL_BLEND); // allow Color to tint the entire screen
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            Color pauseColor = new Color(0, 0, 0, 0.7f);
            shapeRenderer.setColor(pauseColor);
            shapeRenderer.rect(-100, -100, 1000, 1000); // make sure it covers everything
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            game.batch.begin();
            game.batch.setProjectionMatrix(textCam.combined);
            game.font.getData().setScale(2f);
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, "Press Enter to restart the level", -170, 3);
            game.font.draw(game.batch, "Press R to restart the game", -170, -30);
            game.batch.end();
        }

    }

    public boolean shouldPossess() {
        final boolean hasTarget = (targetEntity != null);
        final boolean animationDone = (sylvan.stateTimer >= 0.4f);
        if (hasTarget && animationDone) { // Sylvan can possess if his animation has played fully and he has a valid target
            return true;
        }
        return false;
    }

    public void possess() { // called when possess goes through
        changeCurrentInhabitedEntity(targetEntity); // Sylvan possesses the target Entity
        sylvan.body.setTransform(disappearPos, 0); // Sylvan's body sent off the level
        targetEntity = null; // reset the target
        sylvan.resetState(); // set Sylvan's state back to IDLE from POSSESS
        possessSound.play(0.5f);
    }

    public void getPossessTarget() { // for when player presses possess key
        // using a key to possess means he possesses the closest enemy within range

        if (sylvan.possessed && possessCooldown <= 0) { // if sylvan is currently not possessing anyone and it has been long enough since he last did

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
                targetEntity = enemies.get(idx); // if so, set the targetEntity
                // this changes outcome of the shouldPossess() check
            }

        }

    }

    public void getTargetFromClick(Entity target) { // slightly different method for when player clicks to possess enemy
        // using click means he possesses whichever enemy was clicked if within range
        // this time the target is passed in so we already know who it is. thus the code is simpler here

        double distance = getDistance(sylvan.body.getPosition(),target.body.getPosition());

        if (sylvan.possessed && possessCooldown <= 0 && distance <= 1.5) {
            sylvan.currentState = Entity.State.POSSESS;
            sylvan.stateTimer = 0;
            targetEntity = target;
        }

    }

    public boolean endLevel() { // true if Sylvan has died
        if (sylvan.currentState == Entity.State.DEAD && sylvan.stateTimer > 1.5) {
            return true;
        }
        return false;
    }

    public boolean completeLevel() { // true if Sylvan has collected every token
        if (numTokensCollected == TOKEN_COUNT) {
            return true;
        }
        return false;
    }

    public void getToken(int idx) { // collect token
        Token token = tokens.get(idx); // figure out which token was collected based on index passed from contact listener
        token.shouldCollect = true; // mark token to be collected (body can't be removed here without error from world)
    }

    public double getDistance(Vector2 entity1, Vector2 entity2) { // get distance between two entities
        return Math.sqrt(Math.pow((entity2.x - entity1.x), 2) + Math.pow((entity2.y - entity1.y), 2));
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void resize(int width, int height) { // handles window resize
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() { // not working
        System.out.println("dispose");

        map.dispose();
        mapRenderer.dispose();
        if (!world.isLocked()) {world.dispose();}
        infoDisplay.dispose();
        music.dispose();

        for (String sound : sounds.keySet()) {
            sounds.get(sound).dispose();
        }

    }

    @Override
    public void show() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}