package com.ychstudio.components;

import com.artemis.Component;
import com.ychstudio.gamesys.GameManager;

public class Enemy extends Component {

    public static short defaultMaskBits = GameManager.INDESTRUCTIIBLE_BIT | GameManager.BREAKABLE_BIT | GameManager.PLAYER_BIT | GameManager.EXPLOSION_BIT;

    public enum State {
        WALKING_UP,
        WALKING_DOWN,
        WALKING_LEFT,
        WALKING_RIGHT,
        ATTACKING_UP,
        ATTACKING_DOWN,
        ATTACKING_LEFT,
        ATTACKING_RIGHT,
        DAMAGED,
        DYING;

        public static State getRandomWalkingState() {
            return values()[(int) (Math.random() * 4)];
        }
    }

    public State state;
    public int hp;
    protected float speed;

    public float lifetime; // total time when alive
    
    private String dieSound;
    public String type;
    
    public int receivedDamage;

    public Enemy(int hp) {
        this(hp, 2);
    }

    public Enemy(int hp, float speed) {
        this(hp, speed, "EnemyDie.ogg");
    }

    public Enemy(int hp, float speed, String dieSound) {
        this(hp, speed, dieSound, "basic");
    }
    
    public Enemy(int hp, float speed, String dieSound, String type) {
        state = State.getRandomWalkingState();
        this.hp = hp;
        this.speed = speed;
        this.dieSound = dieSound;
        this.type = type;
        
        lifetime = 0;
        receivedDamage = 0;

        // increase enemy count
        GameManager.enemiesLeft++;
    }

    public void damage(int damage) {
        hp -= damage;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getDieSound() {
        return dieSound;
    }

    public void setDieSound(String dieSound) {
        this.dieSound = dieSound;
    }

}
