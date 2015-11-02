package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
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
    protected void process(int i) {
        Breakable breakable = mBreakable.get(i);
        State state = mState.get(i);
        RigidBody rigidBody = mRigidBody.get(i);

        switch (breakable.getCurrentState()) {
            case EXPLODING:
                state.setCurrentState("exploding");
                if (state.getStateTime() > 0.6f) {
                    rigidBody.body.getWorld().destroyBody(rigidBody.body);
                    world.delete(i);
                }
                break;
            case NORMAL:
            default:
                state.setCurrentState("normal");
                break;
        }
    }

}
