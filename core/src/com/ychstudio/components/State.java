package com.ychstudio.components;

import com.artemis.Component;

public class State extends Component {

    private float stateTime;
    private String currentState;
    private String previousState;

    public State(String state) {
        previousState = currentState = state;
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
        
        if (!previousState.equals(currentState)) {
            previousState = currentState;
            resetStateTime();
        }
    }
    
    public float getStateTime() {
        return stateTime;
    }

    public void addStateTime(float delta) {
        stateTime += delta;
    }

}
