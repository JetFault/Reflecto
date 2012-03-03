package jlwglbox2dframework;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.lwjgl.input.Mouse;

public class GameUtilities {
	public static Vec2 screenCoordsToProjectionCoords(int x, int y, GameSettings settings){
		float conversionRatio = settings.getProjectionHeight() / settings.getScreenHeight();
		
		float posX = x * conversionRatio;
		float posY = y * conversionRatio;
		
		return new Vec2(posX, posY);
	}
	
	public static int[] projectionCoordsToScreenCoords(Vec2 pos, GameSettings settings){
		int[] ret = new int[2];
		
		float conversionRatio = settings.getScreenHeight() / settings.getProjectionHeight();
		
		ret[0] = MathUtils.round(pos.x * conversionRatio);
		ret[1] = MathUtils.round(pos.y * conversionRatio);
				
		return ret;
	}
	
	public static Vec2 getMousePosition(GameSettings settings){
		return screenCoordsToProjectionCoords(Mouse.getX(), Mouse.getY(), settings);
	}
}
