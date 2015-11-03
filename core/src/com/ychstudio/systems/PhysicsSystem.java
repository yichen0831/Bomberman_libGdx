package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.Transform;

public class PhysicsSystem extends IteratingSystem {

    protected ComponentMapper<Transform> mTransform;
    protected ComponentMapper<RigidBody> mRigidBody;
    
    public PhysicsSystem() {
        super(Aspect.all(Transform.class, RigidBody.class));
    }

    @Override
    protected void process(int entityId) {
        Transform transform = mTransform.get(entityId);
        RigidBody rigidBody = mRigidBody.get(entityId);
        
        transform.setPosition(rigidBody.body.getPosition());
    }
    
}
