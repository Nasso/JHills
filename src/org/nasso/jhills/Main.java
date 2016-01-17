package org.nasso.jhills;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	private JHills jh;
	private AnimationTimer timer;
	
	private float lastFrameTime = -1;
	
	private Scene createScene(){
		jh = new JHills();
		
		BorderPane root = new BorderPane();
		root.setCenter(jh);
		
		Scene sce = new Scene(root);
		
		sce.setOnKeyPressed(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent event) {
				jh.setKeyDown(event.getCode(), true);
				
				if(event.getCode() == KeyCode.ESCAPE){
					if(timer != null) timer.stop();
					
					Platform.exit();
				}
			}
		});
		
		sce.setOnKeyReleased(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent event) {
				jh.setKeyDown(event.getCode(), false);
			}
		});
		
		sce.setOnScroll(new EventHandler<ScrollEvent>(){
			public void handle(ScrollEvent event) {
				jh.onScroll((int) -event.getDeltaY()); 
			}
		});
		
		return sce;
	}
	
	public void start(Stage stg) throws Exception {
		stg.setTitle("JHills");
		stg.setFullScreen(true);
		stg.setFullScreenExitHint("");
		stg.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stg.setScene(createScene());
		
		stg.show();
		
		jh.setWidth(stg.getWidth());
		jh.setHeight(stg.getHeight());
		
		timer = new AnimationTimer(){
			public void handle(long now) {
				float nowms = (float) (now / 1000000);
				
				if(lastFrameTime < 0){
					lastFrameTime = nowms;
				}
				
				float delta = nowms - lastFrameTime;
				
				jh.loop(delta, nowms);
				
				lastFrameTime = nowms;
			}
		};
		
		timer.start();
	}
	
	public static void main(String[] args){
		Main.launch(args);
	}
}
