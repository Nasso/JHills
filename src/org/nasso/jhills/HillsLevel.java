package org.nasso.jhills;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.nasso.engine.Game;
import org.nasso.engine.KeyInfo;
import org.nasso.engine.Level;
import org.nasso.engine.MouseInfo;

public class HillsLevel extends Level {
	public static final float METER_SCALE = 100f;
	
	private Map map;
	private Camera cam;
	private Player player;
	
	private World world;
	
	private boolean camFollowPlayer = true;
	private boolean pause = false;
	private boolean losed = false;
	
	private ArrayList<Coin> coins = new ArrayList<Coin>();
	private ArrayList<Jerrycan> jerries = new ArrayList<Jerrycan>();
	private Image coinImg;
	private Image jerryImg;
	
	private int score = 0;
	private String errorMessage = "YOU LOSE!";
	
	// Box2D objects
	private Body groundBody;
	private ArrayList<Body> coinsBodies = new ArrayList<Body>();
	private ArrayList<Body> jerriesBodies = new ArrayList<Body>();
	
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
		
		// Init coins
		int jerryCount = 0;
		int coinCount = 0;
		for(int i = 1; i < (int) (map.getHeights().length/5f); i++){
			if(i % 10 == 0){ // Jerry all 10 units
				Jerrycan cn = new Jerrycan(map.getXSpace() * i * 5, map.getHeightAt(i * 5) + 0.6f);
				
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(cn.getSize(), cn.getSize());
				
				BodyDef def = new BodyDef();
				def.position.set(cn.getX(), cn.getY());
				
				Body cnBody = world.createBody(def);
				
				FixtureDef fixDef = new FixtureDef();
				fixDef.shape = shape;
				fixDef.density = 0f;
				fixDef.isSensor = true;
				fixDef.userData = "JERRY_FIXTURE_"+(jerryCount++);
				
				cnBody.createFixture(fixDef);
				
				jerries.add(cn);
				jerriesBodies.add(cnBody);
			}else{
				Coin cn = new Coin(1, map.getXSpace() * i * 5, map.getHeightAt(i * 5) + 0.6f);
			
				CircleShape shape = new CircleShape();
				shape.setRadius(cn.getRadius());
				
				BodyDef def = new BodyDef();
				def.position.set(cn.getX(), cn.getY());
				
				Body cnBody = world.createBody(def);
				
				FixtureDef fixDef = new FixtureDef();
				fixDef.shape = shape;
				fixDef.density = 0f;
				fixDef.isSensor = true;
				fixDef.userData = "COIN_FIXTURE_"+(coinCount++);
				
				cnBody.createFixture(fixDef);
				
				coins.add(cn);
				coinsBodies.add(cnBody);
			}
		}
		
		world.setContactListener(new ContactListener(){
			public void beginContact(Contact contact) {
				Fixture a = contact.getFixtureA();
				Fixture b = contact.getFixtureB();
				
				Object aData = a.getUserData();
				Object bData = b.getUserData();
				
				int coinIndex = -1;
				int jerryIndex = -1;
				
				if(aData != null && aData.toString().matches("^COIN_FIXTURE_[0-9]+") && b == player.getBallFix()){
					coinIndex = Integer.valueOf(aData.toString().substring(13));
					a.setUserData("DEAD");
				}else if(bData != null && bData.toString().matches("^COIN_FIXTURE_[0-9]+") && a == player.getBallFix()){
					coinIndex = Integer.valueOf(bData.toString().substring(13));
					b.setUserData("DEAD");
				}else if(aData != null && aData.toString().matches("^JERRY_FIXTURE_[0-9]+") && b == player.getBallFix()){
					jerryIndex = Integer.valueOf(aData.toString().substring(14));
					a.setUserData("DEAD");
				}else if(bData != null && bData.toString().matches("^JERRY_FIXTURE_[0-9]+") && a == player.getBallFix()){
					jerryIndex = Integer.valueOf(bData.toString().substring(14));
					b.setUserData("DEAD");
					
				}
				
				if(coinIndex >= 0){
					System.out.println("Destroy "+coinIndex);
					coins.set(coinIndex, null);
					world.destroyBody(coinsBodies.get(coinIndex));
					coinsBodies.set(coinIndex, null);
					
					score++; // yay we got an mlg pro gamer here
				}else if(jerryIndex >= 0){
					jerries.set(jerryIndex, null);
					world.destroyBody(jerriesBodies.get(jerryIndex));
					jerriesBodies.set(jerryIndex, null);
					
					player.setFuel(20); // nice skills dude
				}
			}
			
			public void endContact(Contact contact) {
				
			}
			
			public void preSolve(Contact contact, Manifold oldManifold) {
				
			}
			
			public void postSolve(Contact contact, ContactImpulse impulse) {
				
			}
		});
		
