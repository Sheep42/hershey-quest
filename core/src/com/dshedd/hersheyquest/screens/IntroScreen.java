package com.dshedd.hersheyquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dshedd.hersheyquest.HersheyQuest;

public class IntroScreen extends AbstractScreen {
	SpriteBatch batch;
	TextureRegion introScreen;
	HersheyQuest game;
	
	float time = 0;
	
	public IntroScreen(HersheyQuest game) {
		super(game);
		this.game = game;
	}

	@Override
	public void show() {
		introScreen = new TextureRegion();
		batch = new SpriteBatch();
		
		introScreen.setTexture(new Texture(Gdx.files.internal("badlogic.jpg")));
		introScreen.setRegion(0, 0, 256, 256);
		
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 960, 680);
	}
	
	@Override
	public void render(float delta) {
		//Wipe the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
			batch.draw(introScreen, (Gdx.graphics.getWidth() / 2) - (introScreen.getRegionWidth() / 2), (Gdx.graphics.getHeight() / 2) - (introScreen.getRegionHeight() / 2));
			//batch.draw(introScreen, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		batch.end();
		
		time += delta;
		if(time > 1) {
			if(Gdx.input.isKeyPressed(Keys.ANY_KEY) || Gdx.input.justTouched()) {
				game.setScreen(new GameScreen(game));
			}
		}
	}

	@Override
	public void hide(){
		batch.dispose();
		introScreen.getTexture().dispose();
	}
}
