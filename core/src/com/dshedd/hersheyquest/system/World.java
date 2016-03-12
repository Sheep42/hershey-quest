package com.dshedd.hersheyquest.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.dshedd.hersheyquest.entities.Enemy;
import com.dshedd.hersheyquest.entities.Hershey;

public class World {
	
	private Hershey hershey;
	private Enemy enemy;
	private NervousBar nervousBar;
	private OrthographicCamera cam;
	private Vector3 touchPos = new Vector3(0, 0, 0);
	private SpriteBatch batch;
	private BitmapFont clockText = new BitmapFont();
	private float elapsed = 0, touchTime = 0;
	
	public World() {
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		hershey = new Hershey(0, 0, cam);
		enemy = new Enemy(100, 100, hershey);
		
		nervousBar = new NervousBar(hershey.getPos().x, hershey.getPos().y);
		
		batch = new SpriteBatch();
	}
	
	public void update(float delta){
		elapsed += delta;
		
		hershey.update(delta);
		enemy.update(delta);
		
		checkCollisions(delta);
	}
	
	public void render(float delta) {
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		
		batch.begin();
			//Draw Hershey
			batch.draw(hershey.getTextureRegion(), hershey.getPos().x, hershey.getPos().y, 0, 0, Hershey.WIDTH, Hershey.HEIGHT, 1, 1, 0);
			
			//Draw the enemy
			batch.draw(enemy.getTextureRegion(), enemy.getPos().x, enemy.getPos().y, 0, 0, Enemy.WIDTH, Enemy.HEIGHT, 1, 1, 0);
			
			//Draw the clock
			String time = (Math.round(elapsed) < 10) ? "0" + Math.round(elapsed) : Integer.toString(Math.round(elapsed)); 
			clockText.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			clockText.draw(batch, time, 0, Gdx.graphics.getHeight() / 2 - clockText.getLineHeight());
		batch.end();
	}
	
	public void dispose() {
		
	}

	private void checkCollisions(float delta) {
		if(hershey.getBounds().overlaps(enemy.getBounds())) {
			touchTime += delta;
			
			if(touchTime >= 0.3){
				if(hershey.getNervousPercent() < .99)
					hershey.setNervousPercent(hershey.getNervousPercent() + 0.02f);
				
				System.out.println(Math.round(hershey.getNervousPercent() * 100) + "%");
				touchTime = 0;
			}
		} else {
			touchTime = 0;
		}
	}
	
	//Getters/Setters
	public Hershey getHershey() {
		return hershey;
	}

	public void setHershey(Hershey hershey) {
		this.hershey = hershey;
	}
}
