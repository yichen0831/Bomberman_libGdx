package com.ychstudio.builders;

import com.artemis.Entity;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.components.Anim;
import com.ychstudio.components.Bomb;
import com.ychstudio.components.Breakable;
import com.ychstudio.components.Enemy;
import com.ychstudio.components.Explosion;
import com.ychstudio.components.Player;
import com.ychstudio.components.PowerUp;
import com.ychstudio.components.Renderer;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;
import com.ychstudio.components.Transform;
import com.ychstudio.gamesys.GameManager;
import java.util.HashMap;

public class ActorBuilder {

    protected final float radius = 0.45f;

    private final World b2dWorld;
    private final com.artemis.World world;

    private final AssetManager assetManager;

    private final Vector2 fromV;
    private final Vector2 toV;
    private boolean canExplodeThrough;

    public ActorBuilder(World b2dWorld, com.artemis.World world) {
        this.b2dWorld = b2dWorld;
        this.world = world;
        this.assetManager = GameManager.getInstance().getAssetManager();

        fromV = new Vector2();
        toV = new Vector2();
    }

    public void createWall(float x, float y, float mapWidth, float mapHeight, TextureAtlas tileTextureAtlas) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.INDESTRUCTIIBLE_BIT;
        fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.ENEMY_BIT | GameManager.BOMB_BIT;
        body.createFixture(fixtureDef);

        polygonShape.dispose();

        Renderer renderer;

