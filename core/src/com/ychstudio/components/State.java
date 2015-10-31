package com.ychstudio.components;

import com.artemis.Component;

public class State extends Component {
    private float stateTime;

    public State() {
        stateTime = 0;
    }
    
    public void resetStateTime() {
        stateTime = 0;
    }
    
    public float getStateTime() {
        return stateTime;
    }
    
    public void addStateTime(float delta) {
        stateTime += delta;
    }
    
}
