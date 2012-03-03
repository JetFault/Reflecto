package runtime;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class HardEnemy extends Enemy {
	int hitPts = 1;
	public HardEnemy(Vec2 pos, World physicsWorld, int life) {
		super(pos, physicsWorld);
	
		this.hitPts = life;
	}

}
