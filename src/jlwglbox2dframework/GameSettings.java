package jlwglbox2dframework;

import org.jbox2d.common.Vec2;

public class GameSettings {
	private int screenWidth = 1024;
	private int screenHeight = 768;

	private float aspectRatio = (float)screenWidth / (float)screenHeight;
	
	private float projectionHeight = 100;
	private float projectionWidth = aspectRatio * projectionHeight;
	
	public int drawRate = 50; // the number of frames to draw per second
	public long drawDuration = 1000 / drawRate; // The amount of time a frame should take to draw
	
	public int updateRate = 50; // The number of times per second to update the game world
	public long updateDuration = 1000 / updateRate; // The amount of time a physics update should take
	
	// Physics simulation parameters
	protected float timeStep = 1f / updateRate;
	protected int velocityIterations = 6;
	protected int positionIterations = 2;
	public Vec2 gravity = new Vec2(0, -10);
	public boolean allowSleep = true;
	public boolean showHardwareCursor = false;
	
	public int getScreenWidth() {
		return screenWidth;
	}
	public int getScreenHeight() {
		return screenHeight;
	}
	public float getAspectRatio() {
		return aspectRatio;
	}
	public float getProjectionHeight() {
		return projectionHeight;
	}
	public float getProjectionWidth() {
		return projectionWidth;
	}
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
		aspectRatio = (float)screenWidth / (float)screenHeight;
		projectionWidth = aspectRatio * projectionHeight;
	}
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
		aspectRatio = (float)screenWidth / (float)screenHeight;
		projectionWidth = aspectRatio * projectionHeight;
	}
	public void setProjectionHeight(float projectionHeight) {
		this.projectionHeight = projectionHeight;
		projectionWidth = aspectRatio * projectionHeight;
	}
}
