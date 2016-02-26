package com.dshedd.hersheyquest.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Hershey {
	SpriteBatch batch = new SpriteBatch();
	
	public static final int IDLE = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int UP = 3;
	public static final int DOWN = 4;
	public static final int SPAWN = 5;
	public static final int NERVOUS = 6;
	public static final int OVERWHELMED = 7;
	
	public static final float WIDTH = 32;
	public static final float HEIGHT = 32;
	
	private int state = SPAWN;
	private int dir = RIGHT;
	
	private Rectangle bounds = new Rectangle();
	
	private TextureRegion textureRegion;
	private Texture texture;
	
	private Vector2 pos = new Vector2();
	private Vector2 accel = new Vector2();
	private Vector2 vel = new Vector2();
	
	private float stateTime = 0;
	
	public Hershey(float x, float y) {
		//Init position
		pos.x = x;
		pos.y = y;
		
		//Bounds
		bounds.width = WIDTH;
		bounds.height = HEIGHT;
		bounds.x = pos.x;
		bounds.y = pos.y;
				
		//Init State
		state = SPAWN;
		stateTime = 0;
		
		//Sprite
		texture = new Texture(Gdx.files.internal("hershey.png"));
		textureRegion = new TextureRegion(texture, 0, 0, WIDTH, HEIGHT);
	}
	
	public void update(float delta) {
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			pos.y += 1;
		} else if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			pos.y -= 1;
		}
		
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			pos.x += 1;
		} else if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			pos.x -= 1;
		}
	}

	public void render(float delta) {
		batch.begin();
			batch.draw(textureRegion, pos.x, pos.y, 0, 0, WIDTH, HEIGHT, 1, 1, 0);
		batch.end();
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public Vector2 getPos() {
		return pos;
	}

	public void setPos(Vector2 pos) {
		this.pos = pos;
	}

	public Vector2 getAccel() {
		return accel;
	}

	public void setAccel(Vector2 accel) {
		this.accel = accel;
	}

	public Vector2 getVel() {
		return vel;
	}

	public void setVel(Vector2 vel) {
		this.vel = vel;
	} 
}