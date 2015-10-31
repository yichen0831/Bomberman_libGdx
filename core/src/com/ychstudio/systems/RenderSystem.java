package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ychstudio.components.Renderer;
import com.ychstudio.components.Transform;

public class RenderSystem extends IteratingSystem {

    private SpriteBatch batch;
    
    protected ComponentMapper<Transform> mTransform;
    protected ComponentMapper<Renderer> mRenderer;
    
    public RenderSystem(SpriteBatch batch) {
        super(Aspect.all(Transform.class, Renderer.class));
        
        this.batch = batch;
    }

    @Override
    protected void process(int i) {
        Transform transform = mTransform.get(i);
        Renderer renderer = mRenderer.get(i);
        
        renderer.setPosition(transform.posX, transform.posY);
        renderer.setRotation(transform.rotation);
        renderer.setScale(transform.sclX, transform.sclY);
        
        renderer.draw(batch);
    }
    
}
