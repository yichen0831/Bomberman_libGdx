package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.components.Anim;
import com.ychstudio.components.Bomb;
import com.ychstudio.components.Player;
import com.ychstudio.components.Renderer;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;
import com.ychstudio.components.Transform;
import com.ychstudio.gamesys.GameManager;
import java.util.HashMap;

public class PlayerSystem extends IteratingSystem {

    protected ComponentMapper<Player> mPlayer;
    protected ComponentMapper<Transform> mTransform;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<State> mState;

    private final AssetManager assetManager;

    private boolean hit;
    private Vector2 fromV;
    private Vector2 toV;

    public PlayerSystem() {
        super(Aspect.all(Player.class, Transform.class, RigidBody.class, State.class));
        assetManager = GameManager.getInstance().getAssetManager();
        fromV = new Vector2();
        toV = new Vector2();
    }

    @Override
    protected void process(int entityId) {
        Player player = mPlayer.get(entityId);
        RigidBody rigidBody = mRigidBody.get(entityId);
        State state = mState.get(entityId);

        Vector2 linearVelocity = rigidBody.body.getLinearVelocity();

        float maxSpeed = player.maxSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (!hitBombVertical(rigidBody.body, fromV.set(rigidBody.body.getPosition()), toV.set(rigidBody.body.getPosition().x, rigidBody.body.getPosition().y + 0.5f))) {
                if (Math.abs(linearVelocity.y) < maxSpeed) {
                    rigidBody.body.applyLinearImpulse(new Vector2(0, player.acceleration * rigidBody.body.getMass()), rigidBody.body.getWorldCenter(), true);
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (!hitBombVertical(rigidBody.body, fromV.set(rigidBody.body.getPosition()), toV.set(rigidBody.body.getPosition().x, rigidBody.body.getPosition().y - 0.5f))) {
                if (Math.abs(linearVelocity.y) < maxSpeed) {
                    rigidBody.body.applyLinearImpulse(new Vector2(0, -player.acceleration * rigidBody.body.getMass()), rigidBody.body.getWorldCenter(), true);
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (!hitBombHorizontal(rigidBody.body, fromV.set(rigidBody.body.getPosition()), toV.set(rigidBody.body.getPosition().x - 0.5f, rigidBody.body.getPosition().y))) {
                if (Math.abs(linearVelocity.x) < maxSpeed) {
                    rigidBody.body.applyLinearImpulse(new Vector2(-player.acceleration * rigidBody.body.getMass(), 0), rigidBody.body.getWorldCenter(), true);
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (!hitBombHorizontal(rigidBody.body, fromV.set(rigidBody.body.getPosition()), toV.set(rigidBody.body.getPosition().x + 0.5f, rigidBody.body.getPosition().y))) {
                if (Math.abs(linearVelocity.x) < maxSpeed) {
                    rigidBody.body.applyLinearImpulse(new Vector2(player.acceleration * rigidBody.body.getMass(), 0), rigidBody.body.getWorldCenter(), true);
                }
            }
        }

        // set bomb
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // create bomb
            createBomb(rigidBody, player);
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

    private void createBomb(RigidBody rigidBody, Player player) {
        // box2d
        World b2dWorld = rigidBody.body.getWorld();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(MathUtils.floor(rigidBody.body.getPosition().x) + 0.5f, MathUtils.floor(rigidBody.body.getPosition().y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.45f, 0.45f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.BOMB_BIT;
        fixtureDef.filter.maskBits = GameManager.INDESTRUCTIIBLE_BIT | GameManager.BREAKABLE_BIT;
        body.createFixture(fixtureDef);
        polygonShape.dispose();

        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        HashMap<String, Animation> anims = new HashMap<String, Animation>();
        TextureRegion textureRegion = textureAtlas.findRegion("Bomb");

        Animation anim;
        Array<TextureRegion> keyFrames = new Array<TextureRegion>();
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 16));
        }
        anim = new Animation(0.15f, keyFrames, Animation.PlayMode.LOOP_PINGPONG);
        anims.put("normal", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new EntityBuilder(world)
                .with(
                        new Bomb(player.bombPower, 2.0f),
                        new Transform(body.getPosition().x, body.getPosition().y, 1, 1, 0),
                        new RigidBody(body),
                        new State("normal"),
                        renderer,
                        new Anim(anims)
                )
                .build();

        body.setUserData(e);
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
