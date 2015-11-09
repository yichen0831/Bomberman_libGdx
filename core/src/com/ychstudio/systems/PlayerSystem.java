package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.components.Bomb;
import com.ychstudio.components.Player;
import com.ychstudio.components.Renderer;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;
import com.ychstudio.components.Transform;
import com.ychstudio.gamesys.GameManager;
import java.util.Queue;

public class PlayerSystem extends IteratingSystem {

    protected ComponentMapper<Player> mPlayer;
    protected ComponentMapper<Transform> mTransform;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<State> mState;
    protected ComponentMapper<Renderer> mRenderer;

    private boolean hitting;
    private boolean kicking;
    private Bomb kickingBomb;
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

        if (player.hp > 0 && player.state != Player.State.TELEPORTING) {
            // TODO: cheat code...
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                player.powerUpAmmo();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                player.powerUpPower();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                player.powerUpSpeed();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
                player.powerUpKick();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
                player.powerUpRemote();
            }

            // player movement controls
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                if (player.invincible || !hitBombVertical(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x, body.getPosition().y + 0.5f))) {
                    if (Math.abs(linearVelocity.y) < maxSpeed) {
                        body.applyLinearImpulse(new Vector2(0, player.acceleration * body.getMass()), body.getWorldCenter(), true);
                    }
                }

                player.state = Player.State.WALKING_UP;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (player.invincible || !hitBombVertical(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x, body.getPosition().y - 0.5f))) {
                    if (Math.abs(linearVelocity.y) < maxSpeed) {
                        body.applyLinearImpulse(new Vector2(0, -player.acceleration * body.getMass()), body.getWorldCenter(), true);
                    }
                }

                player.state = Player.State.WALKING_DOWN;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (player.invincible || !hitBombHorizontal(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x - 0.5f, body.getPosition().y))) {
                    if (Math.abs(linearVelocity.x) < maxSpeed) {
                        body.applyLinearImpulse(new Vector2(-player.acceleration * body.getMass(), 0), body.getWorldCenter(), true);
                    }
                }

                player.state = Player.State.WALKING_LEFT;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (player.invincible || !hitBombHorizontal(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x + 0.5f, body.getPosition().y))) {
                    if (Math.abs(linearVelocity.x) < maxSpeed) {
                        body.applyLinearImpulse(new Vector2(player.acceleration * body.getMass(), 0), body.getWorldCenter(), true);
                    }
                }

                player.state = Player.State.WALKING_RIGHT;
            }

            // set bomb or kick bomb
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                kicking = false;
                if (player.kickBomb) {
                    // check if player is facing a bomb, if so, kick it
                    switch (player.state) {
                        case WALKING_UP:
                            if (checkCanKickBomb(body, fromV.set(body.getPosition()), toV.set(new Vector2(body.getPosition().x, body.getPosition().y + 0.6f)))) {
                                kickingBomb.setMove(Bomb.State.MOVING_UP);
                                GameManager.getInstance().playSound("KickBomb.ogg");
                            }
                            break;
                        case WALKING_DOWN:
                            if (checkCanKickBomb(body, fromV.set(body.getPosition()), toV.set(new Vector2(body.getPosition().x, body.getPosition().y - 0.6f)))) {
                                kickingBomb.setMove(Bomb.State.MOVING_DOWN);
                                GameManager.getInstance().playSound("KickBomb.ogg");
                            }
                            break;
                        case WALKING_LEFT:
                            if (checkCanKickBomb(body, fromV.set(body.getPosition()), toV.set(new Vector2(body.getPosition().x - 0.6f, body.getPosition().y)))) {
                                kickingBomb.setMove(Bomb.State.MOVING_LEFT);
                                GameManager.getInstance().playSound("KickBomb.ogg");
                            }
                            break;
                        case WALKING_RIGHT:
                            if (checkCanKickBomb(body, fromV.set(body.getPosition()), toV.set(new Vector2(body.getPosition().x + 0.6f, body.getPosition().y)))) {
                                kickingBomb.setMove(Bomb.State.MOVING_RIGHT);
                                GameManager.getInstance().playSound("KickBomb.ogg");
                            }
                            break;
                        default:
                            break;
                    }
                }

                if (!kicking && player.bombLeft > 0) {
                    // create bomb
                    ActorBuilder actorBuilder = new ActorBuilder(body.getWorld(), world);

                    if (player.remoteBomb) {
                        GameManager.getInstance().getRemoteBombDeque().offer(
                                actorBuilder.createRemoteBomb(player, body.getPosition().x, body.getPosition().y)
                        );
                    } else {
                        actorBuilder.createBomb(player, body.getPosition().x, body.getPosition().y);
                    }
                    player.bombLeft--;
                    GameManager.getInstance().playSound("PlaceBomb.ogg");
                }

            }

            // trigger remote bomb
            if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && player.remoteBomb) {
                Queue<Entity> remoteBombQueue = GameManager.getInstance().getRemoteBombDeque();

                // clean those bomes which have already exploded
                while (!remoteBombQueue.isEmpty() && remoteBombQueue.peek().getComponent(Bomb.class) == null) {
                    remoteBombQueue.remove();
                }

                Entity remoteBombEntity = remoteBombQueue.poll();
                if (remoteBombEntity != null) {
                    Bomb remoteBomb = remoteBombEntity.getComponent(Bomb.class);
                    remoteBomb.countDown = 0;
                }
            }

            // re-generate bomb
            if (player.bombLeft < player.bombCapacity) {
                player.bombRegeratingTimeLeft -= world.getDelta();
            }
            if (player.bombRegeratingTimeLeft <= 0) {
                player.bombLeft++;
                player.bombRegeratingTimeLeft = player.bombRegeratingTime;
            }
        }

        // update bomb data to GameManager
        GameManager.playerBombLeft = player.bombLeft;
        GameManager.playerBombRegeratingTimeLeft = player.bombRegeratingTimeLeft;

        if (linearVelocity.len2() < 0.1f) {
            switch (player.state) {
                case WALKING_UP:
                    player.state = Player.State.IDLING_UP;
                    break;
                case WALKING_DOWN:
                    player.state = Player.State.IDLING_DOWN;
                    break;
                case WALKING_LEFT:
                    player.state = Player.State.IDLING_LEFT;
                    break;
                case WALKING_RIGHT:
                    player.state = Player.State.IDLING_RIGHT;
                    break;
                default:
                    break;
            }
        }

        // invincible timer
        player.invincibleCountDown -= world.getDelta();
        if (player.invincibleCountDown < 0) {
            player.invincible = false;
        }

        if (player.invincible) {
            Filter filter = body.getFixtureList().get(0).getFilterData();
            filter.maskBits = Player.invincibleMaskBit;
            body.getFixtureList().get(0).setFilterData(filter);
            renderer.setColor(new Color(1, 1, 1, 1.2f + MathUtils.sin(player.invincibleCountDown * 24)));
        } else {
            Filter filter = body.getFixtureList().get(0).getFilterData();
            filter.maskBits = Player.defaultMaskBits;
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

                if (state.getStateTime() <= 0) {
                    GameManager.getInstance().playSound("Die.ogg");
                }

                if (state.getStateTime() > 0.65f) {
                    World b2dWorld = body.getWorld();
                    b2dWorld.destroyBody(body);
                    mPlayer.set(entityId, false);
                    mRigidBody.set(entityId, false);
                    mState.set(entityId, false);
                    Transform transform = mTransform.get(entityId);
                    transform.z = 999;

                    GameManager.playerLives--;
                    if (!GameManager.infiniteLives && GameManager.playerLives <= 0) {
                        GameManager.gameOver = true;
                    } else {
                        ActorBuilder actorBuilder = new ActorBuilder(b2dWorld, world);
                        Vector2 respawnPosition = GameManager.getInstance().getPlayerRespawnPosition();
                        actorBuilder.createPlayer(respawnPosition.x, respawnPosition.y, GameManager.resetPlayerAbilities);
                    }
                }
                break;
            case TELEPORTING:
                state.setCurrentState("teleporting");
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

    protected boolean checkCanKickBomb(Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        kickingBomb = null;
        kicking = false;

        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getFilterData().categoryBits == GameManager.BOMB_BIT) {
                    Entity bombEntity = (Entity) fixture.getBody().getUserData();
                    kickingBomb = bombEntity.getComponent(Bomb.class);
                    return 0;
                }
                return 0;
            }
        };

        b2dWorld.rayCast(rayCastCallback, fromV, toV);
        if (kickingBomb != null) {
            kicking = true;
        }
        return kicking;
    }

    protected boolean hitBombVertical(final Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        hitting = false;

        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getBody() == body) {
                    return 1;
                }

                if (fraction < 1.0f && fixture.getFilterData().categoryBits == GameManager.BOMB_BIT) {
                    hitting = true;
                }
                return 0;
            }
        };

        for (int i = 0; i < 3; i++) {
            Vector2 tmpV = new Vector2(toV);
            b2dWorld.rayCast(rayCastCallback, fromV, tmpV.add((1 - i) * 0.4f, 0));

        }
        return hitting;
    }

    protected boolean hitBombHorizontal(final Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        hitting = false;

        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getBody() == body) {
                    return 1;
                }

                if (fraction < 1.0f && fixture.getFilterData().categoryBits == GameManager.BOMB_BIT) {
                    hitting = true;
                }
                return 0;
            }
        };

        for (int i = 0; i < 3; i++) {
            Vector2 tmpV = new Vector2(toV);
            b2dWorld.rayCast(rayCastCallback, fromV, tmpV.add(0, (1 - i) * 0.4f));
        }
        return hitting;
    }
}
