package com.ychstudio.builders;

import com.artemis.Entity;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.components.Anim;
import com.ychstudio.components.Player;
import com.ychstudio.components.Renderer;
import com.ychstudio.components.RigidBody;
import com.ychstudio.components.State;
import com.ychstudio.components.Transform;
import com.ychstudio.gamesys.GameManager;
import java.util.HashMap;

public class MapLoader {

    public static enum BLOCK {

        EMPTY(255, 255, 255), // white
        WALL(0, 0, 0), // black
        BREAKABLE(0, 255, 255), // cyan
        INDESTRUCTIBLE(0, 0, 255), // blue
        PLAYER(255, 0, 0), // red
        ENEMY1(0, 255, 0); // green

        int color;

        BLOCK(int r, int g, int b) {
            color = r << 24 | g << 16 | b << 8 | 0xff;
        }

        boolean sameColor(int color) {
            return this.color == color;
        }
    }

    protected final World b2dWorld;
    protected final com.artemis.World world;
    protected final AssetManager assetManager;

    protected TextureAtlas tileTextureAtlas;
    protected Pixmap pixmap;

    protected int mapWidth;
    protected int mapHeight;

    protected String level;

    protected final float radius = 0.46f;

    public MapLoader(World b2dWorld, com.artemis.World world, String level) {
        this.b2dWorld = b2dWorld;
        this.world = world;
        this.level = level;
        assetManager = GameManager.getInstance().getAssetManager();

        assetManager.load("maps/" + level + ".png", Pixmap.class);
        assetManager.load("maps/" + level + "_tiles.pack", TextureAtlas.class);
        assetManager.finishLoading();

        pixmap = assetManager.get("maps/" + level + ".png", Pixmap.class);
        tileTextureAtlas = assetManager.get("maps/" + level + "_tiles.pack", TextureAtlas.class);

        mapWidth = pixmap.getWidth();
        mapHeight = pixmap.getHeight();
    }

    public void loadMap() {

        int color;
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                color = pixmap.getPixel(x, mapHeight - y - 1);
                if (BLOCK.WALL.sameColor(color)) {
                    createWall(x + 0.5f, y + 0.5f);
                } else if (BLOCK.BREAKABLE.sameColor(color)) {
                    createBreakable(x + 0.5f, y + 0.5f);
                } else if (BLOCK.INDESTRUCTIBLE.sameColor(color)) {
                    createIndestructible(x + 0.5f, y + 0.5f);
                } else if (BLOCK.PLAYER.sameColor(color)) {
                    createPlayer(x + 0.5f, y + 0.5f);
                } else if (BLOCK.ENEMY1.sameColor(color)) {
                    createEnemy1(x + 0.5f, y + 0.5f);
                }
            }
        }

    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    protected Sprite createGroundSprite() {
        TextureRegion textureRegion = tileTextureAtlas.findRegion("ground");

        Sprite sprite = new Sprite();
        sprite.setRegion(textureRegion);
        sprite.setBounds(0, 0, 1, 1);

        return sprite;
    }

    protected void createWall(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        body.createFixture(polygonShape, 1);

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
        Entity e = new EntityBuilder(world)
                .with(
                        new Transform(x, y, 1f, 1f, 0),
                        renderer
                )
                .build();

        body.setUserData(e);
    }

    protected void createIndestructible(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        body.createFixture(polygonShape, 1);

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

    protected void createBreakable(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        body.createFixture(polygonShape, 1);

        polygonShape.dispose();
    }

    protected void createEnemy1(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        body.createFixture(fixtureDef);

        circleShape.dispose();
    }

    protected void createPlayer(float x, float y) {
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

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 24), 16 / GameManager.PPM, 24 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new EntityBuilder(world)
                .with(
                        new Player(),
                        new Transform(x, y, 1, 1, 0),
                        new RigidBody(body),
                        new State("IDLING_DOWN"),
                        renderer,
                        new Anim(anims)
                )
                .build();

        body.setUserData(e);

    }
}
