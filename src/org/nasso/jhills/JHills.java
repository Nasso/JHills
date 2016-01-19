package org.nasso.jhills;

import javafx.scene.input.KeyCode;

import org.nasso.engine.Game;
import org.nasso.engine.KeyInfo;

public class JHills extends Game {
	private MenuLevel menu;
	private HillsLevel hills;
	
	public String getName(){
		return "JHills";
	}
	
	protected void initGame(){
		menu = new MenuLevel(this, new Callback(){
			public void call() {
				hills = new HillsLevel(JHills.this, menu.getSeed());
				JHills.this.setCurrentLevel(hills);
			}
		});
		
		this.setCurrentLevel(menu);
	}
	
	protected void logicStep(float delta, float now) {
		
	}
	
	protected void onKeyDown(KeyInfo key) {
		if(key.getKeyCode() == KeyCode.ESCAPE){
			this.quit();
		}
	}
	
	protected void onKeyUp(KeyInfo key) {
		
	}
	
	protected void onKeyType(KeyInfo key) {
		
	}
	
	public static void main(String[] args){
		JHills.launch(args);
	}
}
