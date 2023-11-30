package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Entities.Sylvan;

import java.awt.Color;

public class SylvanGame extends Game {

	public static final float PPM = 64; // Pixels Per Meter

	// initial screen width and height upon run
	public static final int SCREEN_WIDTH = 400;
	public static final int SCREEN_HEIGHT = 208;

	// collision groups
	public static final short PLAYER_GROUP = 1;
	public static final short ENEMY_GROUP = 2;
	public static final short GROUND_GROUP = 3;
	public static final short MOVABLE_GROUP = 4;

	public SpriteBatch batch;

	Level currentLevel; // keep track of current level
	Entity currentInhabitedEntity; // keep track of who player is possessing

	// Levels
	Level prototypeLevel;

	Sylvan sylvan; // enemies will be in Level, maybe Sylvan should also be?? probably

	@Override
	public void create () {
		batch = new SpriteBatch();
		// CONSTRUCT LEVELS
		createLevels();
		pickLevel(prototypeLevel);
	}

	public void pickLevel(Level level) {
		setCurrentLevel(level);
		level.createEntityBodies();
	}

	public void createLevels() {

		// this seems messy?? but for now this is where every level is created
		// perhaps will save them in an array so that it's easier to switch levels

		int numEnemies;

		// PROTOTYPE LEVEL
		numEnemies = 2;
		Array<Entity> enemies = new Array<Entity>(numEnemies);
		String prototypeMapFilename = "PrototypeLevelMap.tmx";
		String backgroundImgFilename = "..."; // PUT A FILE
		prototypeLevel = new Level(this, enemies,prototypeMapFilename, backgroundImgFilename);
		createEntities(currentLevel, enemies); // currently the parameters do nothing

	}

	// BODIES ARE CREATED IN LEVEL CLASS
	public void createEntities(Level currentLevel, Array<Entity> enemies) { // this has to be called after the world is created, otherwise it won't work
		// this should probably somehow be moved to level..

		Vector2 sylvanPos = new Vector2(1,1.7f);
		sylvan = new Sylvan(this,sylvanPos);
		sylvan.setPosition(1,1.7f);
		changeCurrentInhabitedEntity(sylvan); // on level creation

	}

	public void setCurrentLevel(Level level) {
		this.currentLevel = level;
		this.setScreen(level);
	}

	public void changeCurrentInhabitedEntity(Entity entity) {
		if (currentInhabitedEntity!=null) {currentInhabitedEntity.possessed = false; }
		currentInhabitedEntity = entity;
		entity.possessed = true;
	}

	public Entity getCurrentInhabitedEntity() {
		// this will be used to set camera to position (since it shouldn't center on Sylvan if player is now ex. a Bat)
		return currentInhabitedEntity;
	}

	public SylvanGame getGame() {
		return this;
	} // return a reference to itself

	public void processInput() {

		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
			currentInhabitedEntity.move(Control.RIGHT);
		} if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
			currentInhabitedEntity.move(Control.LEFT);
		} if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			currentInhabitedEntity.move(Control.UP);
		}

	}

	@Override
	public void render () {

		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float dt = Gdx.graphics.getDeltaTime();

		System.out.println("Sprite: " + sylvan.getX());
		System.out.println("Body: " + sylvan.body.getPosition().x);

		sylvan.setPosition((sylvan.body.getPosition().x * SylvanGame.PPM) - sylvan.getWidth() / 2,
				(sylvan.body.getPosition().y * SylvanGame.PPM) - sylvan.getHeight() / 2);

		/*Every frame:
		* Process input (processInput()) -> acts on currentInhabitedEntity
		* Update entities -> for each entity that isnt inhabited or player, update it
		* Resolve any collisions -> box2d, maybe call this in level? depends how you structure it
		* Draw -> self explanatory*/

		processInput();
		currentInhabitedEntity.updateFrame(currentInhabitedEntity.getStateTimer(),dt);
		batch.begin();
		currentInhabitedEntity.draw(batch);
		batch.end();

		// render player movement in here, then call:
		if (currentLevel!= null) { currentLevel.render(dt); } // does the same as super.render()
		else { System.out.println("LEVEL NULL");}

		//super.render(); // calls current screen's (Level's) render method // for now doing the above instead since you need to send in dt
		// (if this doesn't work for some reason, level will handle player movement instead and this will only call super.render())
		//System.out.println(currentInhabitedEntity.body.getPosition());

	}
	
	@Override
	public void dispose () { // IMPLEMENT
		batch.dispose();
		// dispose of textures too?
	}
}
