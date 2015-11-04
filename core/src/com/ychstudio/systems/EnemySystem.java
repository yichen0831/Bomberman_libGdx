package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.components.Enemy;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;
import com.ychstudio.components.Transform;
import com.ychstudio.gamesys.GameManager;

public class EnemySystem extends IteratingSystem {

    protected ComponentMapper<Enemy> mEnemy;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<State> mState;
    protected ComponentMapper<Transform> mTransform;

    private boolean hit;
    private Vector2 fromVector;
    private Vector2 toVector;

    public EnemySystem() {
        super(Aspect.all(Enemy.class, Transform.class, RigidBody.class, State.class));
        fromVector = new Vector2();
        toVector = new Vector2();
    }

    protected boolean hitSomethingVertical(final Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        hit = false;

        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                // if hit the player, ignore it
                if (fixture.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                    return 0;
                }
                
                if (fraction < 1.0f) {
                    hit = true;
                }
                return 0;
            }
        };

        for (int i = 0; i < 3; i++) {
            Vector2 tmpV = new Vector2(toV);
            b2dWorld.rayCast(rayCastCallback, fromV, tmpV.add((1 - i) * 0.4f, 0));

        }
        return hit;
    }

    protected boolean hitSomethingHorizontal(final Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        hit = false;

        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                // if hit the player, ignore it
                if (fixture.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                    return 0;
                }

                if (fraction < 1.0f) {
                    hit = true;
                }
                return 0;
            }
        };

        for (int i = 0; i < 3; i++) {
            Vector2 tmpV = new Vector2(toV);
            b2dWorld.rayCast(rayCastCallback, fromV, tmpV.add(0, (1 - i) * 0.4f));

        }
        return hit;
    }

    protected void changeWalkingState(Enemy enemy) {
        enemy.state = Enemy.State.getRandomWalkingState();
    }

    @Override
    protected void process(int entityId) {
        Enemy enemy = mEnemy.get(entityId);
        RigidBody rigidBody = mRigidBody.get(entityId);
        State state = mState.get(entityId);

        Body body = rigidBody.body;
        
        if (enemy.hp <= 0) {
            enemy.state = Enemy.State.DYING;
        }

        switch (enemy.state) {
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
                Filter filter = body.getFixtureList().get(0).getFilterData();
                filter.maskBits = GameManager.NOTHING_BIT;
                body.getFixtureList().get(0).setFilterData(filter);
                if (state.getStateTime() > 0.6f) {
                    body.getWorld().destroyBody(body);
                    mRigidBody.set(entityId, false);
                    mEnemy.set(entityId, false);
                    mState.set(entityId, false);
                    Transform transform = mTransform.get(entityId);
                    transform.z = 999;
                }

                // TODO: chance to create power-up item
                break;
            case WALKING_LEFT:
                state.setCurrentState("walking_left");
                if (body.getLinearVelocity().x > -enemy.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(-enemy.getSpeed() * body.getMass(), 0), body.getWorldCenter(), true);
                }
                if (hitSomethingHorizontal(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x - 0.5f, body.getPosition().y))) {
                    changeWalkingState(enemy);
                }
                break;
            case WALKING_RIGHT:
                state.setCurrentState("walking_right");
                if (body.getLinearVelocity().x < enemy.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(enemy.getSpeed() * body.getMass(), 0), body.getWorldCenter(), true);
                }
                if (hitSomethingHorizontal(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x + 0.5f, body.getPosition().y))) {
                    changeWalkingState(enemy);
                }
                break;
            case WALKING_UP:
                state.setCurrentState("walking_up");
                if (body.getLinearVelocity().y < enemy.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(0, enemy.getSpeed() * body.getMass()), body.getWorldCenter(), true);
                }
                if (hitSomethingVertical(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x, body.getPosition().y + 0.5f))) {
                    changeWalkingState(enemy);
                }
                break;
            case WALKING_DOWN:
            default:
                state.setCurrentState("walking_down");
                if (body.getLinearVelocity().y > -enemy.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(0, -enemy.getSpeed() * body.getMass()), body.getWorldCenter(), true);
                }
                if (hitSomethingVertical(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x, body.getPosition().y - 0.5f))) {
                    changeWalkingState(enemy);
                }
                break;
        }
    }

}
