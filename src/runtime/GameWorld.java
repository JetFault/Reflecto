package runtime;

import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.ArrayList;

import jlwglbox2dframework.DrawingArgs;
import jlwglbox2dframework.DrawingTool;
import jlwglbox2dframework.Game;
import jlwglbox2dframework.GameSettings;
import jlwglbox2dframework.GameUtilities;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

public class GameWorld extends Game implements ContactListener{
	
	boolean restart = false;
	
	/**
	 * Game States
	 */
	boolean multiBallMode = false;
	boolean firstTime = true;
	float multiBallStartTime;
	float multiBallTimeLimit = 4000f;
	
	boolean rightButtonClicked = false;
	float beginClickTime;
	float impulseTimeLimit = 1000f;

	public Shooter mainShooter;

	public ArrayList<Mirror> mirrors;
	public ArrayList<Enemy> enemies;
	
	public ArrayList<Body> mirrorsDestroy = new ArrayList<>();
	public ArrayList<Body> bulletDestroy = new ArrayList<>();
	public ArrayList<Body> enemyDestroy = new ArrayList<>();
	

	ArrayList<Bullet> tempArrayList = new ArrayList<>();
	
	public int lives = 5;
	
	public int score = 0;
	public int pointsPerKill = 100;

	private long oldTime;
	
	private long oldTimeStartLevel;
	private long levelTimeLimit = 10000;
	private int currLevel = 1;
	
	private long nextSpawn;

	public long timeLimit = 2000;

	public boolean drawingMirror = false;
	public int maxNumMirrors = 5;
	
	boolean drawArrow = true;

	public static void main(String[] args){
		GameSettings settings = new GameSettings();
		settings.showHardwareCursor = true;
		settings.gravity = new Vec2(0, 0);

		GameWorld world = new GameWorld(settings);
		world.run();

	}

	public GameWorld(GameSettings settings) {
		super(settings);
		mirrors = new ArrayList<>();
		enemies = new ArrayList<>();
	}
	
	public void spawnEnemies() { 
		float x = MathUtils.randomFloat(5, settings.getProjectionWidth()-5);
		float y = MathUtils.randomFloat(5, settings.getProjectionHeight()-5);
		Vec2 pos = new Vec2(x, y);		
		Enemy enemy = new Enemy(pos, physicsWorld);
		enemies.add(enemy);
	}

	@Override
	protected void initialize(){
		super.initialize();
		
		Vec2 midpt = new Vec2(settings.getProjectionWidth()/2, settings.getProjectionHeight()/2);
		mainShooter = new Shooter(midpt,physicsWorld);
		this.oldTime = getGameTime();
		this.oldTimeStartLevel = getGameTime();
		
		spawnEnemies();
		
		physicsWorld.setContactListener(this);
	}


