package com.ychstudio.gui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.ychstudio.gamesys.GameManager;

public class Hud implements Disposable {

    private final SpriteBatch batch;

    private Sprite bombSprite;
    private Sprite bombTimerSprite;
    private Pixmap pixmap;
    private Texture bgTexture;
    private Texture bombTimerTexture;

    private float stateTime;
    private Sprite bigBombermanSprite;
    private Animation bigBombermanAnimation;

    public Hud(SpriteBatch batch, float width, float height) {
        this.batch = batch;

        AssetManager assetManager = GameManager.getInstance().getAssetManager();
        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        bombSprite = new Sprite(new TextureRegion(textureAtlas.findRegion("Bomb"), 0, 0, 16, 16));
        bombSprite.setBounds(15.0f, 11.5f, 1, 1);

        pixmap = new Pixmap(5, 15, Pixmap.Format.RGBA8888);
        pixmap.setColor(240.0f / 255.0f, 128 / 255.0f, 0, 1.0f);
        pixmap.fill();

        bgTexture = new Texture(pixmap);

        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        bombTimerTexture = new Texture(pixmap);

        bombTimerSprite = new Sprite(bombTimerTexture);
        bombTimerSprite.setBounds(16f, 12.5f, 3.0f, 0.2f);

        Array<TextureRegion> keyFrames = new Array<TextureRegion>();
        for (int i = 0; i < 5; i++) {
            keyFrames.add(new TextureRegion(textureAtlas.findRegion("Bomberman_big"), 32 * i, 0, 32, 48));
        }
        bigBombermanAnimation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP_PINGPONG);
        bigBombermanSprite = new Sprite(bigBombermanAnimation.getKeyFrame(0));
        bigBombermanSprite.setBounds(17.5f, 0.5f, 2f, 3f);
        stateTime = 0;
        
        pixmap.dispose();
    }

    public void draw(float delta) {
        stateTime += delta;
        bigBombermanSprite.setRegion(bigBombermanAnimation.getKeyFrame(stateTime));

        batch.begin();
        batch.draw(bgTexture, 15, 0);
        for (int i = 0; i < GameManager.playerMaxBomb; i++) {
            float alpha;
            bombSprite.setPosition(15.0f + i % 5, 11.5f - i / 5);
            alpha = i >= GameManager.playerBombLeft ? 0.5f : 1.0f;
            bombSprite.draw(batch, alpha);
        }

        bombTimerSprite.setSize((1.0f - GameManager.playerBombRegeratingTimeLeft / GameManager.playerBombRegeratingTime) * 3.0f, 0.2f);
        bombTimerSprite.draw(batch);

        bigBombermanSprite.draw(batch);

        batch.end();
    }

    @Override
    public void dispose() {
        bgTexture.dispose();
        bombTimerTexture.dispose();
    }

}
