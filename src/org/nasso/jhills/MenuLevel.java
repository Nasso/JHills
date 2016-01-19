package org.nasso.jhills;

import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import org.nasso.engine.Game;
import org.nasso.engine.KeyInfo;
import org.nasso.engine.Level;

public class MenuLevel extends Level {
	private Callback onValidation;
	private String seed = "";
	
	public MenuLevel(Game game, Callback onValidation) {
		super(game);
		
		this.onValidation = onValidation;
	}
	
	public void renderLevel(GraphicsContext gtx) {
		gtx.setFont(Font.font("Arial", 22));
		
		gtx.setFill(Color.web("#784800"));
		gtx.fillRect(0, 0, this.getWidth(), this.getHeight());

		gtx.setFill(Color.web("#FFFFFF"));
		gtx.setTextAlign(TextAlignment.CENTER);
		gtx.fillText("Map seed:", this.getWidth()/2, this.getHeight()/2);
		
		float textBoxWidth = 400;
		float textBoxHeight = 40;
		
		gtx.fillRoundRect(this.getWidth()/2 - textBoxWidth/2, this.getHeight()/2 + 16, textBoxWidth, textBoxHeight, 8, 8);
		
		gtx.save(); // TEXT_BOX_CLIP
			gtx.beginPath();
				gtx.rect(this.getWidth()/2 - textBoxWidth/2, this.getHeight()/2 + 16, textBoxWidth, textBoxHeight);
				gtx.clip();
			gtx.closePath();
			
			gtx.setTextAlign(TextAlignment.LEFT);
			
			if(seed.length() == 0){
				gtx.setFill(Color.web("#AAAAAA"));
				gtx.fillText("Leave blank for random", this.getWidth()/2-textBoxWidth/2 + 8, this.getHeight()/2+textBoxHeight+4);
			}else{
				gtx.setFill(Color.web("#000000"));
				gtx.fillText(seed, this.getWidth()/2-textBoxWidth/2 + 8, this.getHeight()/2+textBoxHeight+4);
			}
		gtx.restore(); // !TEXT_BOX_CLIP
	}
	
	public void keyDown(KeyInfo key) {
		if(key.getKeyCode() == KeyCode.BACK_SPACE && seed.length() > 0){
			seed = seed.substring(0, seed.length()-1);
		}else if(key.getKeyCode() == KeyCode.ENTER){
			onValidation.call();
		}
	}
	
	public void keyType(KeyInfo key) {
		if(key.getKeyChar() != KeyEvent.CHAR_UNDEFINED && key.getKeyChar().matches("[a-zA-Z0-9]") && seed.length() < 30){
			seed += key.getKeyChar();
		}
	}
	
	public void keyUp(KeyInfo key) {
		
	}
	
	public void scroll(int side) {
		
	}
	
	public void step(float delta, float now) {
		
	}
	
	public long getSeed(){
		return (seed.length() == 0 ? new Random().nextLong() : seed.hashCode());
	}
}
