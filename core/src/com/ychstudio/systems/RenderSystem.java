package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ychstudio.components.Renderer;

public class RenderSystem extends IteratingSystem {

    private SpriteBatch batch;
    
    protected ComponentMapper<Renderer> mRenderer;
    
    public RenderSystem(SpriteBatch batch) {
        super(Aspect.all(Renderer.class));
        
        this.batch = batch;
    }

    @Override
    protected void process(int i) {
        Renderer renderer = mRenderer.get(i);
        renderer.draw(batch);
    }
    
}
