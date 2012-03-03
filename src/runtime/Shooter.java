package runtime;

import java.util.ArrayList;
import java.util.LinkedList;

import jlwglbox2dframework.DrawingArgs;
import jlwglbox2dframework.DrawingTool;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

public class Shooter {
	
	World physWorld;
	
	PolygonShape triangle;
	Fixture fixture;
	Body body;
	
	public float desiredAngle; //In Radians
	
	public boolean readyToFire;
	
	public Vec2 position;
	
	ArrayList<Bullet> bullets;
	
	public float bulletSpeed = 28;
	
	public int numPreviewBalls = 0;
	
	LinkedList<Float> savedAngles = new LinkedList<>();
	
	
	public Shooter(Vec2 pos, World physicsWorld) {
		Vec2[] vecs = new Vec2[5];
		vecs[0] = new Vec2(-2,5);
		vecs[1] = new Vec2(0,5);
		vecs[2] = new Vec2(5,0);		
		vecs[3] = new Vec2(0,-5);
		vecs[4] = new Vec2(-2,-5);
		
		this.bullets = new ArrayList<>();
		
		this.physWorld = physicsWorld;
		
		this.position = pos;
		
		triangle = new PolygonShape();
		triangle.set(vecs, vecs.length);
		
		//triangle.setAsBox(5, 5);		
		
		FixtureDef fDef = new FixtureDef();
		fDef.shape = triangle;
		fDef.filter = new Filter();
		fDef.filter.groupIndex = 0;
		fDef.userData = new DrawingArgs(ReadableColor.WHITE, true);
		
		BodyDef bDef = new BodyDef();
		bDef.position = pos;
		bDef.type = BodyType.KINEMATIC;
		
		body = physicsWorld.createBody(bDef);
		fixture = body.createFixture(fDef);
		
	//	desiredAngle = body.getAngle();
		readyToFire = true;
		
	}
	
	public void setColor(ReadableColor colour) {
		this.fixture.setUserData(new DrawingArgs(colour, true));
	}
	
	public void rotate(float degrees) {
		this.desiredAngle = degrees*MathUtils.DEG2RAD;
		float vel = 4.0f/3.5f;
		if(desiredAngle < body.getAngle()) vel *= -1;
		this.body.setAngularVelocity(vel);
		this.body.setAngularDamping(5);
	}
	
	public void stopRotate() {
		this.body.setAngularVelocity(0);		
	}
	
	public float getAngle() { // Degrees
		return MathUtils.RAD2DEG * body.getAngle();
	}
	
	public void setDesiredAngle(float degrees) {
		this.desiredAngle = degrees*MathUtils.DEG2RAD;
	}
	
	public float getDesiredAngle() {
		return this.desiredAngle * MathUtils.RAD2DEG;
	}
	
	public void drawDirection() {
		DrawingTool.setColor(Color.WHITE);
		GL11.glPushMatrix();		
		
		float x = MathUtils.cos(desiredAngle);
		float y = MathUtils.sin(desiredAngle);
		Vec2 end = new Vec2(x,y);
		end.mulLocal(3);	
		end.addLocal(position);
		
		GL11.glTranslatef(position.x, position.y, 0);
		GL11.glRotatef(desiredAngle*MathUtils.RAD2DEG-90, 0, 0, 1);
		GL11.glScalef(4, 4, 4);
		
		GL11.glBegin(GL11.GL_LINES);
		
		GL11.glVertex2f(0,2);
		GL11.glVertex2f(0, 0);
		
		GL11.glVertex2f(0,2);
		GL11.glVertex2f(0.5f, 1.5f);
		
		GL11.glVertex2f(0,2);
		GL11.glVertex2f(-0.5f, 1.5f);
		
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public void fire() {
		Bullet bullet = new Bullet(position, physWorld, desiredAngle, bulletSpeed);
		bullets.add(bullet);
	}
	
	public void multiBallPreviewFire(int balls) {
		int segs = 360/balls;
		for (int i = 0; i < balls; i++) {
			float angle = segs*i + MathUtils.randomFloat(-(segs/3), segs/3);
			savedAngles.add(angle);
			Bullet bullet = new Bullet(position, physWorld, angle, bulletSpeed,Color.CYAN);
			bullets.add(bullet);	
			numPreviewBalls++;
		}
		System.out.println(savedAngles);
	}
	
	public void multiBallFire() {
		System.out.println(savedAngles);
		while (savedAngles.size() > 0) {
			float angle = savedAngles.removeFirst();
			System.out.println(angle);
			Bullet bullet = new Bullet(position, physWorld, angle, bulletSpeed-5.0f);
			bullets.add(bullet);			
		}
	}
	
	public void killBullet(Body body) {
		ArrayList<Bullet> enemiesToKill = new ArrayList<>();
		for (Bullet bullet : bullets) {
			if(bullet.body == body) {
				if(bullet.multiBall) {
					numPreviewBalls--;
				}
				physWorld.destroyBody(body);
				enemiesToKill.add(bullet);
			}
		}
		
		for(Bullet enemy : enemiesToKill) {
			bullets.remove(enemy);
		}
		enemiesToKill.clear();
	}
}
