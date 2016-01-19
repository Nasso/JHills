package org.nasso.jhills;

import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import org.nasso.engine.Game;
import org.nasso.engine.KeyInfo;
import org.nasso.engine.Level;
import org.nasso.engine.MouseInfo;

public class MenuLevel extends Level {
	private Map previewMap = new Map(100, 8, 2.5f, 0);
	private boolean mapUpdate = false;
	
	// Text box
	private	float textBoxWidth = 400;
	private float textBoxHeight = 40;
	
	
	private Paint quitBtnPaint = Color.web("#FFFFFF");
	
	private float lastSeedModification = 0;
	private float theNow = 0;
	
	private long lastSeed = 0;
	
	private Callback onValidation;
	private String seed = "";
	
	public MenuLevel(Game game, Callback onValidation) {
		super(game);
		
		this.onValidation = onValidation;
	}
	
	public void renderLevel(GraphicsContext gtx) {
		gtx.setFont(Font.font("Arial", 50));
		
		gtx.setFill(Color.web("#784800"));
		gtx.fillRect(0, 0, this.getWidth(), this.getHeight());

		gtx.save(); // MAP_RENDER
			gtx.beginPath();
				for(int i = 0; i < previewMap.getPreciseHeights().length; i++){
					float x = i * previewMap.getPreciseXSpace() * 100;
					float y = this.getHeight() - previewMap.getPreciseHeightAt(i) * 100;
					
					gtx.lineTo(x, y);
				}
				
				gtx.stroke();
			gtx.closePath();
		gtx.restore(); // !MAP_RENDER
		
		// Draw Title
		gtx.setFill(Color.web("#FFFFFF"));
		gtx.setTextAlign(TextAlignment.CENTER);
		gtx.fillText("JHills", this.getWidth()/2, 50);
		
		gtx.setFill(Color.web("#FFFFFF"));
		gtx.setFont(Font.font("Arial", 22));
		gtx.fillText("Map seed:", this.getWidth()/2, this.getHeight()/2);

		// Text box render
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
		
		// Draw button
		gtx.setFill(quitBtnPaint);
		gtx.fillRoundRect(this.getWidth()/2 - textBoxWidth/2, this.getHeight()/2-textBoxHeight/2 + 120, textBoxWidth, textBoxHeight, 8, 8);
		gtx.setFill(Color.web("#000000"));
		gtx.fillText("Quit", this.getWidth()/2, this.getHeight()/2 + 128);
		
		// Cursor
		gtx.setStroke(Color.web("#000000"));
		gtx.setFill(Color.web("#FFFFFF"));
		gtx.beginPath();
			float x = getGame().getMouseX();
			float y = getGame().getMouseY();
			
			gtx.moveTo(x, y);
			gtx.lineTo(x+16, y+8);
			gtx.lineTo(x+8, y+8);
			gtx.lineTo(x+8, y+16);
			
			gtx.fill();
			gtx.stroke();
		gtx.closePath();
	}
	
	public void keyDown(KeyInfo key) {
		if(key.getKeyCode() == KeyCode.BACK_SPACE && seed.length() > 0){
			seed = seed.substring(0, seed.length()-1);
			lastSeedModification = theNow;
			mapUpdate = false;
		}else if(key.getKeyCode() == KeyCode.ENTER){
			onValidation.call();
		}
	}
	
	public void keyType(KeyInfo key) {
		if(key.getKeyChar() != KeyEvent.CHAR_UNDEFINED && key.getKeyChar().matches("[a-zA-Z0-9\\s]*") && seed.length() < 30){
			seed += key.getKeyChar();
			lastSeedModification = theNow;
			mapUpdate = false;
		}
	}
	
	public void keyUp(KeyInfo key) {
		
	}
	
	public void scroll(int side) {
		
	}
	
	public void step(float delta, float now) {
		theNow = now;
		
		if(theNow - lastSeedModification >= 1000 && !mapUpdate){
			mapUpdate = true;
			lastSeed = (seed.length() == 0 ? new Random().nextLong() : seed.hashCode());
			new Thread(new Runnable(){
				public void run() {
					previewMap = new Map(100, 8, 2.5f, lastSeed);
				}
			}).start();
		}
	}
	
	public long getSeed(){
		return lastSeed;
	}
	
	public void mouseDown(MouseInfo mouse) {
		
	}
	
	public void mouseUp(MouseInfo mouse) {
		float x = mouse.getX();
		float y = mouse.getY();
		
		if(		x > this.getWidth()/2-textBoxWidth/2 &&
				x < this.getWidth()/2+textBoxWidth/2 &&
				y < this.getHeight()/2+textBoxHeight/2+120 &&
				y > this.getHeight()/2-textBoxHeight/2+120){
			getGame().quit();
		}
	}
	
	public void mouseMove(MouseInfo mouse) {
		float x = mouse.getX();
		float y = mouse.getY();
		
		if(		x > this.getWidth()/2-textBoxWidth/2 &&
				x < this.getWidth()/2+textBoxWidth/2 &&
				y < this.getHeight()/2+textBoxHeight/2+120 &&
				y > this.getHeight()/2-textBoxHeight/2+120){
			quitBtnPaint = Color.web("#2ECC71");
		}else{
			quitBtnPaint = Color.web("#FFFFFF");
		}
	}
}
