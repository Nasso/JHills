package org.nasso.jhills;

public class Camera {
	private float xPos;
	private float yPos;
	
	public Camera(){
		this.xPos = 0;
		this.yPos = 0;
	}

	public float getXPos() {
		return xPos;
	}

	public void setXPos(float xPos) {
		this.xPos = xPos;
	}

	public float getYPos() {
		return yPos;
	}

	public void setYPos(float yPos) {
		this.yPos = yPos;
	}
}
