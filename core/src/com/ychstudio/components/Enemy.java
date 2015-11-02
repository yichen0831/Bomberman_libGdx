package com.ychstudio.components;

import com.artemis.Component;

public class Enemy extends Component {

    public enum State {
        WALKING_UP,
        WALKING_DOWN,
        WALKING_LEFT,
        WALKING_RIGHT,
        ATTACKING_UP,
        ATTACKING_DOWN,
        ATTACKING_LEFT,
        ATTACKING_RIGHT,
        DYING;
        
        public static State getRandomWalkingState() {
            return values()[(int)(Math.random() * 4)];
        }
    }

    protected State currentState;
    protected int hp;
    protected float speed;
    
    public Enemy(int hp) {
        this(hp, 2);
    }

    public Enemy(int hp, float speed) {
        currentState = State.getRandomWalkingState();
        this.hp = hp;
        this.speed = speed;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
