package com.ychstudio.scenes;

import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ychstudio.Bomberman;
import com.ychstudio.builders.WorldBuilder;
import com.ychstudio.gui.Hud;
import com.ychstudio.listeners.B2DWorldContactListener;
import com.ychstudio.systems.AnimationSystem;
import com.ychstudio.systems.BombSystem;
import com.ychstudio.systems.BreakableSystem;
import com.ychstudio.systems.EnemySystem;
import com.ychstudio.systems.ExplosionSystem;
import com.ychstudio.systems.PhysicsSystem;
import com.ychstudio.systems.PlayerSystem;
import com.ychstudio.systems.RenderSystem;
import com.ychstudio.systems.StateSystem;

public class PlayScreen extends ScreenAdapter {
    
    private final float WIDTH = 20;
    private final float HEIGHT = 15;

    private final Bomberman game;
    private final SpriteBatch batch;

    private OrthographicCamera camera;
    private FitViewport viewport;

    private World b2dWorld;
    private com.artemis.World world;

    private Box2DDebugRenderer b2dRenderer;
    private boolean showB2DRenderer;

    private Sprite groundSprite;

    private int mapWidth;
    private int mapHeight;
    
    private Hud hud;

    private float b2dTimer;

    public PlayScreen(Bomberman game) {
        this.game = game;
        this.batch = game.getSpriteBatch();

        showB2DRenderer = true;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        camera.position.set(WIDTH / 2, HEIGHT / 2, 0);

        b2dWorld = new World(new Vector2(), true);
        b2dWorld.setContactListener(new B2DWorldContactListener());
        b2dRenderer = new Box2DDebugRenderer();

        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
                .with(
                        new PlayerSystem(),
                        new BombSystem(),
                        new ExplosionSystem(),
                        new EnemySystem(),
                        new BreakableSystem(),
                        new PhysicsSystem(),
                        new StateSystem(),
                        new AnimationSystem(),
                        new RenderSystem(batch)
                )
                .build();

        world = new com.artemis.World(worldConfiguration);

        WorldBuilder worldBuilder = new WorldBuilder(b2dWorld, world);
        worldBuilder.build("level_1");
        groundSprite = worldBuilder.getGroundSprite();

        mapWidth = worldBuilder.getMapWidth();
        mapHeight = worldBuilder.getMapHeight();
        
        hud = new Hud(batch, WIDTH, HEIGHT);

        b2dTimer = 0;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
    
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showB2DRenderer = !showB2DRenderer;
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        b2dTimer += delta;
        if (b2dTimer > 1 / 60.0f) {
            b2dWorld.step(1 / 60.0f, 8, 3);
            b2dTimer -= 1 / 60.0f;
        }

        batch.setProjectionMatrix(camera.combined);

        // draw ground
        batch.begin();
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                groundSprite.setPosition(x, y);
                groundSprite.draw(batch);
            }
        }
        batch.end();

        world.setDelta(delta);
        world.process();

        if (showB2DRenderer) {
            b2dRenderer.render(b2dWorld, camera.combined);
        }
        
        hud.draw(delta);

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {

        b2dWorld.dispose();
        world.dispose();
        b2dRenderer.dispose();
        hud.dispose();
    }

}
