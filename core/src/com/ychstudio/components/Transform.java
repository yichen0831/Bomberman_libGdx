package com.ychstudio.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class Transform extends Component {
    
    public float posX;
    public float posY;
    
    public float z; // for checking drawing sequence
    
    public float sclX;
    public float sclY;
    
    public float rotation;

    public Transform(float posX, float posY, float sclX, float sclY, float rotation) {
        this.posX = posX;
        this.posY = posY;
        z = posY;
        this.sclX = sclX;
        this.sclY = sclY;
        this.rotation = rotation;
    }
    
    public Transform() {
        this(0, 0, 1, 1, 0);
    }
    
    public void setPosition(float x, float y) {
        posX = x;
        posY = y;
        z = posY;
    }
    
    public void setPosition(Vector2 position) {
        posX = position.x;
        posY = position.y;
        z = posY;
    }
}
