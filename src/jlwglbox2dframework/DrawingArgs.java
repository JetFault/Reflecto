package jlwglbox2dframework;

import org.lwjgl.util.ReadableColor;

public class DrawingArgs {
	public ReadableColor color;
	public boolean filled = false;
	
	public DrawingArgs(ReadableColor color, boolean filled){
		this.color = color;
		this.filled = filled;
	}
}
