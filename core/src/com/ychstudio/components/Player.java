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
    public int hp;
    public int bombPower;
    public int maxBomb;
    public int bombLeft;

    public float bombRegeratingTime;
    public float bombRegeratingTimeLeft;

    public boolean invincible;
    public float invincibleCountDown;

    public Player() {
        state = State.IDLING_DOWN;
        maxSpeed = 3.0f;
        acceleration = 1.0f;
        hp = 1;
        bombPower = 1;
        maxBomb = 3;
        bombLeft = 1;
        bombRegeratingTime = 2.0f;
        bombRegeratingTimeLeft = 2.0f;

        invincible = true;
        invincibleCountDown = 3.0f;
    }

    public void damage(int damage) {
        if (!invincible) {
            hp -= damage;
        }
    }

}
