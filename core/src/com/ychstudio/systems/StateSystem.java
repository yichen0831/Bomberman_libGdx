package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.ychstudio.components.State;

public class StateSystem extends IteratingSystem {

    ComponentMapper<State> mState;
    
    public StateSystem() {
        super(Aspect.all(State.class));
    }

    @Override
    protected void process(int entityId) {
        State state = mState.get(entityId);
        state.addStateTime(world.getDelta());
    }
    
}
