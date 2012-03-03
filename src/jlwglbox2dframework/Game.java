/**
 * 
 */
package jlwglbox2dframework;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.util.HashMap;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * @author Jeremy Schiff
 *
 */
public class Game {
	
	protected final GameSettings settings;
	
	protected final long gameStartTime = getTime();
	
	private HashMap<Integer, Boolean> mouseStates = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> keyStates = new HashMap<Integer, Boolean>();
	
	
	protected World physicsWorld;
	private float _gameSpeed = 1.0f;
	private boolean _quit = false;
	
	public Game(GameSettings settings){
		// Set up the world
		this.settings = settings;
		physicsWorld = new World(settings.gravity, settings.allowSleep);
	}
	
	public void run(){
		try {
			DisplayMode dm = new DisplayMode(settings.getScreenWidth(), settings.getScreenHeight());
			Display.setDisplayMode(dm);
			Display.create();
			DrawingTool.initialize();
		}
		catch (LWJGLException e) {
			System.err.println(e.getMessage());
		}
		DrawingTool.initialize(settings);
		initialize();
		
		Mouse.setGrabbed(!settings.showHardwareCursor);
		Mouse.setCursorPosition(settings.getScreenWidth() / 2, settings.getScreenHeight() / 2);
		
		long start, end, frameDuration;
		// OH HEY IT'S THE MAIN GAME LOOP
		while (!Display.isCloseRequested() && !_quit) {
			// Handle input events
			start = getGameTime();
			
			handleInput();
			update();
			draw();
			
			Display.update();
			end = getGameTime();
			frameDuration = end - start;
			
			// Sleep for any extra time.
			if (frameDuration < settings.drawDuration){
				try {
					Thread.sleep(settings.drawDuration - frameDuration);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
			else{
				System.err.println("Warning, the game is not being drawn or updated fast enough to achieve the desired refresh rate.");
			}
		}
		
		Display.destroy();
	}
	
	protected void draw(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		drawPhysicsWorld();
	}
	
	protected void update(){
		if (_gameSpeed > 0){
			physicsWorld.step(settings.timeStep * _gameSpeed, settings.velocityIterations, settings.positionIterations);	
		}
	}
	
	// Gets the current system time in milliseconds
	private static long getTime(){
		return System.nanoTime() / 1000000;
	}
	
	// Returns the amount of time that has passed since the game started
	public long getGameTime(){
		return getTime() - gameStartTime;
	}
	
	protected void handleInput() {
		// Handle keyboard input
		while (Keyboard.next()){
			int key = Keyboard.getEventKey();
			boolean down = Keyboard.getEventKeyState();
			if (down){
				onKeyDown(key);
			}
			else {
				onKeyUp(key);
			}
		}
		
		// Handle mouse input
		while (Mouse.next()){
			int button = Mouse.getEventButton();
			
			// Mouse click event
			if (button != -1){
				boolean down = Mouse.getEventButtonState();
				if (down){
					onMouseClick(button);
				}
				else {
					onMouseRelease(button);
				}
			}
			
			// Mouse move event
			int dy = Mouse.getDY();
			int dx = Mouse.getDX();
			if (dy > 0 || dx > 0){
				Vec2 mousePos = GameUtilities.getMousePosition(this.settings);
				onMouseMoved(dx, dy, mousePos);
			}
			
			// Mouse scroll event
			if(Mouse.hasWheel()){
				int scroll = Mouse.getEventDWheel();
				if (scroll > 0){
					onMouseScroll(scroll);
				}
			}
		}
	}

	// Draw the world
	protected void drawPhysicsWorld(){
		// Iterate over all the bodies in the world and draw each of their fixtures
		for (Body body = physicsWorld.getBodyList(); body != null; body = body.getNext()){
			DrawingTool.drawBody(body);
		}
	}
	
	// subclass should override these methods to deal with input
	protected void onMouseScroll(int scroll) {
	}

	protected void onMouseClick(int button){
	}
	
	protected void onMouseRelease(int button){	
	}
	
	protected void onKeyDown(int key){
	}
	
	protected void onKeyUp(int key){
		
	}
	
	protected void onMouseMoved(int dx, int dy, Vec2 endPosition){
	}
	
	public boolean getMouseButtonState(int button){
		return mouseStates.get(button);
	}
	
	public boolean getKeyState(int key){
		return keyStates.get(key);
	}
	
	protected void setGameSpeed(float factor){
		_gameSpeed = factor;
	}
	
	protected void pausePhysics(){
		setGameSpeed(0);
	}
	
	protected float getGameSpeed(){
		return _gameSpeed;
	}
	
	protected void initialize(){
	}
	
	public void endGame(){
		_quit = true;
	}
}
