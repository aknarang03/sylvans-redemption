package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ControlsMenu implements Screen {

    private SylvanGame game;
    private OrthographicCamera camera;

    Sprite menuSprite;

    public ControlsMenu(SylvanGame game) {

        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        createBg();

    }
    public void createBg() {
        Texture menuImg = new Texture(Gdx.files.internal("badlogic.jpg"));
        menuSprite = new Sprite(menuImg);
        menuSprite.setSize(game.SCREEN_WIDTH,game.SCREEN_HEIGHT);
        menuSprite.setPosition(0,0);
    }

    @Override
    public void show() {

    }

    public void renderText() {



    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Control input = processInput();

        game.batch.begin();

        //menuSprite.draw(game.batch);

        // ALSO PUT HOW TO PLAY TEXT IN HERE
        // OR DO ANOTHER SCREEN FOR THAT ACCESSIBLE FROM MAIN MENU

        game.font.getData().setScale(1.3f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch,"Controls",170,200);
        game.font.getData().setScale(1.1f);
        game.font.draw(game.batch, "Jump: UP/W",150,160);
        game.font.draw(game.batch, "Left: LEFT/A",150,140);
        game.font.draw(game.batch, "Right: RIGHT/D",150,120);
        game.font.draw(game.batch, "Pause: ESC",150,100);
        game.font.draw(game.batch, "Select: ENTER",150,80);
        game.font.draw(game.batch, "Press ENTER to return to Main Menu",80,30);

        game.batch.end();

        if (input == Control.SELECT) {
            // GO BACK TO MAIN MENU
            game.setScreen(game.mainMenu);
            game.uiSounds.get("select").play(1);
        }

    }

    public Control processInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            return Control.SELECT;
        }
        return null;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
