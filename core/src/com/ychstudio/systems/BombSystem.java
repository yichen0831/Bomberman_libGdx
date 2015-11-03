package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.components.Bomb;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;
import com.ychstudio.components.Transform;
import com.ychstudio.gamesys.GameManager;

public class BombSystem extends IteratingSystem {

    protected ComponentMapper<Bomb> mBomb;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<State> mState;

    private final AssetManager assetManager;

    public BombSystem() {
        super(Aspect.all(Bomb.class, RigidBody.class, Transform.class, State.class));
        assetManager = GameManager.getInstance().getAssetManager();
    }

    @Override
    protected void process(int entityId) {
        Bomb bomb = mBomb.get(entityId);
        State state = mState.get(entityId);
        RigidBody rigidBody = mRigidBody.get(entityId);

        Body body = rigidBody.body;

        bomb.countDown -= world.getDelta();

        if (bomb.countDown <= 0) {
            // explode
            bomb.currentState = Bomb.State.EXPLODING;
        }

        switch (bomb.currentState) {
            case EXPLODING:
                state.setCurrentState("exploding");
                // create explosion
                ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                actorBuilder.createExplosion(bomb, body.getPosition().x, body.getPosition().y);

                // destroy itself
                World b2dWorld = body.getWorld();
                b2dWorld.destroyBody(body);
                world.delete(entityId);
                break;
            case NORMAL:
            default:
                state.setCurrentState("normal");
                break;
        }

    }
}
