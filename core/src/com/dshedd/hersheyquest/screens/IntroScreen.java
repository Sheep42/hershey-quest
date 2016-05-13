package com.dshedd.hersheyquest.screens;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.dshedd.hersheyquest.HersheyQuest;
import com.dshedd.hersheyquest.entities.Hershey;

public class IntroScreen extends AbstractScreen {
	private Hershey hershey;
	private OrthographicCamera cam;
	
	SpriteBatch batch;
	TextureRegion introScreen, continueText;
	HersheyQuest game;
	Vector2 logoPos, continuePos;
	Music titleSong;
	
	protected TiledMap map;
	protected MapProperties mapInfo;
	protected OrthogonalTiledMapRenderer renderer;
	
	float time = 0, hersheyMove = 0;
	Random rand = new Random();
	boolean setDir = false;
	
	public IntroScreen(HersheyQuest game) {
		super(game);
		this.game = game;
		
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.x = cam.viewportWidth / 2;
		cam.position.y = cam.viewportHeight / 2;
		
		game.setViewport(new ScalingViewport(Scaling.fit, game.screenWidth, game.screenHeight, cam));
		
		map = new TmxMapLoader().load("intro.tmx");
		
		renderer = new OrthogonalTiledMapRenderer(map);
		
		MapLayer entities = map.getLayers().get("entities");
		
		Iterator<MapObject> mapObjectIterator = entities.getObjects().iterator();
		while(mapObjectIterator.hasNext()) {
			MapObject obj = mapObjectIterator.next();
			MapProperties objProps = obj.getProperties();
			
			if(obj.getName().equalsIgnoreCase("hershey")) {
				hershey = new Hershey((Float)objProps.get("x"), (Float)objProps.get("y"), cam, map.getLayers().get("collision"));
				hershey.setControllable(false);
				hershey.setDir(4);
			}
		}

	}

	@Override
	public void show() {
		introScreen = new TextureRegion();
		continueText = new TextureRegion();
		batch = new SpriteBatch();
		
		titleSong = Gdx.audio.newMusic(Gdx.files.internal("title.wav"));
		titleSong.setLooping(true);
		titleSong.play();
		
		continueText.setTexture(new Texture(Gdx.files.internal("continue.png")));
		continueText.setRegion(0, 0, continueText.getTexture().getWidth(), continueText.getTexture().getHeight());
		
		introScreen.setTexture(new Texture(Gdx.files.internal("hq-logo.png")));
		introScreen.setRegion(0, 0, introScreen.getTexture().getWidth(), introScreen.getTexture().getHeight());
		
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 960, 680);
		
		logoPos = new Vector2((Gdx.graphics.getWidth() / 2) - (introScreen.getRegionWidth() / 2), 0);
		
		continuePos = new Vector2((Gdx.graphics.getWidth() / 2) - continueText.getRegionWidth() / 2, logoPos.y - 50);
	}
	
	@Override
	public void render(float delta) {
		//Wipe the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(logoPos.y < (Gdx.graphics.getHeight() / 2) + 40) {
			logoPos.y += 2;
			continuePos.y = logoPos.y - 50;
		}
		
		cam.update();
		renderer.getBatch().setProjectionMatrix(cam.combined);
		
		renderer.setView(cam);
		
		renderer.getBatch().begin();
			//Render Background
			renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("background"));
			
			//Draw Hershey
			renderer.getBatch().draw(hershey.getCurrAnimation().getKeyFrame(hershey.getStateTime(), true), hershey.getPos().x, hershey.getPos().y, 0, 0, Hershey.WIDTH, Hershey.HEIGHT, 1, 1, 0);
			
			renderer.getBatch().draw(introScreen, logoPos.x, logoPos.y);
			renderer.getBatch().draw(continueText, continuePos.x, continuePos.y);
		renderer.getBatch().end();
		
		time += delta;
		if(time > 3) {
			if(Gdx.input.isKeyPressed(Keys.ANY_KEY) || Gdx.input.justTouched()) {
				titleSong.stop();
				game.setScreen(new GameScreen(game));
			}
		}
		
		hersheyMove += delta;
		if(hersheyMove > 2 && hersheyMove < 5 && !setDir) {
			hershey.setMoveDown(false);
			hershey.setMoveUp(false);
			hershey.setMoveRight(false);
			hershey.setMoveLeft(false);
			
			hershey.setDir(rand.nextInt(4) + 1);
			setDir = true;
		} else if(hersheyMove > 5) {
			hersheyMove = 0;
			setDir = false;
			hershey.setAccel(new Vector2(0, 0));
			hershey.setVel(new Vector2(0, 0));
			hershey.setDir(-1);
			
			hershey.setMoveDown(false);
			hershey.setMoveUp(false);
			hershey.setMoveRight(false);
			hershey.setMoveLeft(false);
		}
		
		hershey.update(delta);
	}

	@Override
	public void hide(){
		batch.dispose();
		introScreen.getTexture().dispose();
	}
}
