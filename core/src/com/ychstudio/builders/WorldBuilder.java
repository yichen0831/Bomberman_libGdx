package com.ychstudio.builders;

import com.artemis.Entity;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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

public class WorldBuilder {

    private final World b2dWorld;
    private final com.artemis.World world;

    private final AssetManager assetManager;

    public WorldBuilder(World b2dWorld, com.artemis.World world) {
        this.b2dWorld = b2dWorld;
        this.world = world;

        assetManager = GameManager.getInstance().getAssetManager();
    }

    private void createPlayer(float x, float y) {
        // box2d
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearDamping = 12.0f;

        Body body = b2dWorld.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.4f);
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

    public void build() {
        createPlayer(6, 6);

    }

}