		try {
			coinImg = new Image(new FileInputStream("res/coin1.png"));
			jerryImg = new Image(new FileInputStream("res/jerrycan.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
				
				// Render coins
				for(int i = 0; i < coins.size(); i++){
					Coin cn = coins.get(i);

					if(cn == null){
						continue;
					}else if(coinsBodies.get(i) == null){
						continue;
					}else if(coinsBodies.get(i).getFixtureList().getUserData() == null){
						continue;
					}else if(coinsBodies.get(i).getFixtureList().getUserData().equals("DEAD")){
						continue;
					}
					
					if(!coinImg.isError() && !coinImg.isBackgroundLoading()){
						gtx.drawImage(coinImg, cn.getX()-cn.getRadius(), cn.getY()+cn.getRadius(), cn.getRadius()*2, -cn.getRadius()*2);
					}
				}
				
				for(int i = 0; i < jerries.size(); i++){
					Jerrycan cn = jerries.get(i);
					
					if(cn == null){
						continue;
					}else if(jerriesBodies.get(i) == null){
						continue;
					}else if(jerriesBodies.get(i).getFixtureList().getUserData() == null){
						continue;
					}else if(jerriesBodies.get(i).getFixtureList().getUserData().equals("DEAD")){
						continue;
					}
					
					if(!jerryImg.isError() && !jerryImg.isBackgroundLoading()){
						gtx.drawImage(jerryImg, cn.getX()-cn.getSize(), cn.getY()+cn.getSize(), cn.getSize()*2, -cn.getSize()*2);
					}
				}
				
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
		gtx.fillText("Score: "+score, 12, (currentLine++) * lineHeight);
		gtx.fillText("Fuel: "+Math.ceil(player.getFuel()), 12,(currentLine++) * lineHeight);
		gtx.fillText("Honk (A)", 12,(currentLine++) * lineHeight);
		
		if(losed){
			gtx.setFill(Color.web("rgba(0, 0, 0, 0.5)"));
			gtx.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			gtx.setFill(Color.web("#FFFFFF"));
			gtx.setFont(Font.font("Arial", 64));
			gtx.setTextAlign(TextAlignment.CENTER);
			gtx.fillText(errorMessage, this.getWidth()/2, this.getHeight()/2);
		}
		if(pause && !losed){
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
		if(key.getKeyCode() == KeyCode.A){
			Media sound = new Media(new File("res/honk.wav").toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(sound);
			mediaPlayer.setVolume(0.1);
			mediaPlayer.play();
		}
	}
	
	public void keyUp(KeyInfo key) {
		
	}
	
	public void keyType(KeyInfo key) {
		
	}
	
	public void scroll(int side) {
		float camXPos = cam.getXPos();
		
		camXPos += side / METER_SCALE;
		
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
		
		
		if(!pause && !losed){
			if(player.getFuel()<= 0){
				losed = true;
			
			}
			if(getGame().isKeyDown(KeyCode.D)){
				playerBody.applyTorque(-8);
			}
			if(getGame().isKeyDown(KeyCode.Q)){
				playerBody.applyTorque(8);
			}
			
			player.setFuel(player.getFuel()-delta/1000);
		}
	}
	
	public void mouseDown(MouseInfo mouse) {
		
	}
	
	public void mouseUp(MouseInfo mouse) {
		
	}
	
	public void mouseMove(MouseInfo mouse) {
		
	}
}
