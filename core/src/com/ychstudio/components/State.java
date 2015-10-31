package com.ychstudio.components;

import com.artemis.Component;

public class State extends Component {
    private float stateTime;
    private String currentState;

    public State() {
        stateTime = 0;
    }
    
    public void resetStateTime() {
        stateTime = 0;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }
    
    public float getStateTime() {
        return stateTime;
    }
    
    public void addStateTime(float delta) {
        stateTime += delta;
    }
    
}
