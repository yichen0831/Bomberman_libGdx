package com.ychstudio.components;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;

public class RigidBody extends Component {
    public Body body;

    public RigidBody(Body body) {
        this.body = body;
    }
    
}
