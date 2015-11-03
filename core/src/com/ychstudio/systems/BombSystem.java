package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.components.Anim;
import com.ychstudio.components.Bomb;
import com.ychstudio.components.Explosion;
import com.ychstudio.components.Renderer;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;
import com.ychstudio.components.Transform;
import com.ychstudio.gamesys.GameManager;
import java.util.HashMap;

public class BombSystem extends IteratingSystem {

    protected ComponentMapper<Bomb> mBomb;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<State> mState;

    private AssetManager assetManager;

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
                createExplosion(bomb, body);

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

    private void createExplosion(Bomb bomb, Body body) {
        TextureRegion textureRegion = assetManager.get("img/actors.pack", TextureAtlas.class).findRegion("Explosion");
        HashMap<String, Animation> anims = new HashMap<String, Animation>();

        Array<TextureRegion> keyFrames = new Array<TextureRegion>();
        Animation anim;

        float x = body.getPosition().x;
        float y = body.getPosition().y;

        World b2dWorld = body.getWorld();

        // center
        // box2d
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);
        Body explosionBody = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
        fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.BOMB_BIT | GameManager.ENEMY_BIT | GameManager.BREAKABLE_BIT;
        fixtureDef.isSensor = true;
        Fixture fixture = explosionBody.createFixture(fixtureDef);

        for (int i = 0; i < 5; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 16, 16, 16));
        }
        anim = new Animation(0.15f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("exploding", anim);

        Renderer renderer = new Renderer(textureRegion, 1, 1);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        Entity e = new EntityBuilder(world)
                .with(
                        new Explosion(),
                        new Transform(x, y, 1, 1, 0),
                        new RigidBody(explosionBody),
                        new State("exploding"),
                        new Anim(anims),
                        renderer
                )
                .build();
        explosionBody.setUserData(e);

        // up
        for (int i = 0; i < bomb.power; i++) {
            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(x, y + i + 1);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.BOMB_BIT | GameManager.ENEMY_BIT | GameManager.BREAKABLE_BIT;
            fixtureDef.isSensor = true;
            fixture = explosionBody.createFixture(fixtureDef);

            keyFrames.clear();
            anims = new HashMap<String, Animation>();

            for (int j = 0; j < 5; j++) {
                if (i == bomb.power - 1) {
                    keyFrames.add(new TextureRegion(textureRegion, j * 16, 0, 16, 16));

                } else {
                    keyFrames.add(new TextureRegion(textureRegion, j * 16, 16 * 2, 16, 16));
                }
            }
            anim = new Animation(0.15f, keyFrames, Animation.PlayMode.NORMAL);
            anims.put("exploding", anim);

            renderer = new Renderer(textureRegion, 1, 1);
            renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

            new EntityBuilder(world)
                    .with(
                            new Explosion(),
                            new Transform(x, y + i + 1, 1, 1, 0),
                            new RigidBody(explosionBody),
                            new State("exploding"),
                            new Anim(anims),
                            renderer
                    )
                    .build();
            explosionBody.setUserData(e);
        }

        // down
        for (int i = 0; i < bomb.power; i++) {
            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(x, y - i - 1);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.BOMB_BIT | GameManager.ENEMY_BIT | GameManager.BREAKABLE_BIT;
            fixtureDef.isSensor = true;
            fixture = explosionBody.createFixture(fixtureDef);

            keyFrames.clear();
            anims = new HashMap<String, Animation>();

            for (int j = 0; j < 5; j++) {
                if (i == bomb.power - 1) {
                    keyFrames.add(new TextureRegion(textureRegion, j * 16, 16 * 3, 16, 16));

                } else {
                    keyFrames.add(new TextureRegion(textureRegion, j * 16, 16 * 2, 16, 16));
                }
            }
            anim = new Animation(0.15f, keyFrames, Animation.PlayMode.NORMAL);
            anims.put("exploding", anim);

            renderer = new Renderer(textureRegion, 1, 1);
            renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

            new EntityBuilder(world)
                    .with(
                            new Explosion(),
                            new Transform(x, y - i - 1, 1, 1, 0),
                            new RigidBody(explosionBody),
                            new State("exploding"),
                            new Anim(anims),
                            renderer
                    )
                    .build();
            explosionBody.setUserData(e);
        }

        // left
        for (int i = 0; i < bomb.power; i++) {
            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(x - i - 1, y);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.BOMB_BIT | GameManager.ENEMY_BIT | GameManager.BREAKABLE_BIT;
            fixtureDef.isSensor = true;
            fixture = explosionBody.createFixture(fixtureDef);

            keyFrames.clear();
            anims = new HashMap<String, Animation>();

            for (int j = 0; j < 5; j++) {
                if (i == bomb.power - 1) {
                    keyFrames.add(new TextureRegion(textureRegion, j * 16, 16 * 6, 16, 16));

                } else {
                    keyFrames.add(new TextureRegion(textureRegion, j * 16, 16 * 4, 16, 16));
                }
            }
            anim = new Animation(0.15f, keyFrames, Animation.PlayMode.NORMAL);
            anims.put("exploding", anim);

            renderer = new Renderer(textureRegion, 1, 1);
            renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

            new EntityBuilder(world)
                    .with(
                            new Explosion(),
                            new Transform(x - i - 1, y, 1, 1, 0),
                            new RigidBody(explosionBody),
                            new State("exploding"),
                            new Anim(anims),
                            renderer
                    )
                    .build();
            explosionBody.setUserData(e);
        }

        // right
        for (int i = 0; i < bomb.power; i++) {
            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(x + i + 1, y);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.BOMB_BIT | GameManager.ENEMY_BIT | GameManager.BREAKABLE_BIT;
            fixtureDef.isSensor = true;
            fixture = explosionBody.createFixture(fixtureDef);

            keyFrames.clear();
            anims = new HashMap<String, Animation>();

            for (int j = 0; j < 5; j++) {
                if (i == bomb.power - 1) {
                    keyFrames.add(new TextureRegion(textureRegion, j * 16, 16 * 5, 16, 16));

                } else {
                    keyFrames.add(new TextureRegion(textureRegion, j * 16, 16 * 4, 16, 16));
                }
            }
            anim = new Animation(0.15f, keyFrames, Animation.PlayMode.NORMAL);
            anims.put("exploding", anim);

            renderer = new Renderer(textureRegion, 1, 1);
            renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

            new EntityBuilder(world)
                    .with(
                            new Explosion(),
                            new Transform(x + i + 1, y, 1, 1, 0),
                            new RigidBody(explosionBody),
                            new State("exploding"),
                            new Anim(anims),
                            renderer
                    )
                    .build();
            explosionBody.setUserData(e);
        }

        polygonShape.dispose();
    }

}
