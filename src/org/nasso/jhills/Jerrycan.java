package org.nasso.jhills;

public class Jerrycan {
	private float x = 0.0f;
	private float y = 0.0f;
	
	private float size = 0.5f;
	
	public Jerrycan(float x, float y){
		this.setX(x);
		this.setY(y);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}
}
