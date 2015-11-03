package com.ychstudio.components;

import com.artemis.Component;

public class Bomb extends Component {
    public enum State {
        NORMAL,
        EXPLODING
    }
    
    public float countDown;
    public State currentState;
    public int power;

    public Bomb() {
        this(1, 2.0f);
    }
    
    public Bomb(int power) {
        this(power, 2.0f);
    }

    public Bomb(int power, float countDown) {
        this.power = power;
        this.countDown = countDown;
        currentState = State.NORMAL;
    }

}
