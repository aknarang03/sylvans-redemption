package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Entities.Bat;
import com.mygdx.game.Entities.Rock;
import com.mygdx.game.Entities.Spider;
import com.mygdx.game.Entities.Token;

import java.util.HashMap;

public class SylvanGame extends Game {

	public HashMap<String, Sound> uiSounds = new HashMap();
	Sound startGameSound;
	Sound startGameOverlaySound;
	Sound completedLevelSound;
	Sound startLevelSound;
	Sound pauseSound;
	Sound selectSound;
	Music forestMusic;
	Music caveMusic;
	Music mainMenuMusic;

	public BitmapFont font;

	public static final float PPM = 64; // Pixels Per Meter

	// initial screen width and height upon run
	public static final int SCREEN_WIDTH = 400;
	public static final int SCREEN_HEIGHT = 300;

	// collision groups (currently not using)
	public static final short PLAYER_GROUP = 1;
	public static final short ENEMY_GROUP = 2;
	public static final short GROUND_GROUP = 3;
	public static final short MOVABLE_GROUP = 4;
	public static final short TOKEN_GROUP = 5;

	public SpriteBatch batch;

	public Level currentLevel; // keep track of current level

	// Levels
	//public Level level;

	public MainMenu mainMenu;
	public ControlsMenu controlsMenu;
	public GameOverScreen gameOver;
	public LevelWinScreen levelWin;
	public HowToPlayScreen howToPlay;
	public StoryScreen story;
	public GameCompleteScreen gameComplete;

	@Override
	public void create () {
		initSounds();
		batch = new SpriteBatch();
		font = new BitmapFont();
		//createLevel0();
		createLevel1();
		//createLevel2();
		initScreens();
		setScreen(mainMenu);
		mainMenuMusic.play();
	}

	public void initScreens() {
		mainMenu = new MainMenu(this);
		levelWin = new LevelWinScreen(this);
		gameOver = new GameOverScreen(this);
		controlsMenu = new ControlsMenu(this);
		howToPlay = new HowToPlayScreen(this);
		story = new StoryScreen(this);
		gameComplete = new GameCompleteScreen(this);
	}

	public void initSounds() {

		// I should change it to just fully declare Sounds in here since I'm adding them to the map anyway
		// would reduce the amt of variables above
		// do that for the level sounds too

		startGameSound = Gdx.audio.newSound(Gdx.files.internal("sounds/game_start.mp3"));
		startGameOverlaySound = Gdx.audio.newSound(Gdx.files.internal("sounds/game_start_overlay.mp3"));
		completedLevelSound = Gdx.audio.newSound(Gdx.files.internal("sounds/completed_level.mp3"));
		startLevelSound = Gdx.audio.newSound(Gdx.files.internal("sounds/level_start.mp3"));
		pauseSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pause.mp3"));
		selectSound = Gdx.audio.newSound(Gdx.files.internal("sounds/select.mp3"));

		uiSounds.put("start game", startGameSound);
		uiSounds.put("start game overlay", startGameOverlaySound);
		uiSounds.put("completed level", completedLevelSound);
		uiSounds.put("start level", startLevelSound);
		uiSounds.put("pause", pauseSound);
		uiSounds.put("select", selectSound);

		forestMusic = Gdx.audio.newMusic(Gdx.files.internal("music/forest_music.ogg"));
		caveMusic = Gdx.audio.newMusic(Gdx.files.internal("music/cave_music.ogg"));
		mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/title_screen_music.mp3"));
		mainMenuMusic.setVolume(0.5f);

	}

	public void pickLevel(Level level) { // change currentLevel
		currentLevel.music.stop();
		mainMenuMusic.stop();
		setCurrentLevel(level);
		level.createEntities();
	}

	public void nextLevel(int currentLevelID) {
		currentLevel.music.stop();
		currentLevel.dispose();
		switch (currentLevelID) {
			case 0:
				createLevel1();
				break;
			case 1:
				createLevel2();
				break;
		}
		pickLevel(currentLevel);
	}

	public void restartLevel(int id) {
		currentLevel.dispose();
		switch (id) {
			case 0:
				createLevel0();
				break;
			case 1:
				createLevel1();
				break;
			case 2:
				createLevel2();
				break;
		}
		pickLevel(currentLevel);
	}

