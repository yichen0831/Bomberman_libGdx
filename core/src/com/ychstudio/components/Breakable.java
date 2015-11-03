package com.ychstudio.components;

import com.artemis.Component;

public class Breakable extends Component{
    public enum State {
        NORMAL,
        EXPLODING
    }
    
    public State state;
    
    public Breakable() {
        state = State.NORMAL;
    }
}
