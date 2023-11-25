package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;


// structure right now might be messed up
public class SylvansRedemption extends Game {
	SpriteBatch batch;
	Texture img;

	Level currentLevel;
	Entity currentInhabitedEntity;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		// CONSTRUCT LEVELS
		//img = new Texture("badlogic.jpg");
	}

	// don't know yet how level rendering will work..
	// level has a render method but how do I make it so that only the current one renders
	@Override
	public void render () {
		/*Every frame:
		* Process input (processInput()) -> acts on currentInhabitedEntity
		* Update entities -> for each entity that isnt inhabited or player, update it
		* Resolve any collisions -> box2d, maybe call this in level? depends how you structure it
		* Draw -> self explanatory*/
		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
