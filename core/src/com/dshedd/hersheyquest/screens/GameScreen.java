package com.dshedd.hersheyquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.dshedd.hersheyquest.HersheyQuest;
import com.dshedd.hersheyquest.entities.Enemy;
import com.dshedd.hersheyquest.entities.Hershey;
import com.dshedd.hersheyquest.system.World;

public class GameScreen extends AbstractScreen{

	HersheyQuest game;
	protected World world;

	public GameScreen(HersheyQuest game) {
		super(game);
		this.game = game;
		world = new World();
	}
	
	@Override
	public void show() {

	}
	
	@Override
	public void render(float delta) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(!game.isPaused()) {
			world.update(delta);
		}
		
		world.render(delta);
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			if(game.isPaused())
				game.setPaused(false);
			else
				game.setPaused(true);
		}
	}
	
	@Override
	public void hide() {
		
	}

}
