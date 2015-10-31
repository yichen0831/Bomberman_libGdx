package com.ychstudio.gamesys;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class GameManager implements Disposable {
    private static final GameManager instance = new GameManager();
    
    private AssetManager assetManager;
    
    public static final float PPM = 16.0f;
    
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
