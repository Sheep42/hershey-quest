package com.dshedd.hersheyquest.system;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
import com.dshedd.hersheyquest.HersheyQuest;
import com.dshedd.hersheyquest.entities.DogTrainer;
import com.dshedd.hersheyquest.entities.Enemy;
import com.dshedd.hersheyquest.entities.Hershey;
import com.dshedd.hersheyquest.entities.Krissy;

public class World {
	
	private Hershey hershey;
	private Krissy krissy;
	private OrthographicCamera cam;
	private Vector3 touchPos = new Vector3(0, 0, 0);
	private SpriteBatch batch;
	private BitmapFont nervousText, screenText;
	private int levelIndex  = 1;
	private boolean instTrigger = true;
	
	protected TiledMap map;
	protected MapProperties mapInfo;
	protected OrthogonalTiledMapRenderer renderer;
	protected TiledMapTileLayer collisionLayer;
	protected Array<Enemy> enemies = new Array<Enemy>();
	protected Array<DogTrainer> trainers = new Array<DogTrainer>();
	protected Array<String> instructions = new Array<String>();
	protected Random randNumGen = new Random();
	
	Texture mask;
	String currPage;
	
	private float elapsed = 0, touchTime = 0, countDown = 120, nervousX = 0, nervousY = 0;
	
	public World() {
		//Instructions + Pause mask
		Pixmap maskRect = new Pixmap((int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
		maskRect.setColor(0f, 0f, 0f, 0.5f);
		maskRect.drawRectangle(0, 0, (int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());
		maskRect.fill();
		mask = new Texture(maskRect);
		
		//Instructions Array
		instructions.add("This is Hershey. \nHershey gets nervous very easily. \n\nEspecially when other dogs try to touch him.\n\nPress any key to continue...");
		instructions.add("If Hershey gets too nervous, you'll lose.\n\nBe Careful!");
		instructions.add("This is a dog trainer.\nOther dogs like the treats he has.\n\nBut he only has enough for one dog.");
		instructions.add("This is Krissy.\nHershey likes Krissy. She makes him feel....\n\n...Not so nervous...");
		currPage = instructions.get(0);
		
		nervousText = new BitmapFont(Gdx.files.internal("font/font.fnt"));
		screenText = new BitmapFont(Gdx.files.internal("font/font.fnt"));
		screenText.getRegion().setRegionWidth(Gdx.graphics.getWidth() / 2);
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
			} else if(obj.getName().equalsIgnoreCase("krissy")) {
				krissy = new Krissy((Float)objProps.get("x"), (Float)objProps.get("y"), hershey);
			} else if(obj.getName().equalsIgnoreCase("DogTrainer")) {
				DogTrainer dogTrainer = new DogTrainer((Float)objProps.get("x"), (Float)objProps.get("y"));
				trainers.add(dogTrainer);
			}
		}
		
		batch = new SpriteBatch();
	}
	
	public void update(float delta){
		if(!instTrigger && !HersheyQuest.paused) {
			elapsed += delta;
	
			hershey.update(delta);
			krissy.update(delta);
			
			for(DogTrainer trainer : trainers) { 
				trainer.update(delta);
			}
			
			for(Enemy enemy : enemies) { 
				enemy.update(delta);
			}
			
			checkCollisions(delta);
		}
		
		if(!instTrigger){
			if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
				HersheyQuest.paused = !HersheyQuest.paused;
			}
		} else {
			if(Gdx.input.isKeyJustPressed(Keys.ANY_KEY) || Gdx.input.justTouched()) {
				instructions.removeIndex(0);
				
				if(instructions.size > 0) {
					currPage = instructions.get(0);
				} else {
					instTrigger = false;
				}
			}
		}
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
		} else if((hershey.getPos().x + (cam.viewportWidth / 2) > ((Integer)mapInfo.get("width") * 32) - 30)) {
			if(cam.position.x < (((Integer)mapInfo.get("width") * 32) - (cam.viewportWidth / 2))) {
				cam.position.x += 2;
			} else {
				cam.position.x = ((Integer)mapInfo.get("width") * 32) - (cam.viewportWidth / 2);
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
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("background"));
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("foreground"));
			
