package com.dshedd.hersheyquest;

import com.badlogic.gdx.Game;
import com.dshedd.hersheyquest.screens.IntroScreen;

public class HersheyQuest extends Game {
	
	private boolean paused = false;
	
	@Override
	public void create () {
		setScreen(new IntroScreen(this));
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
}
