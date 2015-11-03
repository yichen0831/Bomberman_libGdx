package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.components.Bomb;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;
import com.ychstudio.components.Transform;

public class BombSystem extends IteratingSystem {

    protected ComponentMapper<Bomb> mBomb;
    protected ComponentMapper<Transform> mTransform;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<State> mState;

    public BombSystem() {
        super(Aspect.all(Bomb.class, RigidBody.class, Transform.class, State.class));
    }

    @Override
    protected void process(int entityID) {
        Bomb bomb = mBomb.get(entityID);
        State state = mState.get(entityID);
        RigidBody rigidBody = mRigidBody.get(entityID);
        
        Body body = rigidBody.body;

        bomb.countDown -= world.getDelta();

        if (bomb.countDown <= 0) {
            // explode
            bomb.currentState = Bomb.State.EXPLODING;
        }

        switch (bomb.currentState) {
            case EXPLODING:
                state.setCurrentState("exploding");
                if (state.getStateTime() > 0.75f) {
                    World b2dWorld = body.getWorld();
                    b2dWorld.destroyBody(body);
                    world.delete(entityID);
                }
                break;
            case NORMAL:
            default:
                state.setCurrentState("normal");
                break;
        }

    }

}