			//Draw Hershey
			renderer.getBatch().draw(hershey.getCurrAnimation().getKeyFrame(hershey.getStateTime(), true), hershey.getPos().x, hershey.getPos().y, 0, 0, Hershey.WIDTH, Hershey.HEIGHT, 1, 1, 0);
			
			//Draw hershey's bounding box
//			renderer.getBatch().draw(t, hershey.getBounds().x, hershey.getBounds().y, 0, 0, (int)hershey.getBounds().width, (int)hershey.getBounds().height);
			
			//Draw the dog trainers
			for(DogTrainer trainer : trainers) {
				renderer.getBatch().draw(trainer.getAnimation().getKeyFrame(trainer.getStateTime(), true), trainer.getPos().x, trainer.getPos().y, 0, 0, DogTrainer.WIDTH, DogTrainer.HEIGHT, 1, 1, 0);
			}
			
			//Draw the enemy
			for(Enemy enemy : enemies) {
				renderer.getBatch().draw(enemy.getCurrAnimation().getKeyFrame(enemy.getStateTime(), true), enemy.getPos().x, enemy.getPos().y, 0, 0, Enemy.WIDTH, Enemy.HEIGHT, 1, 1, 0);
			}
			
			//Render Krissy
			renderer.getBatch().draw(krissy.getAnimation().getKeyFrame(krissy.getStateTime(), true), krissy.getPos().x, krissy.getPos().y, 0, 0, Krissy.WIDTH, Krissy.HEIGHT, 1, 1, 0);
			
			//Draw the nervous meter
			String nervousMeter = "Nervous Meter: " + Math.round(hershey.getNervousPercent() * 100) + "%";
			if(hershey.getNervousPercent() > .55) {
				nervousX = cam.position.x;
				nervousY = cam.position.y;
			
				nervousX += (randNumGen.nextFloat() * 5) - 2.5f;
				nervousY += (randNumGen.nextFloat() * 5) - 2.5f;
			} else {
				nervousX = cam.position.x;
				nervousY = cam.position.y;
			}
			
			nervousText.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			nervousText.draw(renderer.getBatch(), nervousMeter, nervousX - 128, nervousY + cam.viewportHeight - 350);
			
			//Draw the Instructions
			if(instTrigger || HersheyQuest.paused)
				renderer.getBatch().draw(mask, cam.position.x - cam.viewportWidth / 2 , cam.position.y - cam.viewportHeight / 2, 0, 0, (int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());
			
			if(instTrigger) {
				screenText.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				screenText.draw(renderer.getBatch(), currPage, (cam.position.x - screenText.getRegion().getRegionWidth() / 2) - 50, cam.position.y + 100);
			}
		renderer.getBatch().end();
	}
	
	public void dispose() {
		
	}

	private void checkCollisions(float delta) {
		if(hershey.getBounds().overlaps(krissy.getBounds())) {
			System.out.println("Win Case");
		}
		
		for(Enemy enemy : enemies) {
			if(hershey.getBounds().overlaps(enemy.getBounds())) {
				touchTime += delta;
				
				if(touchTime >= 0.3){
					if(hershey.getNervousPercent() < 1.0)
						hershey.setNervousPercent(hershey.getNervousPercent() + 0.02f);
					
					touchTime = 0;
				}
			}
			
			//Handle Enemy collision with trainer
			for(DogTrainer trainer : trainers) {
				if(enemy.getPos().dst(trainer.getPos()) <= 50) {
					if(!trainer.isTriggered() && !enemy.isTrained()) {
						trainer.setTriggered(true);
						enemy.setTrained(true);
						break;
					}
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

	public boolean isInstTrigger() {
		return instTrigger;
	}

	public void setInstTrigger(boolean instTrigger) {
		this.instTrigger = instTrigger;
	}
}
