package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.artemis.utils.Sort;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ychstudio.components.Renderer;
import com.ychstudio.components.Transform;
import java.util.Comparator;

public class RenderSystem extends EntitySystem {

    private final SpriteBatch batch;
    
    protected ComponentMapper<Transform> mTransform;
    protected ComponentMapper<Renderer> mRenderer;
    
    public RenderSystem(SpriteBatch batch) {
        super(Aspect.all(Transform.class, Renderer.class));
        
        this.batch = batch;
    }

    @Override
    protected void begin() {
        batch.begin();
    }
    
    @Override
    protected void end() {
        batch.end();
    }

    protected void process(Entity e) {
        Transform transform = mTransform.get(e);
        Renderer renderer = mRenderer.get(e);
        
        renderer.setPosition(transform.posX, transform.posY);
        renderer.setRotation(transform.rotation);
        renderer.setScale(transform.sclX, transform.sclY);
        
        renderer.draw(batch);
    }

    @Override
    protected void processSystem() {
        Bag<Entity> entities = getEntities();
        
        Sort sort = Sort.instance();
        sort.sort(entities, new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                Transform t1 = mTransform.get(o1);
                Transform t2 = mTransform.get(o2);
                return (int) -(t1.posY - t2.posY);
            }
        });
        
        for (Entity e : entities) {
            process(e);
        }
    }
    
}
