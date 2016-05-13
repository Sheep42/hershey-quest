package com.dshedd.hersheyquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dshedd.hersheyquest.screens.IntroScreen;

public class HersheyQuest extends Game {
	
	public static boolean paused = false;
	public int screenWidth = 960, screenHeight = 640;
	
	private Viewport viewport;
	
	@Override
	public void create () {
		setScreen(new IntroScreen(this));
	}
	
	@Override
	public void resize(int width, int height){
		viewport.update(width, height);
	}

	public Viewport getViewport() {
		return viewport;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}	
}
