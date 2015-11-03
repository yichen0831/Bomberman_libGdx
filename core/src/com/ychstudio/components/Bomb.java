package com.ychstudio.components;

import com.artemis.Component;

public class Bomb extends Component {
    public enum State {
        NORMAL,
        EXPLODING
    }
    
    public float countDown;
    public State currentState;

    public Bomb() {
        this(2.0f);
    }

    public Bomb(float countDown) {
        this.countDown = countDown;
        currentState = State.NORMAL;
    }

}
