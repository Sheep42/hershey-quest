package com.dshedd.hersheyquest.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
	SpriteBatch batch = new SpriteBatch();
	
	public static final int IDLE = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int UP = 3;
	public static final int DOWN = 4;
	public static final int SPAWN = 5;
	
	public static final float ACCELERATION = 200f;
	static final float MAX_VEL = 50f;
	public static final float DRAG = 0.92f;
	public static final float WIDTH = 64;
	public static final float HEIGHT = 64;

	public static final float BOUNDS_SHORT = 24;
	public static final float BOUNDS_LONG = 55;
	
	private int state = SPAWN;
	private int dir = RIGHT;
	
	private Rectangle bounds = new Rectangle();
	
	private TextureRegion textureRegion;
	private Texture texture;
	
	private Vector2 pos = new Vector2();
	private Vector2 accel = new Vector2();
	private Vector2 vel = new Vector2();
	
	private float stateTime = 0;
	private boolean trained = false;
	
	Animation currAnimation, enemyUp, enemyDown, enemyLeft, enemyRight;
	
	Hershey hershey;
	
	public Enemy(float x, float y, Hershey hershey) {
		//Init position
		pos.x = x;
		pos.y = y;
		
		this.hershey = hershey;
		
		//Bounds
		bounds.width = BOUNDS_LONG;
		bounds.height = BOUNDS_SHORT;
		bounds.x = pos.x;
		bounds.y = pos.y;
				
		//Init State
		state = SPAWN;
		stateTime = 0;
		
		//Sprite
		texture = new Texture(Gdx.files.internal("enemy.png"));
		textureRegion = new TextureRegion(texture, 0, 0, WIDTH, HEIGHT);
		
		TextureRegion[] split = new TextureRegion(textureRegion).split(32, 32)[0];
		TextureRegion[] mirror = new TextureRegion(textureRegion).split(32, 32)[0];
		
		mirror[0].flip(false, true);
		mirror[1].flip(false, true);
		mirror[2].flip(true, false);
		mirror[3].flip(true, false);
		
		currAnimation = enemyUp = new Animation(0.2f, split[0], split[1]);
		dir = UP;
		
		enemyDown = new Animation(0.2f, mirror[0], mirror[1]);
		enemyRight = new Animation(0.2f, split[2], split[3]);
		enemyLeft = new Animation(0.2f, mirror[2], mirror[3]);
	}
	
	public void update(float delta) {
		if(pos.dst(hershey.getPos()) <= 400 && !trained) {
			if(pos.y <= hershey.getPos().y ) {
				dir = UP;
				accel.y = ACCELERATION;
				
				if(vel.y > 25f && (vel.x < 25f && vel.x > -25f))
					currAnimation = enemyUp;
			} else if(pos.y > hershey.getPos().y) {
				dir = DOWN;
				accel.y = -ACCELERATION;
				
				if(vel.y < -25f && (vel.x < 25f && vel.x > -25f))
					currAnimation = enemyDown;
			}
			
			if(pos.x <= hershey.getPos().x) {
				dir = RIGHT;
				accel.x = ACCELERATION;
				
				if(vel.x > 25f && (vel.y < 25f && vel.y > -25f))
					currAnimation = enemyRight;
			} else if(pos.x > hershey.getPos().x) {
				dir = LEFT;
				accel.x = -ACCELERATION;
				
				if(vel.x < -25f && (vel.y < 25f && vel.y > -25f))
					currAnimation = enemyLeft;
			} 
		}
		
		//Reset the bounding box
		switch(dir) {
			case LEFT: case RIGHT:
				bounds.width = BOUNDS_LONG;
				bounds.height = BOUNDS_SHORT;
				break;
			case UP: case DOWN:
				bounds.width = BOUNDS_SHORT;
				bounds.height = BOUNDS_LONG;
				break;
		}
				
		//Movement
		accel.scl(delta);
		vel.add(accel.x, accel.y);
		
		if(accel.x == 0) vel.x *= DRAG;
		if(accel.y == 0) vel.y *= DRAG;
		
		if(vel.x > MAX_VEL) vel.x = MAX_VEL;
		if(vel.x < -MAX_VEL) vel.x = -MAX_VEL;
		
		if(vel.y > MAX_VEL) vel.y = MAX_VEL;
		if(vel.y < -MAX_VEL) vel.y = -MAX_VEL;
		
		vel.scl(delta);
		
		bounds.x += vel.x;
		//collision here
		
		bounds.y += vel.y;
		//collision here
		
		pos.x = bounds.x;
		pos.y = bounds.y;
		
		vel.scl(1.0f / delta);
		
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

	public float getStateTime() {
		return stateTime;
	}

	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}

	public Animation getCurrAnimation() {
		return currAnimation;
	}

	public void setCurrAnimation(Animation currAnimation) {
		this.currAnimation = currAnimation;
	}

	public boolean isTrained() {
		return trained;
	}

	public void setTrained(boolean trained) {
		this.trained = trained;
	} 
}
