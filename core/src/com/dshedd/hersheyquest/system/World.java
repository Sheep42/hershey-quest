package com.dshedd.hersheyquest.system;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.dshedd.hersheyquest.entities.Enemy;
import com.dshedd.hersheyquest.entities.Hershey;

public class World {
	
	private Hershey hershey;
	private NervousBar nervousBar;
	private OrthographicCamera cam;
	private Vector3 touchPos = new Vector3(0, 0, 0);
	private SpriteBatch batch;
	private BitmapFont clockText = new BitmapFont();
	
	protected TiledMap map;
	protected OrthogonalTiledMapRenderer renderer;
	protected TiledMapTileLayer collisionLayer;
	protected Array<Enemy> enemies = new Array<Enemy>();
	protected MapProperties mapInfo;
	
	private float elapsed = 0, touchTime = 0;
	
	public World() {
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		map = new TmxMapLoader().load("map.tmx");
		mapInfo = map.getProperties();
		
		renderer = new OrthogonalTiledMapRenderer(map);
		
		MapLayer entities = map.getLayers().get("entities");
		
		Iterator<MapObject> mapObjectIterator = entities.getObjects().iterator();
		while(mapObjectIterator.hasNext()) {
			MapObject obj = mapObjectIterator.next();
			MapProperties objProps = obj.getProperties();
			
			if(obj.getName().equalsIgnoreCase("hershey")) {
				hershey = new Hershey((Float)objProps.get("x"), (Float)objProps.get("y"), cam, map.getLayers().get("collision"));
			} else if(obj.getName().equalsIgnoreCase("enemy")) {
				Enemy enemy = new Enemy((Float)objProps.get("x"), (Float)objProps.get("y"), hershey);
				enemies.add(enemy);
			}
		}
		
		batch = new SpriteBatch();
	}
	
	public void update(float delta){
		elapsed += delta;
		
		hershey.update(delta);
		
		for(Enemy enemy : enemies) { 
			enemy.update(delta);
		}
		
		checkCollisions(delta);
	}
	
	public void render(float delta) {
		cam.position.set(hershey.getPos().x, hershey.getPos().y, 0);
		
		//Handle map edges
		if(((hershey.getPos().x - (cam.viewportWidth / 2)) > 0) && (hershey.getPos().x + (cam.viewportWidth / 2) < (Integer)mapInfo.get("width") - 30)) { 
			if(cam.position.x < (hershey.getPos().x + (hershey.WIDTH / 2))) {
				cam.position.x += 2;
			}
		} else if((hershey.getPos().x - (cam.viewportWidth / 2)) <= 0) {
			if(cam.position.x > cam.viewportWidth / 2) {
				cam.position.x -= 2;
			} else {
				cam.position.x = cam.viewportWidth / 2;
			}
		} else if((hershey.getPos().x + (cam.viewportWidth / 2) > (Integer)mapInfo.get("width") - 30)) {
			if(cam.position.x < ((Integer)mapInfo.get("width") - (cam.viewportWidth / 2))) {
				cam.position.x += 2;
			} else {
				cam.position.x = (Integer)mapInfo.get("width") + (cam.viewportWidth / 2) - 30;
			}
		}
		
		if((hershey.getPos().y - (cam.viewportHeight / 2) > 0) && (hershey.getPos().y - (cam.viewportHeight)) < (Integer)mapInfo.get("height") - 30) { 
			if(cam.position.y < (hershey.getPos().y + (hershey.HEIGHT / 2))) {
				cam.position.y += 2;
			}
		} else if(hershey.getPos().y - (cam.viewportHeight / 2) <= 0) {
			if(cam.position.y > cam.viewportHeight / 2) {
				cam.position.y -= 2;
			} else {
				cam.position.y = cam.viewportHeight / 2;
			}
		} else if((hershey.getPos().y + (cam.viewportHeight)) > (Integer)mapInfo.get("height")) {
			if(cam.position.y < ((Integer)mapInfo.get("height") - (cam.viewportHeight / 2))) {
				cam.position.y -= 2;
			} else {
				cam.position.y = cam.viewportHeight + (Integer)mapInfo.get("height") - 30;
			}
		}
		
		cam.update();
		renderer.getBatch().setProjectionMatrix(cam.combined);
		
		renderer.setView(cam);
		
		//Debug stuff
//		Pixmap pm = new Pixmap((int)hershey.getBounds().width, (int)hershey.getBounds().height, Pixmap.Format.RGB888);
//		pm.drawRectangle(0, 0, (int)hershey.getBounds().width, (int)hershey.getBounds().height);
//		Texture t = new Texture(pm);
		
		renderer.getBatch().begin();
			//Render Background
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(0));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(1));
			
			//Draw Hershey
			renderer.getBatch().draw(hershey.getCurrAnimation().getKeyFrame(hershey.getStateTime(), true), hershey.getPos().x, hershey.getPos().y, 0, 0, Hershey.WIDTH, Hershey.HEIGHT, 1, 1, 0);
			
			//Draw hershey's bounding box
//			renderer.getBatch().draw(t, hershey.getBounds().x, hershey.getBounds().y, 0, 0, (int)hershey.getBounds().width, (int)hershey.getBounds().height);
			
			//Draw the enemy
			for(Enemy enemy : enemies) {
				renderer.getBatch().draw(enemy.getTextureRegion(), enemy.getPos().x, enemy.getPos().y, 0, 0, Enemy.WIDTH, Enemy.HEIGHT, 2, 2, 0);
			}
			
			//Render Foreground
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(2));
			
			//Draw the clock
			String time = (Math.round(elapsed) < 10) ? "0" + Math.round(elapsed) : Integer.toString(Math.round(elapsed)); 
			clockText.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			clockText.draw(renderer.getBatch(), time, cam.position.x, cam.position.y + cam.viewportHeight - 350);
		renderer.getBatch().end();
	}
	
	public void dispose() {
		
	}

	private void checkCollisions(float delta) {
		for(Enemy enemy : enemies) {
			if(hershey.getBounds().overlaps(enemy.getBounds())) {
				touchTime += delta;
				
				if(touchTime >= 0.3){
					if(hershey.getNervousPercent() < .99)
						hershey.setNervousPercent(hershey.getNervousPercent() + 0.02f);
					
					System.out.println(Math.round(hershey.getNervousPercent() * 100) + "%");
					touchTime = 0;
				}
			}
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
