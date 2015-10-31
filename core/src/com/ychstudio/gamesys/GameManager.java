package com.ychstudio.gamesys;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;

public class GameManager implements Disposable {
    private static final GameManager instance = new GameManager();
    
    private AssetManager assetManager;
    
    private GameManager() {
        // load resources
        assetManager = new AssetManager();
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
