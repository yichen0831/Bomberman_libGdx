package com.ychstudio.components;

import com.artemis.Component;

public class Breakable extends Component{
    public enum State {
        NORMAL,
        EXPLODING
    }
    
    private State currentState;
    
    public Breakable() {
        currentState = State.NORMAL;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
    
}
