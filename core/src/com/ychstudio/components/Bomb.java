package com.ychstudio.components;

import com.artemis.Component;

public class Bomb extends Component {
    public enum State {
        NORMAL,
        MOVING_UP,
        MOVING_DOWN,
        MOVING_LEFT,
        MOVING_RIGHT,
        EXPLODING
    }
    
    public float countDown;
    public State state;
    public int power;
    public float speed;

    public Bomb() {
        this(1, 2.0f);
    }
    
    public Bomb(int power) {
        this(power, 2.0f);
    }

    public Bomb(int power, float countDown) {
        this.power = power;
        this.countDown = countDown;
        this.speed = 6.0f;
        state = State.NORMAL;
    }
    
    public void setMove(Bomb.State state) {
        this.state = state;
    }
}
