package com.dshedd.hersheyquest;

import com.badlogic.gdx.Game;
import com.dshedd.hersheyquest.screens.IntroScreen;

public class HersheyQuest extends Game {
	
	public static boolean paused = false;
	
	@Override
	public void create () {
		setScreen(new IntroScreen(this));
	}	
}
