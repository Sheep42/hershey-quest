package com.dshedd.hersheyquest.system;

import com.dshedd.hersheyquest.entities.Hershey;

public class World {
	
	Hershey hershey;
	
	public World() {
		hershey = new Hershey(10, 10);
	}
	
	public void update(float delta){
		hershey.update(delta);
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
