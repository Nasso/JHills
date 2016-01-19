package org.nasso.jhills;

import javafx.scene.input.KeyCode;

import org.nasso.engine.Game;

public class JHills extends Game {
	public String getName(){
		return "JHills";
	}
	
	protected void initGame(){
		this.setCurrentLevel(new GameLevel(this));
	}
	
	protected void logicStep(float delta, float now) {
		
	}
	
	protected void onKeyDown(KeyCode key) {
		
	}
	
	protected void onKeyUp(KeyCode key) {
		
	}
	
	public static void main(String[] args){
		JHills.launch(args);
	}
}
