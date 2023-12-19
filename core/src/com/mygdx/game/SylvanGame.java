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

/*
Anjali Narang
Aaila Arif
Jenna Esposito
 */

public class SylvanGame extends Game {

	// SOUND VARS
	public HashMap<String, Sound> uiSounds = new HashMap();
	private Sound startGameSound;
	private Sound startGameOverlaySound;
	private Sound completedLevelSound;
	private Sound startLevelSound;
	private Sound pauseSound;
	private Sound selectSound;

	// MUSIC VARS
	Music forestMusic;
	Music caveMusic;
	Music mainMenuMusic;

	public SpriteBatch batch;
	public BitmapFont font; // font to draw text throughout project

	public static final float PPM = 64; // Pixels Per Meter

	// initial screen width and height
	public static final int SCREEN_WIDTH = 400;
	public static final int SCREEN_HEIGHT = 300;

	public Level currentLevel; // keep track of current level

	// screens
	public MainMenu mainMenu;
	public ControlsMenu controlsMenu;
	public GameOverScreen gameOver;
	public LevelWinScreen levelWin;
	public HowToPlayScreen howToPlay;
	public StoryScreen story;
	public GameCompleteScreen gameComplete;

	@Override
	public void create () {

		initSounds(); // set up sounds map

		batch = new SpriteBatch();
		font = new BitmapFont();

		createLevel1(); // creates level 1 and sets it to current level
		initScreens(); // creates any screens that only need to be created once

		setScreen(mainMenu); // begin at main menu
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

	// SEE IF COMMENTING OUT MUSIC DOES NOTHING
	// combine pick level and set screen to level

	public void pickLevel(Level level) {
		currentLevel.music.stop();
		mainMenuMusic.stop();
		setScreenToLevel(level); // change currentLevel to passed in level
		level.createEntities(); // set up level
	}

	public void setScreenToLevel(Level level) {
		this.setScreen(level); // set screen to passed in level
		// init music
		currentLevel.music.setVolume(0.5f);
		currentLevel.music.setLooping(true);
		currentLevel.music.play();
	}

	public void nextLevel(int currentLevelID) {
		// current level becomes next level based on current id
		currentLevel.dispose();
		switch (currentLevelID) {
			case 0:
				createLevel1();
				break;
			case 1:
				createLevel2();
				break;
		}
		pickLevel(currentLevel); //
	}

	public void restartLevel(int id) {
		currentLevel.dispose();
		switch (id) {
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
		currentLevel.dispose();
		setScreen(mainMenu);
		mainMenuMusic.play();
		createLevel1();
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

	@Override
	public void render () { super.render(); } // calls current screen's (Level's) render method
	
	@Override
	public void dispose () {
		batch.dispose();
	}

}
