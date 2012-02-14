package runtime;

import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

public class FixtureUserData {
	public ReadableColor color;
	public boolean filled = false;
	
	public FixtureUserData(ReadableColor color, boolean filled){
		this.color = color;
		this.filled = filled;
	}
}
