package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.components.Breakable;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;

public class BreakableSystem extends IteratingSystem {

    protected ComponentMapper<Breakable> mBreakable;
    protected ComponentMapper<State> mState;
    protected ComponentMapper<RigidBody> mRigidBody;

    public BreakableSystem() {
        super(Aspect.all(Breakable.class, State.class));
    }

    @Override
    protected void process(int entityId) {
        Breakable breakable = mBreakable.get(entityId);
        State state = mState.get(entityId);
        RigidBody rigidBody = mRigidBody.get(entityId);
        Body body = rigidBody.body;

        switch (breakable.state) {
            case EXPLODING:
                state.setCurrentState("exploding");
                if (state.getStateTime() > 0.6f) {
                    body.getWorld().destroyBody(body);
                    world.delete(entityId);

                    // chance to create PowerUp item
                    if (Math.random() < 0.2) {
                        ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                        actorBuilder.createPowerUp(body.getPosition().x, body.getPosition().y);
                    }
                }
                break;
            case NORMAL:
            default:
                state.setCurrentState("normal");
                break;
        }
    }

}
