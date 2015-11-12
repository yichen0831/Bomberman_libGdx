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
        TELEPORTING,
        DYING
    }

    public State state;

    public static short defaultMaskBits = GameManager.INDESTRUCTIIBLE_BIT | GameManager.BREAKABLE_BIT | GameManager.ENEMY_BIT | GameManager.BOMB_BIT | GameManager.EXPLOSION_BIT | GameManager.POWERUP_BIT | GameManager.PORTAL_BIT;
    public static short invincibleMaskBit = GameManager.INDESTRUCTIIBLE_BIT | GameManager.BREAKABLE_BIT | GameManager.POWERUP_BIT | GameManager.PORTAL_BIT;
    
    public static final int MAX_BOMB_CAPACITY = 10;
    public static final int MAX_BOMB_POWER = 6;

    public float maxSpeed;
    public float acceleration;
    public int hp;
    public int bombPower;
    public int bombCapacity;
    public int bombLeft;
    public boolean kickBomb;
    public boolean remoteBomb;
    
    public float bombRegeratingTime;
    public float bombRegeratingTimeLeft;

    public boolean invincible;
    public float invincibleCountDown;
    
    public int receivedDamage;

    public Player(boolean resetPlayerAbilities) {
        state = State.IDLING_DOWN;

        if (resetPlayerAbilities) {
            GameManager.resetPlayerAbilities();
        }

        maxSpeed = 3.0f + GameManager.playerMaxSpeed * 1.2f;
        bombPower = 1 + GameManager.playerBombPower;
        bombCapacity = GameManager.playerBombCapacity;
        bombRegeratingTime = GameManager.playerBombRegeratingTime;
        remoteBomb = GameManager.playerRemoteBomb;
        kickBomb = GameManager.playerKickBomb;

        hp = 1;
        acceleration = 1.0f;
        bombLeft = 0;
        bombRegeratingTimeLeft = 0f;

        invincible = true;
        invincibleCountDown = 3.0f;
        
        receivedDamage = 0;
    }

    public void damage(int damage) {
        if (!invincible) {
            hp -= damage;
        }
    }

    public void powerUpAmmo() {
        if (bombCapacity < MAX_BOMB_CAPACITY) {
            bombCapacity++;
            GameManager.playerBombCapacity = bombCapacity;
        } else {
            decreaseBombRegeneratingTime();
        }

        GameManager.getInstance().playSound("Powerup.ogg");
    }

    public void powerUpPower() {
        if (bombPower < MAX_BOMB_POWER) {
            GameManager.playerBombPower++;
            bombPower = 1 + GameManager.playerBombPower;
        } else {
            decreaseBombRegeneratingTime();
        }

        GameManager.getInstance().playSound("Powerup.ogg");
    }

    public void powerUpSpeed() {
        if (maxSpeed <= 8.0f) {
            GameManager.playerMaxSpeed++;
            maxSpeed = 3.0f + GameManager.playerMaxSpeed * 1.2f;
        } else {
            decreaseBombRegeneratingTime();
        }

        GameManager.getInstance().playSound("Powerup.ogg");
    }

    public void powerUpKick() {
        if (!kickBomb) {
            kickBomb = true;
            GameManager.playerKickBomb = kickBomb;
        } else {
            decreaseBombRegeneratingTime();
        }

        GameManager.getInstance().playSound("Powerup.ogg");
    }

    public void powerUpRemote() {
        if (!remoteBomb) {
            remoteBomb = true;
            GameManager.playerRemoteBomb = remoteBomb;
        } else {
            decreaseBombRegeneratingTime();
        }

        GameManager.getInstance().playSound("Powerup.ogg");
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
