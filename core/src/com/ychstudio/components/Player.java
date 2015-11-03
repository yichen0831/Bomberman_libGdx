package com.ychstudio.components;

import com.artemis.Component;

public class Player extends Component {
    public enum State {
        IDLING_UP,
        IDLING_LEFT,
        IDLING_DOWN,
        IDLING_RIGHT,
        WALKING_UP,
        WALKING_LEFT,
        WALKING_DOWN,
        WALKING_RIGHT,
        DYING
    }
    
    public State state;
    
    public float maxSpeed;
    public float acceleration;
    public int bombPower;

    public Player() {
        state = State.IDLING_DOWN;
        maxSpeed = 3.0f;
        acceleration = 1.0f;
        bombPower = 1;
    }
    
}
