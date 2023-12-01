package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

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

	public Level currentLevel; // keep track of current level

	// Levels
	Level prototypeLevel;

	@Override
	public void create () {
		batch = new SpriteBatch();

		// CONSTRUCT LEVELS
		createLevels();

		pickLevel(prototypeLevel);
	}

	public void pickLevel(Level level) {
		setCurrentLevel(level);
		level.createEntities();
	}

	public void createLevels() {

		// this seems messy?? but for now this is where every level is created
		// perhaps will save them in an array so that it's easier to switch levels

		int numEnemies;

		// PROTOTYPE LEVEL
		numEnemies = 2;
		Array<Entity> prototypeEnemies = new Array<Entity>(numEnemies);
		// put enemies in array
		String prototypeMapFilename = "PrototypeLevelMap.tmx";
		String backgroundImgFilename = "..."; // PUT A FILE
		prototypeLevel = new Level(this, prototypeEnemies, prototypeMapFilename, backgroundImgFilename);

	}

	public void setCurrentLevel(Level level) {
		this.currentLevel = level;
		this.setScreen(level);
	}

	public SylvanGame getGame() {
		return this;
	} // return a reference to itself

	@Override
	public void render () {
		super.render(); // calls current screen's (Level's) render method
	}
	
	@Override
	public void dispose () { // IMPLEMENT
		batch.dispose();
		// dispose of textures too?
	}
}
