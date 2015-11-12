package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ychstudio.components.Particle;

public class ParticleSystem extends IteratingSystem {

    protected ComponentMapper<Particle> mParticle;

    private SpriteBatch batch;

    public ParticleSystem(SpriteBatch batch) {
        super(Aspect.all(Particle.class));
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

    @Override
    protected void process(int entityId) {
        Particle particle = mParticle.get(entityId);

        if (!particle.particleEffect.isComplete()) {
            particle.particleEffect.draw(batch, world.getDelta());
        } else {
            world.delete(entityId);
        }
    }

}
