package runtime;

import java.util.ArrayList;

import jlwglbox2dframework.DrawingArgs;
import jlwglbox2dframework.GameSettings;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

public class Bullet {


	World physWorld;

	CircleShape ball;
	Fixture fixture;
	Body body;

	public float desiredAngle;

	public Vec2 position;
	
	float speed = 25.0f;

	ArrayList<Bullet> bullets;
	
	int mirrorsHit = 0;
	
	public boolean multiBall = false;
	
	ReadableColor colour = ReadableColor.YELLOW;


	public Bullet(Vec2 pos, World physicsWorld, float angle, float speed) {

		this.physWorld = physicsWorld;

		this.position = pos;
		this.desiredAngle = angle;
		this.speed = speed;
		
		fire();
	}
	
	public Bullet(Vec2 pos, World physicsWorld, float angle, float speed, ReadableColor Colour) {

		this.physWorld = physicsWorld;

		this.position = pos;
		this.desiredAngle = angle;
		this.speed = speed;
		this.colour = Colour;
		
		multiBall=true;
		
		fire();

	}

	public float getAngle() {
		return MathUtils.RAD2DEG * body.getAngle();
	}

	public void setDesiredAngle(float degrees) {
		this.desiredAngle = degrees*MathUtils.DEG2RAD;
	}
	
	public void setMultiBall() {
		multiBall = true;
	}
	
	public boolean isMultiBall() {
		return multiBall;
	}

	public void fire() {
		CircleShape ball = new CircleShape();
		ball.m_radius = 1;

		FixtureDef fDef = new FixtureDef();
		fDef.shape = ball;
		fDef.restitution = 1;
		fDef.friction = 0;
		fDef.userData = new DrawingArgs(colour, true);
		fDef.filter.categoryBits = 0x0002;
		fDef.filter.maskBits = 0xFFFF & ~0x0002;
		if(multiBall) {
			fDef.filter = new Filter();
			fDef.filter.groupIndex = 0;
		}
		BodyDef bodyDef = new BodyDef();
		Vec2 loc = new Vec2(position);
		bodyDef.position = loc;
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.userData = "bullet";

		//	float velX = MathUtils.cos(body.getAngle());
		//	float velY = MathUtils.sin(body.getAngle());

		body = physWorld.createBody(bodyDef);
		
		float velX = MathUtils.cos(desiredAngle);
		float velY = MathUtils.sin(desiredAngle);
		Vec2 vel = new Vec2(velX, velY);
		vel.mulLocal(speed);
		body.setLinearVelocity(vel);
		
		fixture = body.createFixture(fDef);

	}
	
	public boolean isOffScreen(GameSettings settings) {
		Vec2 ballPos = body.getPosition();
		if( ballPos.x < 0
			|| ballPos.x > settings.getProjectionWidth()
			|| ballPos.y < 0
			|| ballPos.y > settings.getProjectionHeight()) {
				return true;
		}
		return false;
	}
	
}
