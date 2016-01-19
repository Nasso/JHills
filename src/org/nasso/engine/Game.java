package org.nasso.engine;

import java.util.HashMap;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public abstract class Game extends Application {
	// Game
	private HashMap<KeyCode, Boolean> keyStates = new HashMap<KeyCode, Boolean>();
	private Canvas gameCanvas;
	private Level currentLevel;
	
	private AnimationTimer timer;
	
	private float mouseX = 0;
	private float mouseY = 0;
	
	// FPS counter
	private int fps = 0;
	private int frameNum = 0;
	private float lastFPSUpdate = 0;
	private float lastFrameTime = -1;
	
	protected GraphicsContext gtx;
	
	private Scene createScene(){
		gameCanvas = new Canvas();
		
		BorderPane root = new BorderPane();
		root.setCenter(gameCanvas);
		
		Scene sce = new Scene(root);
		
		sce.setOnKeyPressed(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent event) {
				Game.this.setKeyDown(new KeyInfo(event.getCode(), event.getText(), event.getCharacter()), true);
			}
		});
		
		sce.setOnKeyTyped(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent event) {
				Game.this.sheduleOnKeyType(new KeyInfo(event.getCode(), event.getText(), event.getCharacter()));
			}
		});
		
		sce.setOnKeyReleased(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent event) {
				Game.this.setKeyDown(new KeyInfo(event.getCode(), event.getText(), event.getCharacter()), false);
			}
		});
		
		sce.setOnScroll(new EventHandler<ScrollEvent>(){
			public void handle(ScrollEvent event) {
				Game.this.onScroll((int) -event.getDeltaY()); 
			}
		});
		
		sce.setOnMousePressed(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				Game.this.sheduleOnMouseDown(new MouseInfo((float) event.getSceneX(), (float) event.getSceneY(), event.getButton()));
			}
		});
		
		sce.setOnMouseReleased(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				Game.this.sheduleOnMouseUp(new MouseInfo((float) event.getSceneX(), (float) event.getSceneY(), event.getButton()));
			}
		});
		
		sce.setOnMouseMoved(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				Game.this.sheduleOnMouseMove(new MouseInfo((float) event.getSceneX(), (float) event.getSceneY(), event.getButton()));
			}
		});
		
		sce.setCursor(Cursor.NONE);
		
		return sce;
	}
	
	public void start(Stage stg) throws Exception {
		stg.setTitle(getName());
		stg.setFullScreen(true);
		stg.setFullScreenExitHint("");
		stg.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stg.setScene(createScene());
		
		stg.show();
		
		gameCanvas.setWidth(stg.getWidth());
		gameCanvas.setHeight(stg.getHeight());
		
		timer = new AnimationTimer(){
			public void handle(long now) {
				float nowms = (float) (now / 1000000);
				
				if(lastFrameTime < 0){
					lastFrameTime = nowms;
				}
				
				float delta = nowms - lastFrameTime;
				
				Game.this.loop(delta, nowms);
				
				lastFrameTime = nowms;
			}
		};
		
		timer.start();
		
		gtx = gameCanvas.getGraphicsContext2D();
		
		initGame();
	}
	
	protected abstract void initGame();
	
	private void onScroll(int side){
		if(this.currentLevel != null){
			this.currentLevel.scroll(side);
		}
	}
	
	private void loop(float delta, float now){
		frameNum++;
		if(now - lastFPSUpdate > 1000){
			this.fps = frameNum;
			frameNum = 0;
			lastFPSUpdate = now;
		}
		
		logicStep(delta, now);
		if(this.currentLevel != null){
			this.currentLevel.step(delta, now);
			
			gtx.save();
				this.currentLevel.renderLevel(gtx);
			gtx.restore();
		}
	}
	
	protected abstract void logicStep(float delta, float now);
	
	private void sheduleOnKeyDown(KeyInfo key){
		this.onKeyDown(key);
		
		if(this.currentLevel != null){
			this.currentLevel.keyDown(key);
		}
	}
	
	private void sheduleOnKeyUp(KeyInfo key){
		this.onKeyUp(key);
		
		if(this.currentLevel != null){
			this.currentLevel.keyUp(key);
		}
	}
	
	private void sheduleOnKeyType(KeyInfo key){
		this.onKeyType(key);
		
		if(this.currentLevel != null){
			this.currentLevel.keyType(key);
		}
	}
	
	private void sheduleOnMouseDown(MouseInfo mouse){
		this.onMouseDown(mouse);
		
		if(this.currentLevel != null){
			this.currentLevel.mouseDown(mouse);
		}
	}
	
	private void sheduleOnMouseUp(MouseInfo mouse){
		this.onMouseUp(mouse);
		
		if(this.currentLevel != null){
			this.currentLevel.mouseUp(mouse);
		}
	}
	
	private void sheduleOnMouseMove(MouseInfo mouse){
		this.mouseX = mouse.getX();
		this.mouseY = mouse.getY();
		
		this.onMouseMove(mouse);
		
		if(this.currentLevel != null){
			this.currentLevel.mouseMove(mouse);
		}
	}
	
	protected abstract void onKeyDown(KeyInfo key);
	protected abstract void onKeyUp(KeyInfo key);
	protected abstract void onKeyType(KeyInfo key);
	
	protected abstract void onMouseDown(MouseInfo mouse);
	protected abstract void onMouseUp(MouseInfo mouse);
	protected abstract void onMouseMove(MouseInfo mouse);
	
	private void setKeyDown(KeyInfo kc, boolean isDown){
		Boolean oldValue = keyStates.get(kc.getKeyCode());
		
		keyStates.put(kc.getKeyCode(), isDown);
		
		if(oldValue == null){
			if(isDown){
				sheduleOnKeyDown(kc);
			}else{
				sheduleOnKeyUp(kc);
			}
		}else if(oldValue){
			if(!isDown){
				sheduleOnKeyUp(kc);
			}
		}else if(!oldValue){
			if(isDown){
				sheduleOnKeyDown(kc);
			}
		}
	}
	
	public void quit(){
		if(timer != null) timer.stop();
		
		Platform.exit();
	}
	
	public boolean isKeyDown(KeyCode kc){
		if(keyStates.containsKey(kc)){
			if(keyStates.get(kc) != null){
				return keyStates.get(kc);
			}
		}
		
		return false;
	}
	
	public Level getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
	}

	public int getFPS() {
		return fps;
	}

	public String getName() {
		return "Nasso Engine Game";
	}
	
	public float getWidth(){
		return (float) gameCanvas.getWidth();
	}
	
	public float getHeight(){
		return (float) gameCanvas.getHeight();
	}

	public float getMouseX() {
		return mouseX;
	}

	public float getMouseY() {
		return mouseY;
	}
}