	@Override
	protected void update(){
		super.update();
		long currTime = this.getGameTime();
		
		killAllTheThings();
		
		//Delete bullets offscreen
		for( Bullet bullet : mainShooter.bullets ) {
			if(bullet.isOffScreen(settings)) {
				tempArrayList.add(bullet);				
			}
		}		
		for( Bullet tmpBullet : tempArrayList) {
			mainShooter.killBullet(tmpBullet.body);
			if(!tmpBullet.multiBall) {
				lives--;
				System.out.println("Lives: " + lives);
			}
			//Death
			if(lives == 0) {
				System.out.println("You lose!");
				restart = true;
				setGameSpeed(0f);
				endGame();
			}
		}
		tempArrayList.clear();

		
		//Is it time to fire the Shooter?!
		if (mainShooter.readyToFire && ((currTime - oldTime) > timeLimit)) {
			
			if(multiBallMode) {
				if(firstTime) {
					multiBallStartTime = getGameTime();				
					mainShooter.multiBallPreviewFire(2);
					firstTime = false;
				}
				if((getGameTime() - multiBallStartTime) > multiBallTimeLimit) {
					mainShooter.multiBallFire();
					multiBallMode = false;					
				}
			//	mainShooter.rotate(mainShooter.getDesiredAngle());
			}
			else {
				//Fire
				mainShooter.fire();

				//Rotate
				float randDegrees = MathUtils.randomFloat(-355, 355);
				mainShooter.rotate(randDegrees);
			}
			
			//Increase Levels
			if((currTime - oldTimeStartLevel) > levelTimeLimit) {
				currLevel++;
				System.out.println("\tLevel " + currLevel);
				oldTimeStartLevel = currTime;
				mainShooter.bulletSpeed += 3.0f;
			}
			
			mainShooter.readyToFire = false;
		}
		//Is the shooter at the destination angle?
		if( !mainShooter.readyToFire && (MathUtils.abs((mainShooter.getAngle() - mainShooter.getDesiredAngle())) < 1)) {
			mainShooter.stopRotate();
			mainShooter.readyToFire = true;
			
			spawnEnemies();

			this.oldTime = currTime;
		}

		//Impulse
		if(rightButtonClicked && (getGameTime() - beginClickTime) < impulseTimeLimit) {
			Vec2 mouseLoc = GameUtilities.getMousePosition(settings);
			for(Bullet bullet : mainShooter.bullets) {
				Vec2 pos = bullet.body.getPosition();
				Vec2 impulse = mouseLoc.sub(pos);
				impulse.normalize();
				
				float factor = 1/(MathUtils.distance(pos, mouseLoc));
				factor *= 2;
				factor = MathUtils.max(factor, 0.9f);
				
				impulse.mulLocal(factor);
				bullet.body.applyLinearImpulse(impulse, mouseLoc);
			}
		}
		
		
		if(drawingMirror) {
			Mirror mirror = this.mirrors.get(mirrors.size()-1);
			mirror.temporaryEnd = GameUtilities.getMousePosition(settings);
		}
	}

	@Override
	protected void draw(){
		super.draw();

		if((mirrors.size() != 0) && drawingMirror) {
			Mirror mirror = this.mirrors.get(mirrors.size()-1);
			mirror.draw();
		}
		
		if(drawArrow) {
			mainShooter.drawDirection();
		}
		
		if(rightButtonClicked) {
			DrawingTool.setColor(Color.ORANGE);
			Vec2 pos = GameUtilities.getMousePosition(settings);
			DrawingTool.drawCircle(pos, 0, 2, 30, new DrawingArgs(ReadableColor.ORANGE, false));
		}
	}

	
	
	/**
	 * The below code is absolutely horrific.
	 * This is why coding during an allnighter should not be done.
	 * Honestly though, what was I even thinking?!
	 * Why not just use a damn HashMap... or just not be dumb -_-
	 */
	

	/**
	 * Killing Objects
	 */
	public void killMirror(Body body) {
		ArrayList<Mirror> enemiesToKill = new ArrayList<>();
		for (Mirror mirror : mirrors) {
			if(mirror.body == body) {
				physicsWorld.destroyBody(body);
				enemiesToKill.add(mirror);
			}
		}
		
		for(Mirror enemy : enemiesToKill) {
			mirrors.remove(enemy);
		}
		enemiesToKill.clear();
	}
	
	public void killEnemy(Body body) {
		ArrayList<Enemy> enemiesToKill = new ArrayList<>();
		for (Enemy enemy : enemies) {
			if(enemy.body == body) {
				score += pointsPerKill;
				System.out.println("\tScore: " + score);
				physicsWorld.destroyBody(body);
				enemiesToKill.add(enemy);
			}
		}
		
		for(Enemy enemy : enemiesToKill) {
			enemies.remove(enemy);
		}
		enemiesToKill.clear();
	}
	
	public void killAllTheThings() {
		for(Body bullet : bulletDestroy) {
			mainShooter.killBullet(bullet);
		}
		for(Body enemy : enemyDestroy) {
			killEnemy(enemy);
		}
		for(Body mirror : mirrorsDestroy) {
			killMirror(mirror);
		}
		bulletDestroy.clear();
		enemyDestroy.clear();
		mirrorsDestroy.clear();
	}

	
	
	
	
	/**
	 * Collision Detection
	 */
	
	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		Body cont1 = contact.m_fixtureA.getBody();
		Body cont2 = contact.m_fixtureB.getBody();

		String str1 = (String) cont1.getUserData();
		String str2 = (String) cont2.getUserData();
		
		boolean hitMirror = false;
		
		boolean bullet1 = false;
		boolean bullet2 = false;
		
		boolean enemy1 = false;
		boolean enemy2 = false;
		
		boolean mirror1 = false;
		boolean mirror2 = false;

