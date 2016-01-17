package org.nasso.jhills;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Player {
	private float xPos = 1;
	private float yPos = 10;
	private float angle = 0;
	
	private float radius = 0.5f;
	
	private Body body;
	
	public Player(World world){
		this.genBody(world);
	}
	
	private void genBody(World world){
		BodyDef playerDef = new BodyDef();
		playerDef.type = BodyType.DYNAMIC;
		playerDef.position.set(this.getXPos(), this.getYPos());
		
		this.body = world.createBody(playerDef);
		
		CircleShape playerShape = new CircleShape();
		playerShape.setRadius(this.getRadius());
		
		FixtureDef playerFixtureDef = new FixtureDef();
		playerFixtureDef.shape = playerShape;
		playerFixtureDef.density = 5f;
		playerFixtureDef.friction = 0.5f;
		
		this.body.createFixture(playerFixtureDef);
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

	public Body getBody() {
		return body;
	}
}
