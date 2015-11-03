package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.ychstudio.components.Explosion;
import com.ychstudio.components.State;

public class ExplosionSystem extends IteratingSystem {

    protected ComponentMapper<Explosion> mExplosion;
    protected ComponentMapper<State> mState;
    
    public ExplosionSystem() {
        super(Aspect.all(Explosion.class, State.class));
    }

    @Override
    protected void process(int entityId) {
//        Explosion explosion = mExplosion.get(entityId);
        State state = mState.get(entityId);
        if (state.getStateTime() > 0.75f) {
            world.delete(entityId);
        }
    }
    
}
