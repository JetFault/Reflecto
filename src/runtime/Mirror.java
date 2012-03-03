package runtime;

import jlwglbox2dframework.DrawingArgs;
import jlwglbox2dframework.DrawingTool;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class Mirror {
	Vec2 startPos = null;
	Vec2 temporaryEnd = null;
	Vec2 endPos = null;
	
	World physWorld;
	
	PolygonShape rect;
	Fixture fixture;
	Body body;
	
	float width = 0.5f;
	
	long timeCreated;
	
	public Mirror(Vec2 start, long time, World physW) {
		this.startPos = start;
		this.temporaryEnd = start;
		this.timeCreated = time;
		
		this.physWorld = physW;
	}
	
	public void endMirror(Vec2 end) {
		this.endPos = end;
		addToPhysics();
	}
	
	public void draw() {
		DrawingTool.setColor(Color.CYAN);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		
		GL11.glVertex2f(startPos.x, startPos.y);
		GL11.glVertex2f(temporaryEnd.x, temporaryEnd.y);
		GL11.glEnd();		
	}
	
	public void addToPhysics() {
		float hx = MathUtils.distance(endPos, startPos) / 2;
		float hy = width;
		
		Vec2 center = startPos.add(endPos);
		center.mulLocal(0.5f);
		
		float y = endPos.y - startPos.y;
		float x = endPos.x - startPos.x;
		float angle = MathUtils.atan2(y, x);
		
		rect = new PolygonShape();
		rect.setAsBox(hx, hy, new Vec2(0,0), angle);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = rect;
		fixtureDef.userData = new DrawingArgs(Color.WHITE, true);
		
		BodyDef bDef = new BodyDef();
		bDef.position = center;
		bDef.type = BodyType.STATIC;
		bDef.userData = "mirror";
		
		this.body = physWorld.createBody(bDef);
		fixture = body.createFixture(fixtureDef);	
		
	}

}
