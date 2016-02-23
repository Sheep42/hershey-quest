package com.dshedd.hersheyquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.dshedd.hersheyquest.HersheyQuest;
import com.dshedd.hersheyquest.entities.Hershey;
import com.dshedd.hersheyquest.system.World;
import com.dshedd.hersheyquest.system.WorldRenderer;

public class GameScreen extends AbstractScreen{

	HersheyQuest game;
	World world;
	WorldRenderer worldRenderer;
	Hershey h;

	public GameScreen(HersheyQuest game) {
		super(game);
		this.game = game;
	}
	
	@Override
	public void show() {
		world = new World();
		worldRenderer = new WorldRenderer(world);
	}
	
	@Override
	public void render(float delta) {
		System.out.println("Game...");
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		world.update(delta);
		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		worldRenderer.render(delta);
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			if(game.isPaused())
				game.setPaused(false);
			else
				game.setPaused(true);
		}
		
		if(game.isPaused()) {
			System.out.println("Paused...");
		}
	}
	
	@Override
	public void hide() {
		world.dispose();
		worldRenderer.dispose();
	}

}
