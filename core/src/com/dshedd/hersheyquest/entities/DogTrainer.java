package com.dshedd.hersheyquest.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class DogTrainer {
	SpriteBatch batch = new SpriteBatch();
	
	public static final int IDLE = 0;
	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	public static final int UP = 1;
	public static final int DOWN = -1;
	public static final int SPAWN = 5;
	
	public static final float ACCELERATION = 200f;
	static final float MAX_VEL = 50f;
	public static final float DRAG = 0.92f;
	public static final float WIDTH = 64;
	public static final float HEIGHT = 64;
	
	private int state = SPAWN;
	private int dir = RIGHT;
	
	private Rectangle bounds = new Rectangle();
	
	private TextureRegion textureRegion;
	private Texture texture;
	private Animation animation;
	
	private Vector2 pos = new Vector2();
	private Vector2 accel = new Vector2();
	private Vector2 vel = new Vector2();
	
	private float stateTime = 0;
	
	public DogTrainer(float x, float y) {
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
		texture = new Texture(Gdx.files.internal("dog-trainer.png"));
		textureRegion = new TextureRegion(texture, 0, 0, WIDTH, HEIGHT);
		
		TextureRegion[] split = new TextureRegion(textureRegion).split(32, 32)[0];
		
		animation = new Animation(0.2f, split[0], split[1]);
	}
	
	public void update(float delta) {
		stateTime += delta;
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

	public TextureRegion getTextureRegion() {
		return textureRegion;
	}

	public void setTextureRegion(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	} 
}