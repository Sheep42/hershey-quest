package com.dshedd.hersheyquest.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.dshedd.hersheyquest.entities.Enemy;
import com.dshedd.hersheyquest.entities.Hershey;

public class World {
	
	private Hershey hershey;
	private Enemy enemy;
	private OrthographicCamera cam;
	private Vector3 touchPos = new Vector3(0, 0, 0);
	private SpriteBatch batch;
	
	public World() {
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		hershey = new Hershey(0, 0, cam);
		enemy = new Enemy(100, 100, hershey);
		
		batch = new SpriteBatch();
	}
	
	public void update(float delta){
		hershey.update(delta);
		enemy.update(delta);
	}
	
	public void render(float delta) {
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		
		batch.begin();
			//Draw Hershey
			batch.draw(hershey.getTextureRegion(), hershey.getPos().x, hershey.getPos().y, 0, 0, Hershey.WIDTH, Hershey.HEIGHT, 1, 1, 0);
			
			//Draw the enemy
			batch.draw(enemy.getTextureRegion(), enemy.getPos().x, enemy.getPos().y, 0, 0, Enemy.WIDTH, Enemy.HEIGHT, 1, 1, 0);
		batch.end();
	}
	
	public void dispose() {
		
	}

	public Hershey getHershey() {
		return hershey;
	}

	public void setHershey(Hershey hershey) {
		this.hershey = hershey;
	}
}
