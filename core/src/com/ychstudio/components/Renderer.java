package com.ychstudio.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
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

    public void setRegion(TextureRegion textureRegion) {
        sprite.setRegion(textureRegion);
    }

    public void setOrigin(float x, float y) {
        sprite.setOrigin(x, y);
    }

    public void setSize(float width, float height) {
        sprite.setSize(width, height);
    }

    public void setPosition(float x, float y) {
        sprite.setPosition(x - sprite.getOriginX(), y - sprite.getOriginY());
    }

    public void setRotation(float degrees) {
        sprite.setRotation(degrees);
    }

    public void setScale(float x, float y) {
        sprite.setScale(x, y);
    }

    public void setFlip(boolean x, boolean y) {
        sprite.setFlip(x, y);
    }

    public boolean isFlipX() {
        return sprite.isFlipX();
    }

    public boolean isFlipY() {
        return sprite.isFlipY();
    }
    
    public void setColor(Color tint) {
        sprite.setColor(tint);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
