package com.ychstudio.components;

import com.artemis.Component;

public class State extends Component {

    private float stateTime;
    private String currentState;
    private String previousState;
    private boolean looping; // if the animation should loop

    public State(String state) {
        previousState = currentState = state;
        stateTime = 0;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public void resetStateTime() {
        stateTime = 0;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        setCurrentState(currentState, looping);
    }
    
    public void setCurrentState(String currentState, boolean looping) {
        this.currentState = currentState;
        this.looping = looping;
        
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
