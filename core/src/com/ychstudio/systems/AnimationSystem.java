package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.ychstudio.components.Anim;
import com.ychstudio.components.Renderer;
import com.ychstudio.components.State;

public class AnimationSystem extends IteratingSystem {

    ComponentMapper<Renderer> mRenderer;
    ComponentMapper<Anim> mAnim;
    ComponentMapper<State> mState;
    
    public AnimationSystem() {
        super(Aspect.all(Renderer.class, Anim.class, State.class));
    }

    @Override
    protected void process(int i) {
        Renderer renderer = mRenderer.get(i);
        Anim anim = mAnim.get(i);
        State state = mState.get(i);
        
        renderer.setRegion(anim.getTextureRegion(state.getCurrentState(), state.getStateTime(), true));
    }
    
}
