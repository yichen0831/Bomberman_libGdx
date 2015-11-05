package com.ychstudio.components;

import com.artemis.Component;
import com.badlogic.gdx.math.MathUtils;
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

    public static short defaultMaskBits = GameManager.INDESTRUCTIIBLE_BIT | GameManager.BREAKABLE_BIT | GameManager.ENEMY_BIT | GameManager.EXPLOSION_BIT | GameManager.POWERUP_BIT | GameManager.PORTAL_BIT;
    public static short invincibleMaskBit = GameManager.INDESTRUCTIIBLE_BIT | GameManager.BREAKABLE_BIT | GameManager.POWERUP_BIT | GameManager.PORTAL_BIT;

    public float maxSpeed;
    public float acceleration;
    public int hp;
    public int bombPower;
    public int maxBomb;
    public int bombLeft;
    public boolean kickBomb;
    public boolean remoteBomb;

    public float bombRegeratingTime;
    public float bombRegeratingTimeLeft;

    public boolean invincible;
    public float invincibleCountDown;

    public Player(boolean restore) {
        state = State.IDLING_DOWN;

        if (!restore) {
            GameManager.playerMaxBomb = 3;
            GameManager.playerMaxSpeed = 0;
            GameManager.playerBombPower = 0;
            GameManager.playerBombRegeratingTime = 2.0f;
            GameManager.playerKickBomb = false;
            GameManager.playerRemoteBomb = false;
        }

        maxSpeed = 3.0f + GameManager.playerMaxSpeed * 1.2f;
        bombPower = 1 + GameManager.playerBombPower;
        maxBomb = GameManager.playerMaxBomb;
        bombRegeratingTime = GameManager.playerBombRegeratingTime;
        remoteBomb = GameManager.playerRemoteBomb;
        kickBomb = GameManager.playerKickBomb;

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

    public void powerUpAmmo() {
        if (maxBomb <= 9) {
            maxBomb++;
            GameManager.playerMaxBomb = maxBomb;
        } else {
            decreaseBombRegeneratingTime();
        }
    }

    public void powerUpPower() {
        if (bombPower < 6) {
            GameManager.playerBombPower++;
            bombPower = 1 + GameManager.playerBombPower;
        } else {
            decreaseBombRegeneratingTime();
        }
    }

    public void powerUpSpeed() {
        if (maxSpeed <= 8.0f) {
            GameManager.playerMaxSpeed++;
            maxSpeed = 3.0f + GameManager.playerMaxSpeed * 1.2f;
        } else {
            decreaseBombRegeneratingTime();
        }
    }

    public void powerUpKick() {
        if (!kickBomb) {
            kickBomb = true;
            GameManager.playerKickBomb = kickBomb;
        } else {
            decreaseBombRegeneratingTime();
        }
    }

    public void powerUpRemote() {
        if (!remoteBomb) {
            remoteBomb = true;
            GameManager.playerRemoteBomb = remoteBomb;
        } else {
            decreaseBombRegeneratingTime();
        }
    }

    public void decreaseBombRegeneratingTime() {
        if (bombRegeratingTime <= 0.2f) {
            return;
        }

        bombRegeratingTime -= 0.2f;
        GameManager.playerBombRegeratingTime = bombRegeratingTime;
        bombRegeratingTimeLeft = MathUtils.clamp(bombRegeratingTimeLeft, 0, bombRegeratingTime);
    }

}
