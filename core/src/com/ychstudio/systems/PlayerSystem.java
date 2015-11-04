package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.components.Player;
import com.ychstudio.components.Renderer;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;
import com.ychstudio.components.Transform;
import com.ychstudio.gamesys.GameManager;

public class PlayerSystem extends IteratingSystem {

    protected ComponentMapper<Player> mPlayer;
    protected ComponentMapper<Transform> mTransform;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<State> mState;
    protected ComponentMapper<Renderer> mRenderer;

    private boolean hit;
    private final Vector2 fromV;
    private final Vector2 toV;

    public PlayerSystem() {
        super(Aspect.all(Player.class, Transform.class, Renderer.class, RigidBody.class, State.class));
        fromV = new Vector2();
        toV = new Vector2();
    }

    @Override
    protected void process(int entityId) {
        Player player = mPlayer.get(entityId);
        RigidBody rigidBody = mRigidBody.get(entityId);
        State state = mState.get(entityId);
        Renderer renderer = mRenderer.get(entityId);

        Body body = rigidBody.body;

        Vector2 linearVelocity = body.getLinearVelocity();

        float maxSpeed = player.maxSpeed;

        if (player.hp > 0) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                if (!hitBombVertical(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x, body.getPosition().y + 0.5f))) {
                    if (Math.abs(linearVelocity.y) < maxSpeed) {
                        body.applyLinearImpulse(new Vector2(0, player.acceleration * body.getMass()), body.getWorldCenter(), true);
                    }
                }
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (!hitBombVertical(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x, body.getPosition().y - 0.5f))) {
                    if (Math.abs(linearVelocity.y) < maxSpeed) {
                        body.applyLinearImpulse(new Vector2(0, -player.acceleration * body.getMass()), body.getWorldCenter(), true);
                    }
                }
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (!hitBombHorizontal(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x - 0.5f, body.getPosition().y))) {
                    if (Math.abs(linearVelocity.x) < maxSpeed) {
                        body.applyLinearImpulse(new Vector2(-player.acceleration * body.getMass(), 0), body.getWorldCenter(), true);
                    }
                }
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (!hitBombHorizontal(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x + 0.5f, body.getPosition().y))) {
                    if (Math.abs(linearVelocity.x) < maxSpeed) {
                        body.applyLinearImpulse(new Vector2(player.acceleration * body.getMass(), 0), body.getWorldCenter(), true);
                    }
                }
            }

            // set bomb
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                // create bomb
                ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);
                actorBuilder.createBomb(player, body.getPosition().x, body.getPosition().y);
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

        // invincible timer
        player.invincibleCountDown -= world.getDelta();
        if (player.invincibleCountDown < 0) {
            player.invincible = false;
        }

        if (player.invincible) {
            Filter filter = body.getFixtureList().get(0).getFilterData();
            filter.maskBits = GameManager.INDESTRUCTIIBLE_BIT | GameManager.BREAKABLE_BIT;
            body.getFixtureList().get(0).setFilterData(filter);
            renderer.setColor(new Color(1, 1, 1, 1f + (float) Math.sin(player.invincibleCountDown * 20)));
        } else {
            Filter filter = body.getFixtureList().get(0).getFilterData();
            filter.maskBits = GameManager.INDESTRUCTIIBLE_BIT | GameManager.BREAKABLE_BIT | GameManager.ENEMY_BIT | GameManager.EXPLOSION_BIT;
            body.getFixtureList().get(0).setFilterData(filter);
            renderer.setColor(Color.WHITE);
        }

        if (player.hp <= 0) {
            player.state = Player.State.DYING;
        }

        switch (player.state) {
            case DYING:
                state.setCurrentState("dying");
                Filter filter = body.getFixtureList().get(0).getFilterData();
                filter.maskBits = GameManager.NOTHING_BIT;
                body.getFixtureList().get(0).setFilterData(filter);
                if (state.getStateTime() > 0.65f) {
                    World b2dWorld = body.getWorld();
                    b2dWorld.destroyBody(body);
                    mPlayer.set(entityId, false);
                    mRigidBody.set(entityId, false);
                    mState.set(entityId, false);
                    Transform transform = mTransform.get(entityId);
                    transform.z = 999;

                    ActorBuilder actorBuilder = new ActorBuilder(b2dWorld, world);
                    Vector2 respawnPosition = GameManager.getInstance().getPlayerRespawnPosition();
                    actorBuilder.createPlayer(respawnPosition.x, respawnPosition.y);
                }
                break;
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

    protected boolean hitBombVertical(final Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        hit = false;

        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getBody() == body) {
                    return 1;
                }

                if (fraction < 1.0f && fixture.getFilterData().categoryBits == GameManager.BOMB_BIT) {
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

    protected boolean hitBombHorizontal(final Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        hit = false;

        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getBody() == body) {
                    return 1;
                }

                if (fraction < 1.0f && fixture.getFilterData().categoryBits == GameManager.BOMB_BIT) {
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
}
