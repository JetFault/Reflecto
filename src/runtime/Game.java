
package runtime;

import org.jbox2d.common.Vec2;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

// This line lets me say stuff like glOrtho() instead of GL11.glOrtho()
import static org.lwjgl.opengl.GL11.*;


public class Game {
	
	public static final Vec2 gravity = new Vec2(0, -9.8f);
	public static final int screenWidth = 1440;
	public static final int screenHeight = 900;

	public static final double aspectRatio = (double)screenWidth / (double)screenHeight;
	
	public static final double projectionHeight = 100;
	public static final double projectionWidth = aspectRatio * projectionHeight;
	
	public static final int drawRate = 60; // the number of frames to draw per second
	public static final long drawDuration = 1000 / drawRate; // The amount of time a frame should take to draw
	
	public static final int updateRate = 60; // The number of times per second to update the game world
	public static final long updateDuration = 1000 / updateRate; // The amount of time a physics update should take
	
	// Physics simulation parameters
	public static final float timeStep = 1f / updateRate;
	public static final int velocityIterations = 6;
	public static final int positionIterations = 2;
	
	public static final long gameStartTime = getTime();
	public static final GameWorld gameWorld = new GameWorld();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			DisplayMode dm = new DisplayMode(Game.screenWidth, Game.screenHeight);
			Display.setDisplayMode(dm);
			Display.create();
		}
		catch (LWJGLException e) {
			System.err.println(e.getMessage());
		}
		
		// Set up projection matrix for 2d world!
		setupProjection();
		gameWorld.initialize(gravity, false);
		
		long start;
		long end;
		
		// OH HEY IT'S THE MAIN GAME LOOP
		while (!Display.isCloseRequested()) {
			start = getTime();
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			// Simulate physics world
			gameWorld.step();
			
			// Draw physics objects
			gameWorld.draw();
			
			// Draw non physics objects
			
			Display.update();
			
			end = getTime();
			long frameDuration = end - start;
			
			// Sleep for any extra time.
			if (frameDuration < drawDuration){
				try {
					Thread.sleep(drawDuration - frameDuration);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
		
		Display.destroy();
	}
	
	// Gets the current system time
	public static long getTime(){
		return System.nanoTime() / 1000000;
	}
	
	// Returns the amount of time that has passed since the game started
	public static long getGameTime(){
		return getTime() - gameStartTime;
	}
	
	private static void setupProjection() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, projectionWidth, 0, projectionHeight, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		
		glLoadIdentity();
	}
}
