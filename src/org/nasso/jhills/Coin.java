package org.nasso.jhills;

public class Coin {
	private float xPos;
	private float yPos;
	private float radius = 0.3f;
	
	private int value = 1;
	
	public Coin(int value, float x, float y){
		this.value = value;
		this.xPos = x;
		this.yPos = y;
	}
	
	public Coin(int value){
		this(value, 0, 0);
	}
	
	public Coin(){
		this(1);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public float getX() {
		return xPos;
	}

	public void setX(float xPos) {
		this.xPos = xPos;
	}

	public float getY() {
		return yPos;
	}

	public void setY(float yPos) {
		this.yPos = yPos;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
}
