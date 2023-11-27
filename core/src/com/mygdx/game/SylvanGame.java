package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Entities.Sylvan;

public class SylvanGame extends Game {

	public static final float PPM = 64; // Pixels Per Meter
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

	@Override
	public void create () {
		batch = new SpriteBatch();
		// CONSTRUCT LEVELS
		createLevels();
		setCurrentLevel(prototypeLevel);
		Sylvan sylvan = new Sylvan(this); // body isn't showing up, unsure if position is off or if body construction isn't working
	}

	public void createLevels() {

		int numEnemies;

		// PROTOTYPE LEVEL
		numEnemies = 2;
		Array<Entity> enemies = new Array<Entity>(numEnemies);
		String prototypeMapFilename = "PrototypeLevelMap.tmx";
		prototypeLevel = new Level(this, enemies,prototypeMapFilename);

	}

	public void setCurrentLevel(Level level) {
		this.currentLevel = level;
		this.setScreen(level);
	}

	public Entity getCurrentInhabitedEntity() {
		// this will be used to set camera to position
		return currentInhabitedEntity;
	}

	public SylvanGame getGame() {
		return this;
	}

	@Override
	public void render () {

		Gdx.gl.glClearColor(0.8f, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float dt = Gdx.graphics.getDeltaTime();

		/*Every frame:
		* Process input (processInput()) -> acts on currentInhabitedEntity
		* Update entities -> for each entity that isnt inhabited or player, update it
		* Resolve any collisions -> box2d, maybe call this in level? depends how you structure it
		* Draw -> self explanatory*/

		// render player movement in here, then call:
		if (currentLevel!= null) { currentLevel.render(dt); }
		else { System.out.println("NULL");} // does the same as super.render()
		//super.render(); // calls current screen's (Level's) render method
		// (if that doesn't work for some reason, level will handle player movement instead)
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		// dispose of textures?
	}
}
