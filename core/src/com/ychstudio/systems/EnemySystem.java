package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.ychstudio.components.Enemy;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;

public class EnemySystem extends IteratingSystem {

    protected ComponentMapper<Enemy> mEnemy;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<State> mState;

    public EnemySystem() {
        super(Aspect.all(Enemy.class, RigidBody.class, State.class));
    }

    @Override
    protected void process(int i) {
        Enemy enemy = mEnemy.get(i);
        RigidBody rigidBody = mRigidBody.get(i);
        State state = mState.get(i);

        switch (enemy.getCurrentState()) {
            case ATTACKING_LEFT:
                state.setCurrentState("attacking_left");
                break;
            case ATTACKING_RIGHT:
                state.setCurrentState("attacking_right");
                break;
            case ATTACKING_UP:
                state.setCurrentState("attacking_up");
                break;
            case ATTACKING_DOWN:
                state.setCurrentState("attacking_down");
                break;
            case DYING:
                state.setCurrentState("dying");
                break;
            case WALKING_LEFT:
                state.setCurrentState("walking_left");
                break;
            case WALKING_RIGHT:
                state.setCurrentState("walking_right");
                break;
            case WALKING_UP:
                state.setCurrentState("walking_up");
                break;
            case WALKING_DOWN:
            default:
                state.setCurrentState("walking_down");
                break;
        }
    }

}
