package com.dshedd.hersheyquest.system;

import com.dshedd.hersheyquest.entities.Enemy;
import com.dshedd.hersheyquest.entities.Hershey;

public class World {
	
	private Hershey hershey;
	public Enemy enemy;
	
	public World() {
		hershey = new Hershey(10, 10);
		enemy = new Enemy(100, 100, hershey);
	}
	
	public void update(float delta){
		hershey.update(delta);
		enemy.update(delta);
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
