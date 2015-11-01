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
//        Transform transform = mTransform.get(i);
        RigidBody rigidBody = mRigidBody.get(i);
        State state = mState.get(i);

        Vector2 linearVelocity = rigidBody.body.getLinearVelocity();

        float maxSpeed = player.maxSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (Math.abs(linearVelocity.y) < maxSpeed) {
                rigidBody.body.applyLinearImpulse(new Vector2(0, player.acceleration * rigidBody.body.getMass()), rigidBody.body.getWorldCenter(), true);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (Math.abs(linearVelocity.y) < maxSpeed) {
                rigidBody.body.applyLinearImpulse(new Vector2(0, -player.acceleration * rigidBody.body.getMass()), rigidBody.body.getWorldCenter(), true);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (Math.abs(linearVelocity.x) < maxSpeed) {
                rigidBody.body.applyLinearImpulse(new Vector2(-player.acceleration * rigidBody.body.getMass(), 0), rigidBody.body.getWorldCenter(), true);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (Math.abs(linearVelocity.x) < maxSpeed) {
                rigidBody.body.applyLinearImpulse(new Vector2(player.acceleration * rigidBody.body.getMass(), 0), rigidBody.body.getWorldCenter(), true);
            }
        }

        if (linearVelocity.x > 0.1f) {
            player.state = Player.State.WALKING_RIGHT;
        } else if (linearVelocity.x < -0.1f) {
            player.state = Player.State.WALKING_LEFT;
        } else if (linearVelocity.y > 0.1f) {
            player.state = Player.State.WALKING_UP;
        } else if (linearVelocity.y < -0.1f) {
            player.state = Player.State.WALKING_DOWN;
        } else {
            if (player.state == Player.State.WALKING_UP) {
                player.state = Player.State.IDLING_UP;
            } else if (player.state == Player.State.WALKING_LEFT) {
                player.state = Player.State.IDLING_LEFT;
            } else if (player.state == Player.State.WALKING_DOWN) {
                player.state = Player.State.IDLING_DOWN;
            } else if (player.state == Player.State.WALKING_RIGHT) {
                player.state = Player.State.IDLING_RIGHT;
            }

        }

        switch (player.state) {
            case WALKING_UP:
                state.setCurrentState("walking_up");
                break;
            case WALKING_LEFT:
                state.setCurrentState("walking_left");
                break;
            case WALKING_DOWN:
                state.setCurrentState("walking_down");
                break;
            case WALKING_RIGHT:
                state.setCurrentState("walking_right");
                break;
            case IDLING_LEFT:
                state.setCurrentState("idling_left");
                break;
            case IDLING_RIGHT:
                state.setCurrentState("idling_right");
                break;
            case IDLING_UP:
                state.setCurrentState("idling_up");
                break;
            case IDLING_DOWN:
            default:
                state.setCurrentState("idling_down");
                break;
        }

    }
}
