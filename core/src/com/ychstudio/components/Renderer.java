package com.ychstudio.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Renderer extends Component {

    private Sprite sprite;

    public Renderer(TextureRegion textureRegion) {
        sprite = new Sprite(textureRegion);
    }

    public Renderer(TextureRegion textureRegion, float width, float height) {
        this(textureRegion);
        sprite.setSize(width, height);
    }
    
    public void setCenter(float x, float y) {
        sprite.setCenter(x, y);
    }

    public void setSize(float width, float height) {
        sprite.setSize(width, height);
    }

    public void setPosition(float x, float y) {
        sprite.setPosition(x, y);
    }
    
    public void setRotation(float degrees) {
        sprite.setRotation(degrees);
    }
    
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

}
