package com.ychstudio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ychstudio.gamesys.GameManager;
import com.ychstudio.scenes.MainMenuScreen;

public class Bomberman extends Game {

    private SpriteBatch batch;

    public SpriteBatch getSpriteBatch() {
        return batch;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        GameManager.getInstance().getAssetManager().dispose();
    }

}