		if(str1 != null) {
			if ( (str1.compareTo("bullet") == 0) ) {
				bullet1 = true;
//				bulletDestroy.add(cont1);
			}
			else if ( (str1.compareTo("mirror") == 0) ) {
				hitMirror = true;
				mirror1 = true;
//				mirrorsDestroy.add(cont1);
			}
			else if ( (str1.compareTo("enemy") == 0) ) {
				enemy1 = true;
//				enemyDestroy.add(cont1);
			}
		}
		
		if(str2 != null) {
			if ( (str2.compareTo("bullet") == 0) ) {
				bullet2 = true;
//				bulletDestroy.add(cont2);
			}
			else if ( (str2.compareTo("mirror") == 0) ) {
				hitMirror = true;
				mirror2 = true;
//				mirrorsDestroy.add(cont2);
			}
			else if ( (str2.compareTo("enemy") == 0) ) {
				enemy2 = true;
//				enemyDestroy.add(cont2);
			}
		}
		
		if(bullet1 && bullet2) {
			return;
		}
		if(enemy1 && enemy2) {
			return;
		}
		
		if(!hitMirror && (bullet1 || bullet2)) {
			if(bullet1){
				bulletDestroy.add(cont1);
			}
			if(bullet2){
				bulletDestroy.add(cont2);
			}
		}
		
		if(!hitMirror && (enemy1 || enemy2)) {
			if(enemy1) {
				enemyDestroy.add(cont1);
			}
			if(enemy2) {
				enemyDestroy.add(cont2);
			}
		}
		if(hitMirror && (enemy1 || enemy2)) {
			if(mirror1) {
				mirrorsDestroy.add(cont1);
			}
			if(mirror2) {
				mirrorsDestroy.add(cont2);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		Body cont1 = contact.m_fixtureA.getBody();
		Body cont2 = contact.m_fixtureB.getBody();

		String str1 = (String) cont1.getUserData();
		String str2 = (String) cont2.getUserData();
		
		boolean hitBullet = false;
		
		boolean mirror1 = false;
		boolean mirror2 = false;

		if(str1 != null) {
			if ( (str1.compareTo("mirror") == 0) ) {
				mirror1 = true;
			}
			else if ( (str2.compareTo("bullet") == 0) ) {
				hitBullet = true;
//				bulletDestroy.add(cont2);
			}
		}
		
		if(str2 != null) {
			if ( (str2.compareTo("mirror") == 0) ) {
				mirror2 = true;
			}
			else if ( (str2.compareTo("bullet") == 0) ) {
				hitBullet = true;
				
//				bulletDestroy.add(cont2);
			}
		}
		
		if(hitBullet && (mirror1 || mirror2)) {
			if (mirror1) {
				mirrorsDestroy.add(cont1);
			}
			if (mirror2) {
				mirrorsDestroy.add(cont2);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}
	
	
	/**
	 * KEY EVENTS
	 */	
	@Override
	protected void onMouseClick(int button){
		Vec2 mouseLoc = GameUtilities.getMousePosition(settings);
		
		if(button == 0) {
			Mirror mir = new Mirror(mouseLoc, getGameTime(), physicsWorld);
			mirrors.add(mir);
			drawingMirror = true;
		}
		else if(button == 1) {
			rightButtonClicked = true;
			beginClickTime = getGameTime();
		}
		
		
	}
	
	@Override
	protected void onMouseRelease(int button){
		if(button == 0) {
			Vec2 end = GameUtilities.getMousePosition(settings);
			if(mirrors.size() != 0) {
				Mirror mirror = mirrors.get(mirrors.size()-1);
				mirror.endMirror(end);
			}

			if(mirrors.size() > maxNumMirrors) {
				physicsWorld.destroyBody(mirrors.get(0).body);
				mirrors.remove(0);
			}

		drawingMirror = false;
		}
		else if(button == 1) {
			rightButtonClicked = false;
		}
	}
	
	@Override
	protected void onKeyDown(int key){
		if(key == Keyboard.KEY_D) {
			drawArrow = !drawArrow;
		}
		
		if(key == Keyboard.KEY_Q) {
			multiBallMode = true;
			firstTime = true;
		}
	}
	
	@Override
	protected void onKeyUp(int key){
		
	}
}
