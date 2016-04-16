package com.dshedd.hersheyquest.entities;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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
	public static final int EXCITED = 8;
	
	public static final float ACCELERATION = 267f;
	static final float MAX_VEL = 80f;
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
	
	Vector3 touchPos = new Vector3(); 
	
	private OrthographicCamera cam;
	
	private float stateTime = 0, 
				nervousPercent = 0,
				oldX = 0,
				oldY = 0;

	protected Animation hersheyUp, hersheyDown, hersheyRight, hersheyLeft;
	
	protected MapLayer collisionLayer;
	
	private Animation currAnimation;
	
	private boolean collide = false;
	
	public Hershey(float x, float y, OrthographicCamera cam, MapLayer collisionLayer) {
		//Init position
		pos.x = x;
		pos.y = y;
		
		//Bounds
		bounds.width = BOUNDS_SHORT;
		bounds.height = BOUNDS_LONG;
		
		bounds.x = pos.x + bounds.width / 2;
		bounds.y = pos.y;
				
		this.collisionLayer = collisionLayer;
		
		//Init State
		state = SPAWN;
		stateTime = 0;
		
		//Sprite
		texture = new Texture(Gdx.files.internal("hershey-full.png"));
		textureRegion = new TextureRegion(texture, 0, 0, WIDTH, HEIGHT);
		
		TextureRegion[] split = new TextureRegion(textureRegion).split(32, 32)[0];
		TextureRegion[] mirror = new TextureRegion(textureRegion).split(32, 32)[0];
		
		mirror[0].flip(false, true);
		mirror[1].flip(false, true);
		mirror[2].flip(true, false);
		mirror[3].flip(true, false);
		
		currAnimation = hersheyUp = new Animation(0.2f, split[0], split[1]);
		dir = UP;
		
		hersheyDown = new Animation(0.2f, mirror[0], mirror[1]);
		hersheyRight = new Animation(0.2f, split[2], split[3]);
		hersheyLeft = new Animation(0.2f, mirror[2], mirror[3]);
		
		//Camera
		this.cam = cam;
	}
	
	public void update(float delta) {
		collide = false;
		
		//Handle key input
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			dir = UP;
			accel.y = ACCELERATION;
			currAnimation = hersheyUp;
		} else if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			dir = DOWN;
			accel.y = -ACCELERATION;
			currAnimation = hersheyDown;
		}
		
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			dir = RIGHT;
			accel.x = ACCELERATION;
			currAnimation = hersheyRight;
		} else if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			dir = LEFT;
			accel.x = -ACCELERATION;
			currAnimation = hersheyLeft;
		} 
		
		//Handle touch input
		if(Gdx.input.isTouched()) {
			touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			cam.unproject(touchPos);
			
			if(touchPos.y >= (pos.y + HEIGHT/2)) {
				accel.y = ACCELERATION;
			} else if(touchPos.y < (pos.y + HEIGHT/2)) {
				accel.y = -ACCELERATION;
			}

			if(touchPos.x >= (pos.x + WIDTH/2) ) {
				accel.x = ACCELERATION;
			} else if(touchPos.x < (pos.x + WIDTH/2)) {
				accel.x = -ACCELERATION;
			}
			
			if(accel.x > 0 || accel.y > 0) {
				if(vel.x > vel.y) {
					dir = RIGHT;
					currAnimation = hersheyRight;
				} else {
					dir = UP;
					currAnimation = hersheyUp;
				}
			} else if(accel.x < 0 || accel.y < 0) {
				if(vel.x < vel.y) {
					dir = LEFT;
					currAnimation = hersheyLeft;
				} else {
					dir = DOWN;
					currAnimation = hersheyDown;
				}
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
		
		//Center the bounding box
		bounds.x = pos.x + ((this.WIDTH / 2) - (bounds.width / 2));
		bounds.y = pos.y + ((this.HEIGHT / 2) - (bounds.height / 2));
		
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
		
		//Collsion
		oldX = bounds.x;
		oldY = bounds.y;
		
		bounds.x += vel.x;
		bounds.y += vel.y;
			
		Iterator<MapObject> mapObjectIterator = collisionLayer.getObjects().iterator();
		while(mapObjectIterator.hasNext()) {
			MapObject obj = mapObjectIterator.next();
			MapProperties objProperties = obj.getProperties();
			
			Rectangle objBounds = new Rectangle((Float)objProperties.get("x"), 
												(Float)objProperties.get("y"), 
												(Float)objProperties.get("width"), 
												(Float)objProperties.get("height")
											);
			
			if(objBounds.overlaps(bounds)) {
				collide = true;
				break;
			}
		}
		
		if(collide) {
			vel.x = 0;
			vel.y = 0;
			
			bounds.x = oldX;
			bounds.y = oldY;
		} else {
			pos.x += vel.x;
			pos.y += vel.y;
		}
		
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

	public float getNervousPercent() {
		return nervousPercent;
	}

	public void setNervousPercent(float nervousPercent) {
		this.nervousPercent = nervousPercent;
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
}