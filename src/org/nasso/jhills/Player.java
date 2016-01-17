package org.nasso.jhills;

public class Player {
	private float xPos = 100;
	private float yPos = 1000;
	private float angle = 0;
	
	private float radius = 50;
	
	public Player(){
		
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
	
	public float getRadius() {
		return radius;
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}
}
