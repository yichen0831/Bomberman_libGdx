package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.ychstudio.components.PowerUp;
import com.ychstudio.components.Renderer;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;

public class PowerUpSystem extends IteratingSystem {

    protected ComponentMapper<PowerUp> mPowerUp;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<Renderer> mRenderer;
    protected ComponentMapper<State> mState;
    
    public PowerUpSystem() {
        super(Aspect.all(PowerUp.class, RigidBody.class, Renderer.class, State.class));
    }

    @Override
    protected void process(int entityId) {
        PowerUp powerUp = mPowerUp.get(entityId);
        RigidBody rigidBody = mRigidBody.get(entityId);
        Renderer renderer = mRenderer.get(entityId);
        State state = mState.get(entityId);
        
        if (state.getStateTime() > powerUp.life) {
            // destroy
        }
    }
    
}
