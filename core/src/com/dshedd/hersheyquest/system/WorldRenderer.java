package com.dshedd.hersheyquest.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.dshedd.hersheyquest.entities.Enemy;
import com.dshedd.hersheyquest.entities.Hershey;

public class WorldRenderer {
	OrthographicCamera cam;
	Hershey hershey;
	Enemy enemy;
	
	public WorldRenderer(World world) {
		this.hershey = world.getHershey();
		this.enemy = world.enemy;
		
		this.cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.cam.position.set(hershey.getPos().x, hershey.getPos().y, 0);
	}
	
	public void render(float delta) {
		hershey.render(delta);
		enemy.render(delta);
	}
	
	public void dispose() {
		
	}
}
