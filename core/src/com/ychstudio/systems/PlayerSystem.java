package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.ychstudio.components.Player;
import com.ychstudio.components.Renderer;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;
import com.ychstudio.components.Transform;

public class PlayerSystem extends IteratingSystem {

    ComponentMapper<Player> mPlayer;
    ComponentMapper<Transform> mTransform;
    ComponentMapper<RigidBody> mRigidBody;
    ComponentMapper<State> mState;

    public PlayerSystem() {
        super(Aspect.all(Player.class, Transform.class, RigidBody.class, State.class));
    }

    @Override
    protected void process(int i) {
        Player player = mPlayer.get(i);
        Transform transform = mTransform.get(i);
        RigidBody rigidBody = mRigidBody.get(i);
        State state = mState.get(i);

        float maxSpeedSqr = player.maxSpeed * player.maxSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (rigidBody.body.getLinearVelocity().len2() < maxSpeedSqr) {
                rigidBody.body.applyLinearImpulse(new Vector2(0, player.acceleration * rigidBody.body.getMass()), rigidBody.body.getWorldCenter(), true);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (rigidBody.body.getLinearVelocity().len2() < maxSpeedSqr) {
                rigidBody.body.applyLinearImpulse(new Vector2(0, -player.acceleration * rigidBody.body.getMass()), rigidBody.body.getWorldCenter(), true);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (rigidBody.body.getLinearVelocity().len2() < maxSpeedSqr) {
                rigidBody.body.applyLinearImpulse(new Vector2(-player.acceleration * rigidBody.body.getMass(), 0), rigidBody.body.getWorldCenter(), true);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (rigidBody.body.getLinearVelocity().len2() < maxSpeedSqr) {
                rigidBody.body.applyLinearImpulse(new Vector2(player.acceleration * rigidBody.body.getMass(), 0), rigidBody.body.getWorldCenter(), true);
            }
        }
    }

}
