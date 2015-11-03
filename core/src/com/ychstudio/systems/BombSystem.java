package com.ychstudio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
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

        // center
        for (int i = 0; i < 5; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 16, 16, 16));
        }
        anim = new Animation(0.15f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("exploding", anim);

        Renderer renderer = new Renderer(textureRegion, 1, 1);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        new EntityBuilder(world)
                .with(
                        new Explosion(),
                        new Transform(x, y, 1, 1, 0),
                        new State("exploding"),
                        new Anim(anims),
                        renderer
                )
                .build();

        // up
        for (int i = 0; i < bomb.power; i++) {
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
                            new State("exploding"),
                            new Anim(anims),
                            renderer
                    )
                    .build();
        }

        // down
        for (int i = 0; i < bomb.power; i++) {
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
                            new State("exploding"),
                            new Anim(anims),
                            renderer
                    )
                    .build();
        }

        // left
        for (int i = 0; i < bomb.power; i++) {
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
                            new State("exploding"),
                            new Anim(anims),
                            renderer
                    )
                    .build();
        }

        // right
        for (int i = 0; i < bomb.power; i++) {
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
                            new State("exploding"),
                            new Anim(anims),
                            renderer
                    )
                    .build();
        }
    }

}
