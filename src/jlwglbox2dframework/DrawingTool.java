package jlwglbox2dframework;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.lwjgl.util.ReadableColor;

public class DrawingTool {
	
	private static Vec2[] squareVectors;
	private static boolean initialized = false;
	private static Vec2 cameraPosition;
	private static GameSettings settings;
	
	private static void _initialize(GameSettings settings){
		DrawingTool.settings = settings;
		
		Vec2 squareTL = new Vec2(-.5f, +.5f);
		Vec2 squareTR = new Vec2(+.5f, +.5f);
		Vec2 squareBL = new Vec2(-.5f, -.5f);
		Vec2 squareBR = new Vec2(+.5f, -.5f);
		squareVectors = new Vec2[] { squareTL, squareTR, squareBR, squareBL };
		
		cameraPosition = new Vec2(0, 0);
		setCameraPosition(cameraPosition);
		initialized = true;
	}
	
	public static void initialize(GameSettings settings){
		if (!initialized) _initialize(settings);
	}
	
	// initializes with the default game settings
	public static void initialize(){
		if (!initialized) _initialize(new GameSettings());
	}
	
	public static void drawBody(Body body) {
		Vec2 position = body.getPosition();
		float angle = body.getAngle() * MathUtils.RAD2DEG;
		
		for (Fixture fixture = body.getFixtureList(); fixture != null; fixture = fixture.getNext()){
			ShapeType type = fixture.getType();
			DrawingArgs userData = (DrawingArgs)fixture.getUserData();
			
			switch (type) {
			case CIRCLE : 
				CircleShape circleShape = (CircleShape)fixture.getShape();
				float radius = circleShape.m_radius;
				drawCircle(position, angle, radius, 300, userData);
				// draw circle using center and radius
				break;
			case POLYGON :
				PolygonShape polygonShape = (PolygonShape)fixture.getShape();
				Vec2[] vertices = polygonShape.m_vertices;
				drawPolygon(position, angle, vertices, userData);
				// Draw polygon using vertices
				break;
			case UNKNOWN :
				// Try to draw vertices I guess?  Maybe it's a line thing.
				break;
			default:
				break;
			}
		}
	}

	private static void setFilled(boolean filled) {
		initialize();
		// TODO Auto-generated method stub
		if (filled)	glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		else glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	}

	public static void drawCircle(Vec2 center, float angle, float r, int num_segments, DrawingArgs userData) { 	
		initialize();
		float theta = 2 * MathUtils.PI / num_segments; 
		float tangetial_factor = (float)Math.tan(theta);

		float radial_factor = MathUtils.cos(theta);//calculate the radial factor 

		float x = 1;//we start at angle = 0 

		float y = 0; 

		setFilled(userData.filled);
		setColor(userData.color);
		glPushMatrix();
		glTranslatef(center.x, center.y, 0);
		glScalef(r, r, 1);
		glRotated(angle, 0, 0, 1);
		glBegin(GL_POLYGON); 
		for(int ii = 0; ii < num_segments; ii++) 
		{ 
			// Sin/cos method
//			x = MathUtils.cos(((float)ii / num_segments) * MathUtils.TWOPI);
//			y = MathUtils.sin(((float)ii / num_segments) * MathUtils.TWOPI);
			
			glVertex2f(x/* + center.x*/, y/* + center.y*/);//output vertex 

			// Other method
			//calculate the tangential vector 
			//remember, the radial vector is (x, y) 
			//to get the tangential vector we flip those coordinates and negate one of them 
			float tx = -y; 
			float ty = x; 

			//add the tangential vector 

			x += tx * tangetial_factor; 
			y += ty * tangetial_factor; 

			//correct using the radial factor 

			x *= radial_factor; 
			y *= radial_factor; 
		} 
		glEnd(); 
		glPopMatrix();
	}

	
	public static void drawPolygon(Vec2 position, Vec2[] vertices, DrawingArgs userData){
		initialize();
		drawPolygon(position, 0, vertices, userData);
	}
	
	public static void drawPolygon(Vec2 position, float angle, Vec2[] vertices, DrawingArgs userData){
		initialize();
		drawPolygon(position, 1, 1, angle, vertices, userData);
	}
	
	public static void drawPolygon(Vec2 position, float scaleX, float scaleY, float angle, Vec2[] vertices, DrawingArgs userData){
		initialize();
		setFilled(userData.filled);
		setColor(userData.color);
		glPushMatrix();
		glTranslatef(position.x, position.y, 0);
		glRotated(angle, 0, 0, 1);
		glScalef(scaleX, scaleY, 1);
		glBegin(GL_POLYGON); 
		
		for (Vec2 vertex : vertices){
			if (vertex.x != 0 || vertex.y != 0){
				glVertex2f(vertex.x, vertex.y);
			}
		}
		
		glEnd();
		glPopMatrix();
	}
	
	public static void drawRectangle(Vec2 position, float lengthX, float lengthY, float angle, DrawingArgs drawArgs){
		initialize();
		drawPolygon(position, lengthX, lengthY, angle, squareVectors, drawArgs);
	}
	
	public static void setColor(ReadableColor color){
		initialize();
		float red = color.getRed() / 255f;
		float blue = color.getBlue() / 255f;
		float green = color.getGreen() / 255f;
		float alpha = color.getAlpha() / 255f;
		glColor4f(red, green, blue, alpha);
	}
	
	public static void translateCamera(Vec2 translation){
		Vec2 inverse = translation.negate();
		cameraPosition = cameraPosition.add(translation);
		glTranslatef(inverse.x, inverse.y, 0);
	}
	
	public static void setCameraPosition(Vec2 pos){
		setupProjection(DrawingTool.settings.getProjectionWidth(), DrawingTool.settings.getProjectionHeight());
		cameraPosition = new Vec2(0, 0);
		translateCamera(pos);
	}
	
	public static void setupProjection(float projectionWidth, float projectionHeight) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, projectionWidth, 0, projectionHeight, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		
		glLoadIdentity();
	}
	
	// Gets the extents of the current camera view
	public static float getProjectionExtent(boolean horizontal, boolean lower){
		float max, min, camerapos;
		if (horizontal){
			max = settings.getProjectionWidth();
			camerapos = cameraPosition.x;
		}
		else {
			max = settings.getProjectionHeight();
			camerapos = cameraPosition.y;
		}
		min = 0;
		
		if (lower){
			return min + camerapos;
		}
		else{
			return max + camerapos;
		}
	}
	
	public static Vec2 getCameraCenter(){
		return new Vec2(cameraPosition.x + (settings.getProjectionWidth() / 2), cameraPosition.y + (settings.getProjectionHeight() / 2));
	}
}
