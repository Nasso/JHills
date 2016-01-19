package org.nasso.jhills;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.nasso.engine.Game;
import org.nasso.engine.KeyInfo;
import org.nasso.engine.Level;

public class HillsLevel extends Level {
	public static final float METER_SCALE = 100f;
	
	private Map map;
	private Camera cam;
	private Player player;
	
	private World world;
	
	private boolean camFollowPlayer = true;
	private boolean pause = false;
	
	// Box2D objects
	private Body groundBody;
	
	public HillsLevel(Game game, long mapSeed) {
		super(game);
		
		world = new World(new Vec2(0.0f, -9.84f), true);
		
		map = new Map(1000, 16, 3f, mapSeed);
		cam = new Camera();
		player = new Player(world);
		
		// Ground body creation
		BodyDef groundDef = new BodyDef();
		groundDef.position.set(0.0f, 0.0f);
		
		groundBody = world.createBody(groundDef);
		
		for(int i = 0; i < map.getPreciseHeights().length-1; i++){
			float xspace = map.getPreciseXSpace();
			
			PolygonShape shape = new PolygonShape();
			shape.set(new Vec2[]{
					new Vec2(i * xspace, map.getPreciseHeightAt(i)),
					new Vec2((i+1) * xspace, map.getPreciseHeightAt(i+1)),
					new Vec2((i+1) * xspace, 0),
					new Vec2(i * xspace, 0)
			}, 4);
			
			groundBody.createFixture(shape, 0f);
		}
		
		// Add walls
		PolygonShape leftWall = new PolygonShape();
		leftWall.set(new Vec2[]{
				new Vec2(-1, 0),
				new Vec2(0, 0),
				new Vec2(0, 100),
				new Vec2(-1, 100)
		}, 4);
		PolygonShape rightWall = new PolygonShape();
		rightWall.set(new Vec2[]{
				new Vec2(map.getMeterWidth(), 0),
				new Vec2(map.getMeterWidth() + 1, 0),
				new Vec2(map.getMeterWidth() + 1, 100),
				new Vec2(map.getMeterWidth(), 100)
		}, 4);
		
		groundBody.createFixture(leftWall, 0f);
		groundBody.createFixture(rightWall, 0f);
	}
	
	public void renderLevel(GraphicsContext gtx) {
		gtx.setFont(Font.font("Arial", 22));
		
		// Clear
		gtx.setFill(Color.web("#7EC0EE"));
		gtx.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		gtx.save(); // SCALED
			gtx.scale(METER_SCALE, METER_SCALE);
			
			float camX = cam.getXPos();
			float camY = cam.getYPos();
			
			if(camFollowPlayer){
				camX = (float) (player.getXPos() + player.getRadius() - this.getMeterWidth()/2);
				camX = (float) Math.max(0, Math.min(map.getMeterWidth() - this.getMeterWidth(), camX));
				
				camY = (float) (player.getYPos() + player.getRadius() - this.getMeterHeight()/4 * 3);
				camY = Math.max(0, camY);
				
				cam.setXPos(camX);
				cam.setYPos(camY);
			}
			
			gtx.save(); // VIEW_SPACE
				gtx.translate(0, this.getMeterHeight());
				gtx.scale(1, -1);
				
				gtx.translate(-camX, -camY);
				
				gtx.setFill(Color.web("#784800"));
				gtx.setStroke(Color.web("#7CFC00"));
				gtx.setLineWidth(0.08f);
				gtx.beginPath();
					int startAt = (int) (camX / map.getPreciseXSpace());
					int endAt = (int) ((camX + this.getMeterWidth()) / map.getPreciseXSpace()) + 5; // 5 is an arbitrary offset
					
					for(int i = startAt; i < endAt; i++){
						float x = i * map.getPreciseXSpace();
						float y = map.getPreciseHeightAt(i);
						
						gtx.lineTo(x, y);
					}
					
					gtx.stroke();
					
					gtx.lineTo(endAt * map.getPreciseXSpace(), 0);
					gtx.lineTo(startAt * map.getPreciseXSpace(), 0);
					
					gtx.fill();
				gtx.closePath();
				
				gtx.save(); // PLAYER_SPACE
					gtx.translate(player.getXPos() + player.getRadius(), player.getYPos() + player.getRadius());
					gtx.rotate(Math.toDegrees(player.getAngle()));
					
					gtx.setFill(Color.web("#FFFFFF"));
					gtx.fillArc(-player.getRadius(), -player.getRadius(), player.getRadius()*2, player.getRadius()*2, 0, 360, ArcType.OPEN);
				gtx.restore(); // !PLAYER_SPACE
			gtx.restore(); // !VIEW_SPACE
		gtx.restore(); // !SCALED
		
		float playerProgress = player.getXPos() / map.getMeterWidth();
		gtx.setFill(Color.web("#CCCCCC"));
		gtx.fillRect(0, this.getHeight()-12, this.getWidth(), 12);
		gtx.setFill(Color.web("#00DD00"));
		gtx.fillRect(0, this.getHeight()-12, playerProgress * this.getWidth(), 12);
		
		int currentLine = 1;
		int lineHeight = 30;
		gtx.setFill(Color.web("#FFFFFF"));
		gtx.fillText("FPS: "+this.getGame().getFPS(), 12, (currentLine++) * lineHeight);
		gtx.fillText("Map seed: "+map.getSeed(), 12, (currentLine++) * lineHeight);
		gtx.fillText("Follow player (F): "+camFollowPlayer, 12, (currentLine++) * lineHeight);
		
		if(pause){
			gtx.setFill(Color.web("rgba(0, 0, 0, 0.5)"));
			gtx.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}
	
	private float getMeterHeight() {
		return this.getGame().getHeight() / METER_SCALE;
	}

	private float getMeterWidth() {
		return this.getGame().getWidth() / METER_SCALE;
	}

	public void keyDown(KeyInfo key) {
		if(key.getKeyCode() == KeyCode.F){
			camFollowPlayer = !camFollowPlayer;
		}
		if(key.getKeyCode() == KeyCode.P){
			pause = !pause;
		}
	}
	
	public void keyUp(KeyInfo key) {
		
	}
	
	public void keyType(KeyInfo key) {
		
	}
	
	public void scroll(int side) {
		float camXPos = cam.getXPos();
		
		camXPos += side;
		
		camXPos = (float) Math.max(0, Math.min(map.getMeterWidth() - this.getWidth(), camXPos));
		
		cam.setXPos(camXPos);
	}
	
	public void step(float delta, float now) {
		if(!pause){
			world.step(delta / 1000.0f, 16, 8);
		}
		
		Body playerBody = player.getBody();
		Vec2 playerPos = playerBody.getPosition();
		player.setXPos(playerPos.x - player.getRadius());
		player.setYPos(playerPos.y - player.getRadius());
		player.setAngle(playerBody.getAngle());

		if(!pause){
			if(getGame().isKeyDown(KeyCode.D)){
				playerBody.applyTorque(-8);
			}
			if(getGame().isKeyDown(KeyCode.Q)){
				playerBody.applyTorque(8);
			}
		}
	}
}
