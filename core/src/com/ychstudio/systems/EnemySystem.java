package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.builders.ActorBuilder;
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

    private Enemy enemy;
    private RigidBody rigidBody;
    private State state;

    private Vector2[] boss1TargetCorners = {
        new Vector2(3f, 7.5f),
        new Vector2(12f, 7.5f),
        new Vector2(7.5f, 12f),
        new Vector2(7.5f, 5.5f)
    };

    private int boss1CurrentTarget = MathUtils.random(0, 3);

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
                if (fixture.getFilterData().categoryBits == GameManager.PLAYER_BIT || fixture.getFilterData().categoryBits == GameManager.POWERUP_BIT) {
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
                // if hit the player or power-up item, ignore it
                if (fixture.getFilterData().categoryBits == GameManager.PLAYER_BIT || fixture.getFilterData().categoryBits == GameManager.POWERUP_BIT) {
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
        enemy = mEnemy.get(entityId);
        rigidBody = mRigidBody.get(entityId);
        state = mState.get(entityId);

        switch (enemy.type) {
            case "boss1":
                handleBoss1(entityId);
                break;
            case "bomb":
                handleBombEnemy(entityId);
                break;
            default:
                handleBasics(entityId);
                break;
        }
    }

    private void handleBasics(int entityId) {
        Body body = rigidBody.body;

        if (enemy.receivedDamage > 0) {
            enemy.damage(enemy.receivedDamage);
            enemy.receivedDamage = 0;
        }

        if (enemy.hp <= 0) {
            enemy.state = Enemy.State.DYING;
            enemy.lifetime = 0;
        } else {
            enemy.lifetime += world.getDelta();
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

                if (state.getStateTime() <= 0) {
                    GameManager.getInstance().playSound(enemy.getDieSound(), 1.0f, MathUtils.random(0.8f, 1.2f), 0);
                }

                if (state.getStateTime() > 0.6f) {
                    // decrease enemy count
                    GameManager.enemiesLeft--;

                    // if no enemy left, create the portal
                    if (GameManager.enemiesLeft <= 0) {
                        ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                        actorBuilder.createPortal();
                        GameManager.getInstance().playSound("PortalAppears.ogg");
                    }

                    // chance to create PowerUp item
                    if (Math.random() < 0.2) {
                        ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                        actorBuilder.createPowerUp(body.getPosition().x, body.getPosition().y);
                    }

                    body.getWorld().destroyBody(body);
                    mRigidBody.set(entityId, false);
                    mEnemy.set(entityId, false);
                    mState.set(entityId, false);
                    Transform transform = mTransform.get(entityId);
                    transform.z = 999;
                }
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

    private void handleBombEnemy(int entityId) {
        Body body = rigidBody.body;

        if (enemy.receivedDamage > 0) {
            enemy.damage(enemy.receivedDamage);
            enemy.receivedDamage = 0;
        }

        if (enemy.hp <= 0) {
            enemy.state = Enemy.State.DYING;
            enemy.lifetime = 0;
        } else {
            enemy.lifetime += world.getDelta();
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
                if (state.getStateTime() > 3f) {
                    enemy.state = Enemy.State.getRandomWalkingState();
                    ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                    actorBuilder.createBombEnemy(body.getPosition().x, body.getPosition().y);
                }
                break;
            case ATTACKING_DOWN:
                state.setCurrentState("attacking_down");
                break;
            case DYING:
                state.setCurrentState("dying");
                Filter filter = body.getFixtureList().get(0).getFilterData();
                filter.maskBits = GameManager.NOTHING_BIT;
                body.getFixtureList().get(0).setFilterData(filter);

                if (state.getStateTime() <= 0) {
                    GameManager.getInstance().playSound("Explosion.ogg", 1.0f, MathUtils.random(0.6f, 0.8f), 0);
                    ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                    actorBuilder.createExplosion(body.getPosition().x, body.getPosition().y, 1);
                }

                if (state.getStateTime() > 0.6f) {
                    // decrease enemy count
                    GameManager.enemiesLeft--;

                    // if no enemy left, create the portal
                    if (GameManager.enemiesLeft <= 0) {
                        ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                        actorBuilder.createPortal();
                        GameManager.getInstance().playSound("PortalAppears.ogg");
                    }

                    // chance to create PowerUp item
                    if (Math.random() < 0.2) {
                        ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                        actorBuilder.createPowerUp(body.getPosition().x, body.getPosition().y);
                    }

                    body.getWorld().destroyBody(body);
                    mRigidBody.set(entityId, false);
                    mEnemy.set(entityId, false);
                    mState.set(entityId, false);
                    Transform transform = mTransform.get(entityId);
                    transform.z = 999;

                    world.delete(entityId);
                }
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

    private void handleBoss1(int entityId) {
        Body body = rigidBody.body;

        if (boss1TargetCorners[boss1CurrentTarget].dst2(body.getPosition()) < 0.1f) {
            boss1CurrentTarget = MathUtils.random(0, 3);
        }

        if (enemy.receivedDamage > 0) {
            if (enemy.state != Enemy.State.DAMAGED) {
                enemy.damage(1);    // boss only take 1 damage per time

                // chance to create PowerUp item
                if (Math.random() < 0.2) {
                    ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                    actorBuilder.createPowerUp(body.getPosition().x, body.getPosition().y - 2f);
                }
            }
            enemy.receivedDamage = 0;
            enemy.state = Enemy.State.DAMAGED;
        }

        if (enemy.hp <= 0) {
            enemy.state = Enemy.State.DYING;
        }

        enemy.lifetime += world.getDelta();

        // Boss1 attack
        if (enemy.hp > 0 && MathUtils.random() < 0.002) {
            enemy.state = Enemy.State.ATTACKING_DOWN;
        }

        switch (enemy.state) {
            case ATTACKING_LEFT:
            case ATTACKING_RIGHT:
            case ATTACKING_UP:
            case ATTACKING_DOWN:
                state.setCurrentState("attacking_down");
                if (state.getStateTime() > 0.6f) {
                    ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                    actorBuilder.createExplosion(body.getPosition().x, body.getPosition().y - 4f, 1);
                    GameManager.getInstance().playSound("Boss1Hammer.ogg");
                    changeWalkingState(enemy);
                }
                break;
            case DAMAGED:
                state.setCurrentState("damaged");
                if (state.getStateTime() > 0.2f) {
                    changeWalkingState(enemy);
                }
                break;
            case DYING:
                state.setCurrentState("dying");
                Filter filter = body.getFixtureList().get(0).getFilterData();
                filter.maskBits = GameManager.NOTHING_BIT;
                body.getFixtureList().get(0).setFilterData(filter);

                if (state.getStateTime() <= 0) {
                    // TODO: create boss explosion effect
                    enemy.lifetime = 0;
                }

                if (enemy.lifetime > 0.4f) {
                    GameManager.getInstance().playSound("Explosion.ogg", 1.0f, MathUtils.random(0.9f, 1.1f), 0);
                    enemy.lifetime -= 0.4f;
                }

                if (state.getStateTime() > 2.2f) {
                    // decrease enemy count
                    GameManager.enemiesLeft--;

                    // if no enemy left, create the portal
                    if (GameManager.enemiesLeft <= 0) {
                        ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                        actorBuilder.createPortal();
                        GameManager.getInstance().playSound("PortalAppears.ogg");
                        GameManager.getInstance().playMusic("Victory.ogg", false);
                    }

                    body.getWorld().destroyBody(body);
                    world.delete(entityId);
                }
                break;
            case WALKING_LEFT:
            case WALKING_RIGHT:
            case WALKING_UP:
            case WALKING_DOWN:
            default:
                state.setCurrentState("walking_down");
                toVector.set(boss1TargetCorners[boss1CurrentTarget]);
                toVector.sub(body.getPosition());
                toVector.nor();

                if (body.getLinearVelocity().len2() < enemy.getSpeed() * enemy.getSpeed()) {
                    body.applyLinearImpulse(toVector.scl(enemy.getSpeed()), body.getWorldCenter(), true);
                }
                break;
        }
    }
}
