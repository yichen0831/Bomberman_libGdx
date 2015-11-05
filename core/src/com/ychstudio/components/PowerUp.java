package com.ychstudio.components;

import com.artemis.Component;

public class PowerUp extends Component {
    public enum Type {
        AMMO,
        POWER,
        SPEED,
        KICK,
        REMOTE;
        
        public static Type getRandomType() {
            int index;
            int random = (int) (Math.random() * 10);
            if (random < 3) {
                index = 0;  // AMMO
            }
            else if (random < 6) {
                index = 1;  // POWER
            }
            else if (random < 8) {
                index = 2;  // SPEED
            }
            else if (random < 9) {
                index = 3;  // KICK
            }
            else {
                index = 4;  // REMOTE
            }
            return values()[index];
        }
    }
    
    public Type type;
    
    public float life;
    
    public PowerUp() {
        this(6.0f);
    }

    public PowerUp(float life) {
        type = Type.getRandomType();
        this.life = life;
    }
}
