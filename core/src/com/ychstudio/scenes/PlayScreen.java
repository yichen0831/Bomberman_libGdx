package com.ychstudio.scenes;

import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.Bomberman;
import com.ychstudio.systems.AnimationSystem;
import com.ychstudio.systems.PhysicsSystem;
import com.ychstudio.systems.RenderSystem;
import com.ychstudio.systems.StateSystem;

public class PlayScreen extends ScreenAdapter {

    private final Bomberman game;
    private final SpriteBatch batch;

    private World b2dWorld;
    private com.artemis.World world;

    private float b2dTimer;

    public PlayScreen(Bomberman game) {
        this.game = game;
        this.batch = game.getSpriteBatch();
    }

    @Override
    public void show() {
        b2dWorld = new World(new Vector2(), true);

        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
                .with(
                        new PhysicsSystem(),
                        new StateSystem(),
                        new AnimationSystem(),
                        new RenderSystem(batch)
                )
                .build();

        world = new com.artemis.World(worldConfiguration);

        b2dTimer = 0;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        b2dTimer += delta;
        if (b2dTimer > 1 / 60.0f) {
            b2dWorld.step(1 / 60.0f, 8, 3);
            b2dTimer -= 1 / 60.0f;
        }

        world.setDelta(delta);
        world.process();

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {

        b2dWorld.dispose();
        world.dispose();
    }

}