        if (x < 1.0f) {
            if (y < 1.0f) {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 0, 16 * 2, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
            } else if (y > mapHeight - 1) {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 0, 16 * 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            } else {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 0, 16 * 1, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            }
        } else if (x > mapWidth - 1) {
            if (y < 1.0f) {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 16 * 2, 16 * 2, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            } else if (y > mapHeight - 1) {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 16 * 2, 16 * 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            } else {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 16 * 2, 16 * 1, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            }
        } else {
            if (y < 1.0f) {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 16 * 1, 16 * 2, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            } else if (y > mapHeight - 1) {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 16 * 1, 16 * 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            } else {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 0, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
            }

        }

        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);
        new EntityBuilder(world)
                .with(
                        new Transform(x, y, 1f, 1f, 0),
                        renderer
                )
                .build();
    }

    public void createIndestructible(float x, float y, TextureAtlas tileTextureAtlas) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.INDESTRUCTIIBLE_BIT;
        fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.ENEMY_BIT | GameManager.BOMB_BIT;
        body.createFixture(fixtureDef);

        polygonShape.dispose();

        Renderer renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("indestructible"), 0, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        new EntityBuilder(world)
                .with(
                        new Transform(x, y, 1f, 1f, 0),
                        renderer
                )
                .build();
    }

    public void createBreakable(float x, float y, TextureAtlas tileTextureAtlas) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.BREAKABLE_BIT;
        fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.ENEMY_BIT | GameManager.BOMB_BIT | GameManager.EXPLOSION_BIT;
        body.createFixture(fixtureDef);

        polygonShape.dispose();

        HashMap<String, Animation> anims = new HashMap<String, Animation>();
        TextureRegion textureRegion = tileTextureAtlas.findRegion("breakable");

        Animation anim;
        Array<TextureRegion> keyFrames = new Array<TextureRegion>();
        for (int i = 0; i < 4; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 16));
        }
        anim = new Animation(0.15f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("normal", anim);

        keyFrames.clear();
        for (int i = 4; i < 10; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 16));
        }
        anim = new Animation(0.125f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("exploding", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        Entity e = new EntityBuilder(world)
                .with(
                        new Breakable(),
                        new Transform(x, y, 1, 1, 0),
                        new RigidBody(body),
                        new State("normal"),
                        renderer,
                        new Anim(anims)
                )
                .build();

        body.setUserData(e);
    }

    public void createOctopus(float x, float y) {
        // box2d
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 12.0f;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.ENEMY_BIT;
        fixtureDef.filter.maskBits = Enemy.defaultMaskBits;
        body.createFixture(fixtureDef);

        circleShape.dispose();

        // animation
        HashMap<String, Animation> anims = new HashMap<String, Animation>();
        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        TextureRegion textureRegion = textureAtlas.findRegion("Octopus");
        Animation anim;

        Array<TextureRegion> keyFrames = new Array<TextureRegion>();
        // walking down
        for (int i = 0; i < 4; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_down", anim);

        keyFrames.clear();
        // walking up
        for (int i = 4; i < 8; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_up", anim);

        keyFrames.clear();
        // walking left
        for (int i = 8; i < 12; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_left", anim);

        keyFrames.clear();
        // walking right
        for (int i = 8; i < 12; i++) {
            TextureRegion textureRegionRight = new TextureRegion(textureRegion, i * 16, 0, 16, 24);
            textureRegionRight.flip(true, false);
            keyFrames.add(textureRegionRight);
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_right", anim);

        keyFrames.clear();
        // dying
        for (int i = 12; i < 16; i++) {
            TextureRegion textureRegionRight = new TextureRegion(textureRegion, i * 16, 0, 16, 24);
            keyFrames.add(textureRegionRight);
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("dying", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 24), 16 / GameManager.PPM, 24 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new EntityBuilder(world)
                .with(
                        new Enemy(1),
                        new Transform(x, y, 1, 1, 0),
                        new RigidBody(body),
                        new State("walking_down"),
                        renderer,
                        new Anim(anims)
                )
                .build();

        body.setUserData(e);

    }

    public void createPlayer(float x, float y, boolean restore) {
        // box2d
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearDamping = 12.0f;

        Body body = b2dWorld.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.PLAYER_BIT;
        fixtureDef.filter.maskBits = Player.defaultMaskBits;
        body.createFixture(fixtureDef);
        circleShape.dispose();

        // animation
        HashMap<String, Animation> anims = new HashMap<String, Animation>();
        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        TextureRegion textureRegion = textureAtlas.findRegion("Bomberman1");
        Animation anim;

        Array<TextureRegion> keyFrames = new Array<TextureRegion>();
        // walking up
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_up", anim);

        // walking left
        keyFrames.clear();
        for (int i = 3; i < 6; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_left", anim);

        // walking down
        keyFrames.clear();
        for (int i = 6; i < 9; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_down", anim);

        // walking right
        keyFrames.clear();
        for (int i = 9; i < 12; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_right", anim);

        // idling up
        keyFrames.clear();
        keyFrames.add(new TextureRegion(textureRegion, 1 * 16, 0, 16, 24));
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("idling_up", anim);

        // idling left
        keyFrames.clear();
        keyFrames.add(new TextureRegion(textureRegion, 3 * 16, 0, 16, 24));
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("idling_left", anim);

        // idling down
        keyFrames.clear();
        keyFrames.add(new TextureRegion(textureRegion, 7 * 16, 0, 16, 24));
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("idling_down", anim);

        // idling right
        keyFrames.clear();
        keyFrames.add(new TextureRegion(textureRegion, 9 * 16, 0, 16, 24));
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("idling_right", anim);

        // dying
        keyFrames.clear();
        for (int i = 12; i < 18; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("dying", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 24), 16 / GameManager.PPM, 24 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new com.artemis.utils.EntityBuilder(world)
                .with(
                        new Player(restore),
                        new Transform(x, y, 1, 1, 0),
                        new RigidBody(body),
                        new State("idling_down"),
                        renderer,
                        new Anim(anims)
                )
                .build();

        body.setUserData(e);
    }

    public void createBomb(Player player, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(MathUtils.floor(x) + 0.5f, MathUtils.floor(y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.45f, 0.45f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.BOMB_BIT;
        fixtureDef.filter.maskBits = Bomb.defaultMaskBits;
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
    
        public Entity createRemoteBomb(Player player, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(MathUtils.floor(x) + 0.5f, MathUtils.floor(y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.45f, 0.45f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.BOMB_BIT;
        fixtureDef.filter.maskBits = Bomb.defaultMaskBits;
        body.createFixture(fixtureDef);
        polygonShape.dispose();

        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        HashMap<String, Animation> anims = new HashMap<String, Animation>();
        TextureRegion textureRegion = textureAtlas.findRegion("Bomb");

        Animation anim;
        Array<TextureRegion> keyFrames = new Array<TextureRegion>();
        for (int i = 3; i < 5; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 16));
        }
        anim = new Animation(0.15f, keyFrames, Animation.PlayMode.LOOP_PINGPONG);
        anims.put("normal", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new EntityBuilder(world)
                .with(
                        new Bomb(player.bombPower, 16.0f),
                        new Transform(body.getPosition().x, body.getPosition().y, 1, 1, 0),
                        new RigidBody(body),
                        new State("normal"),
                        renderer,
                        new Anim(anims)
                )
                .build();

        body.setUserData(e);
        return e;
    }

    private boolean checkCanExplodeThrough(Vector2 fromV, Vector2 toV) {
        canExplodeThrough = true;
        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getFilterData().categoryBits == GameManager.INDESTRUCTIIBLE_BIT) {
                    canExplodeThrough = false;
                    return 0;
                }

                if (fixture.getFilterData().categoryBits == GameManager.BREAKABLE_BIT) {
                    canExplodeThrough = false;
                    Entity e = (Entity) fixture.getBody().getUserData();
                    Breakable breakable = e.getComponent(Breakable.class);
                    breakable.state = Breakable.State.EXPLODING;
                    return 0;
                }

                if (fixture.getFilterData().categoryBits == GameManager.EXPLOSION_BIT) {
                    canExplodeThrough = false;
                    return 0;
                }

                return 0;
            }
        };

        b2dWorld.rayCast(rayCastCallback, fromV, toV);
        return canExplodeThrough;
    }

    public void createExplosion(Bomb bomb, float x, float y) {
        x = MathUtils.floor(x) + 0.5f;
        y = MathUtils.floor(y) + 0.5f;

        TextureRegion textureRegion = assetManager.get("img/actors.pack", TextureAtlas.class).findRegion("Explosion");
        HashMap<String, Animation> anims = new HashMap<String, Animation>();

        Array<TextureRegion> keyFrames = new Array<TextureRegion>();
        Animation anim;

        // center
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        Body explosionBody = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.3f, 0.3f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
        fixtureDef.filter.maskBits = Explosion.defaultMaskBits;
        fixtureDef.isSensor = true;
        explosionBody.createFixture(fixtureDef);

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
            if (!checkCanExplodeThrough(fromV.set(x, y + i), toV.set(x, y + i + 1))) {
                break;
            }

            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y + i + 1);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = Explosion.defaultMaskBits;
            fixtureDef.isSensor = true;
            explosionBody.createFixture(fixtureDef);

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
            if (!checkCanExplodeThrough(fromV.set(x, y - i), toV.set(x, y - i - 1))) {
                break;
            }

            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y - i - 1);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = Explosion.defaultMaskBits;
            fixtureDef.isSensor = true;
            explosionBody.createFixture(fixtureDef);

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
            if (!checkCanExplodeThrough(fromV.set(x - i, y), toV.set(x - i - 1, y))) {
                break;
            }

            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x - i - 1, y);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = Explosion.defaultMaskBits;
            fixtureDef.isSensor = true;
            explosionBody.createFixture(fixtureDef);

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
            if (!checkCanExplodeThrough(fromV.set(x + i, y), toV.set(x + i + 1, y))) {
                break;
            }

            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x + i + 1, y);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = Explosion.defaultMaskBits;
            fixtureDef.isSensor = true;
            explosionBody.createFixture(fixtureDef);

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

    public void createPowerUp(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(MathUtils.floor(x) + 0.5f, MathUtils.floor(y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.4f, 0.4f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.POWERUP_BIT;
        fixtureDef.filter.maskBits = GameManager.PLAYER_BIT;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        PowerUp powerUp = new PowerUp();
        int i;
        switch (powerUp.type) {
            case REMOTE:
                i = 4;
                break;
            case KICK:
                i = 3;
                break;
            case SPEED:
                i = 2;
                break;
            case POWER:
                i = 1;
                break;
            case AMMO:
            default:
                i = 0;
                break;

        }

        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        Renderer renderer = new Renderer(new TextureRegion(textureAtlas.findRegion("Items"), i * 16, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        Entity e = new EntityBuilder(world)
                .with(
                        powerUp,
                        new RigidBody(body),
                        new Transform(body.getPosition().x, body.getPosition().y, 1, 1, 0),
                        new State("normal"),
                        renderer
                )
                .build();

        body.setUserData(e);
        polygonShape.dispose();
    }
}
