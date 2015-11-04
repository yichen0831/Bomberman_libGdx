package com.ychstudio.gamesys;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class GameManager implements Disposable {
    private static final GameManager instance = new GameManager();
    
    private final AssetManager assetManager;
    
    public static final float PPM = 16.0f;
    
    public static final short NOTHING_BIT           = 0;
    public static final short INDESTRUCTIIBLE_BIT   = 1;
    public static final short BREAKABLE_BIT         = 1 << 1;
    public static final short PLAYER_BIT            = 1 << 2;
    public static final short BOMB_BIT              = 1 << 3;
    public static final short EXPLOSION_BIT         = 1 << 4;
    public static final short ENEMY_BIT             = 1 << 5;
    
    private GameManager() {
        // load resources
        assetManager = new AssetManager();
        
        assetManager.load("img/actors.pack", TextureAtlas.class);
        
        assetManager.finishLoading();
    }
    
    public static GameManager getInstance() {
        return instance;
    }
    
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
    
}
