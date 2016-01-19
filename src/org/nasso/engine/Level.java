package org.nasso.engine;

import javafx.scene.canvas.GraphicsContext;

public abstract class Level {
	private Game game;
	
	public Level(Game game){
		this.game = game;
	}
	
	public abstract void renderLevel(GraphicsContext gtx);
	public abstract void keyDown(KeyInfo key);
	public abstract void keyUp(KeyInfo key);
	public abstract void keyType(KeyInfo key);
	public abstract void scroll(int side);
	public abstract void step(float delta, float now);

	public Game getGame() {
		return game;
	}
	
	public float getWidth(){
		return (float) game.getWidth();
	}
	
	public float getHeight(){
		return (float) game.getHeight();
	}
}