	public void restartGame() {
		currentLevel.music.stop();
		currentLevel.dispose();
		setScreen(mainMenu);
		mainMenuMusic.play();
		createLevel1();
	}

	public void createLevel0() {
		// PROTOTYPE LEVEL

		final int numEnemies = 3;
		final int numTokens = 2;
		final int id = 0;

		Bat bat = new Bat(this,new Vector2(5,3));
		Spider spider = new Spider(this,new Vector2(2.4f,2.5f));
		Rock rock = new Rock(this,new Vector2(1,1));

		Array<Entity> prototypeEnemies = new Array<Entity>(numEnemies);
		prototypeEnemies.add(bat,spider,rock);

		Token token1 = new Token(this,new Vector2(6,4));
		Token token2 = new Token(this,new Vector2(5,4));

		Array<Token> prototypeTokens = new Array<Token>();
		prototypeTokens.add(token1, token2);

		String prototypeMapFilename = "PrototypeLevelMap.tmx";

		currentLevel = new Level(this, prototypeEnemies, prototypeTokens, prototypeMapFilename, numTokens, id, forestMusic);
	}

	public void createLevel1() {

		final int numEnemies = 5;
		final int numTokens = 4;
		final int id = 1;

		Bat bat1 = new Bat(this,new Vector2(4,6.5f));
		Spider spider1 = new Spider(this,new Vector2(4,1));
		Bat bat2 = new Bat(this,new Vector2(16.6f,2));
		Spider spider2 = new Spider(this,new Vector2(19.8f,1));
		Bat bat3 = new Bat(this,new Vector2(20f,4));

		Array<Entity> lvl1enemies = new Array<Entity>(numEnemies);
		lvl1enemies.add(bat1,spider1,bat2,spider2);
		lvl1enemies.add(bat3);

		Token token1 = new Token(this,new Vector2(2,7));
		Token token2 = new Token(this,new Vector2(13.25f,8.9f));
		Token token3 = new Token(this,new Vector2(22.1f,1.1f));
		Token token4 = new Token(this,new Vector2(21f,6.3f));

		Array<Token> lvl1tokens = new Array<Token>();
		lvl1tokens.add(token1,token2,token3,token4);

		String mapFilename = "SRLvl1.tmx";

		currentLevel = new Level(this, lvl1enemies, lvl1tokens, mapFilename, numTokens, id, forestMusic);
	}

	public void createLevel2() {

		final int numEnemies = 7;
		final int numTokens = 4;
		final int id = 2;

		Spider spider1 = new Spider(this,new Vector2(4,3));
		Rock rock1 = new Rock(this, new Vector2(6,8));
		Rock rock2 = new Rock(this, new Vector2(1.5f,8));
		Spider spider2 = new Spider(this,new Vector2(13.83f,5.6f));
		Bat bat1 = new Bat(this,new Vector2(19.16f,6.9f));
		Bat bat2 = new Bat(this,new Vector2(17,2));
		Rock rock3 = new Rock(this,new Vector2(24.65f,2));

		Array<Entity> lvl2enemies = new Array<Entity>(numEnemies);
		lvl2enemies.add(spider1,rock1,rock2,spider2);
		lvl2enemies.add(bat1,bat2,rock3);

		Token token1 = new Token(this,new Vector2(0.75f,5.9f));
		Token token2 = new Token(this,new Vector2(15,7.07f));
		Token token3 = new Token(this,new Vector2(21.03f,8.4f));
		Token token4 = new Token(this,new Vector2(24.15f,2));

		Array<Token> lvl2tokens = new Array<Token>();
		lvl2tokens.add(token1,token2,token3,token4);

		String mapFilename = "Level2Map..tmx";

		currentLevel = new Level(this, lvl2enemies, lvl2tokens, mapFilename, numTokens, id, caveMusic);

	}

	public void setCurrentLevel(Level level) {
		if (currentLevel != null) {
			currentLevel.dispose();
		}
		this.currentLevel = level;
		this.setScreen(level);
		currentLevel.music.setVolume(0.5f);
		currentLevel.music.setLooping(true);
		currentLevel.music.play();
	}

	@Override
	public void render () { super.render(); } // calls current screen's (Level's) render method
	
	@Override
	public void dispose () { // IMPLEMENT
		batch.dispose();
		// dispose of textures too?
	}

}
