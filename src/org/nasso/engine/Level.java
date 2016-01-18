package org.nasso.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

public abstract class Level {
	public Level(){
		
	}
	
	public abstract void renderLevel(GraphicsContext gtx);
	public abstract void keyDown(KeyCode key);
	public abstract void keyUp(KeyCode key);
	
}
