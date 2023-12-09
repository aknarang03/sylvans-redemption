package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Entities.Bat;
import com.mygdx.game.Entities.Rock;
import com.mygdx.game.Entities.Spider;
import com.mygdx.game.Entities.Sylvan;
import com.mygdx.game.Entities.Token;

public class SylvanGame extends Game {

	public static final float PPM = 64; // Pixels Per Meter

	// initial screen width and height upon run
	public static final int SCREEN_WIDTH = 400;
	public static final int SCREEN_HEIGHT = 208;

	// collision groups (currently not using)
	public static final short PLAYER_GROUP = 1;
	public static final short ENEMY_GROUP = 2;
	public static final short GROUND_GROUP = 3;
	public static final short MOVABLE_GROUP = 4;
	public static final short TOKEN_GROUP = 5;

	public SpriteBatch batch;

	public Level currentLevel; // keep track of current level

	// Levels
	Level prototypeLevel;

	@Override
	public void create () {
		batch = new SpriteBatch();
		createLevels(); // construct levels
		pickLevel(prototypeLevel);
	}

	public void pickLevel(Level level) { // change currentLevel
		setCurrentLevel(level);
		level.createEntities();
	}

	public void createLevels() {
		// for now this is where every level is created
		// perhaps will save them in an array / map or something so that it's easier to switch levels
		int numEnemies;
		int numTokens;

		// PROTOTYPE LEVEL

		numEnemies = 3;
		numTokens = 3;

		Bat bat = new Bat(this,new Vector2(5,1));
		Spider spider = new Spider(this,new Vector2(2.4f,2.5f));
		Rock rock = new Rock(this,new Vector2(1,1));

		Array<Entity> prototypeEnemies = new Array<Entity>(numEnemies);
		prototypeEnemies.add(bat,spider,rock);

		Token token1 = new Token(this,new Vector2(6,4));
		Token token2 = new Token(this,new Vector2(5,4));

		Array<Token> prototypeTokens = new Array<Token>();
		prototypeTokens.add(token1, token2);

		String prototypeMapFilename = "PrototypeLevelMap.tmx";

		prototypeLevel = new Level(this, prototypeEnemies, prototypeTokens, prototypeMapFilename, numTokens);

	}

	public void setCurrentLevel(Level level) {
		this.currentLevel = level;
		this.setScreen(level);
	}

	@Override
	public void render () { super.render(); } // calls current screen's (Level's) render method
	
	@Override
	public void dispose () { // IMPLEMENT
		batch.dispose();
		// dispose of textures too?
	}

}
