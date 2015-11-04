package com.ychstudio.components;

import com.artemis.Component;
import com.ychstudio.gamesys.GameManager;

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

    public Player(boolean restore) {
        state = State.IDLING_DOWN;

        if (restore) {
            maxSpeed = 3.0f + GameManager.playerMaxSpeed;
            bombPower = 1 + GameManager.playerBombPower;
            maxBomb = GameManager.playerMaxBomb;
            bombRegeratingTime = GameManager.playerBombRegeratingTime;
        } else {
            maxSpeed = 3.0f;
            bombPower = 1;
            maxBomb = 3;
            bombRegeratingTime = 2.0f;
        }

        hp = 1;
        acceleration = 1.0f;
        bombLeft = 0;
        bombRegeratingTimeLeft = 0f;

        invincible = true;
        invincibleCountDown = 3.0f;
    }

    public void damage(int damage) {
        if (!invincible) {
            hp -= damage;
        }
    }

}
