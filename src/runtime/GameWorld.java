package runtime;

import java.awt.Shape;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.JointType;
import org.lwjgl.util.Color;

public class GameWorld {

	private World world;
	
	public World getWorld() {
		return world;
	}

	private void setWorld(World world) {
		this.world = world;
	}

	public GameWorld(){
	}

	public void initialize(Vec2 gravity, boolean sleep /*, LevelDef level*/){
		setWorld(new World(gravity, sleep));
		
		Vec2 pos = new Vec2((float)Game.projectionWidth / 2,(float) Game.projectionHeight / 2);
		
		// Make ball
		
		PolygonShape pShape = new PolygonShape();
		pShape.setAsBox(5, 15);
		
		FixtureDef fd = new FixtureDef();
		fd.shape = pShape;	
		fd.userData = new FixtureUserData(Color.BLUE, true);
		fd.restitution = 0.5f;
		fd.density = 1f;
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position = pos;
		bd.fixedRotation = false;
		
		Body body = world.createBody(bd);
		body.setTransform(pos, 119);
		body.createFixture(fd);
		
		// Make ground
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(100, 100);
		
		fd = new FixtureDef();
		fd.shape = shape;
		fd.userData = new FixtureUserData(Color.RED, false);
		
		bd.type = BodyType.STATIC;
		bd.position = new Vec2(0, -90f);
		bd.angle = 0;
		
		body = world.createBody(bd);
		body.createFixture(fd);
		
	}
	
	// Simulate the world one step
	public void step(){
		world.step(Game.timeStep, Game.velocityIterations, Game.positionIterations);
	}
	
	// Draw the world
	public void draw(){
		// Iterate over all the bodies in the world and draw each of their fixtures
		for (Body body = world.getBodyList(); body != null; body = body.getNext()){
			DrawingTool.drawBody(body);
		}
	}
}
