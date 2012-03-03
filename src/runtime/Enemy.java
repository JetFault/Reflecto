package runtime;

import jlwglbox2dframework.DrawingArgs;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.Color;

public class Enemy {
	
	World physWorld;
	
	CircleShape shape;
	Fixture fixture;
	Body body;
	
	int hitPts = 1;
	
	public Enemy(Vec2 pos, World physicsWorld) {
		this.physWorld = physicsWorld;
		
		CircleShape ball = new CircleShape();
		ball.m_radius = 3;

		FixtureDef fDef = new FixtureDef();
		fDef.shape = ball;
		fDef.restitution = 1;
		fDef.friction = 0;
		fDef.userData = new DrawingArgs(Color.RED, true);

		BodyDef bodyDef = new BodyDef();
		bodyDef.position = pos;
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.userData = "enemy";
		
		body = physWorld.createBody(bodyDef);
		
		fixture = body.createFixture(fDef);
	}
	
	public Enemy(Vec2 pos, World physicsWorld, int health) {
		this(pos, physicsWorld);
		this.hitPts = health;
	}
}
