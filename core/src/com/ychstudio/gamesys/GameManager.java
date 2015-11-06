package com.ychstudio.gamesys;

import com.artemis.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import java.util.LinkedList;
import java.util.Queue;

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
    public static final short POWERUP_BIT           = 1 << 6;
    public static final short PORTAL_BIT            = 1 << 7;
    
    public static boolean restorePowerUp = true;
    
    public static int playerMaxBomb = 3;
    public static int playerBombLeft = 0;
    public static float playerBombRegeratingTime = 2.0f;
    public static float playerBombRegeratingTimeLeft = 0;
    public static int playerMaxSpeed = 0;
    public static int playerBombPower = 0;
    public static boolean playerKickBomb = false;
    public static boolean playerRemoteBomb = false;
    
    public static int playerLives = 3;
    
    private Vector2 playerRespawnPosition;
    private Vector2 portalPosition;
    
    public static int enemiesLeft;
    
    private Queue<Entity> remoteBombQueue;
    
    private GameManager() {
        // load resources
        assetManager = new AssetManager();
        
        assetManager.load("img/actors.pack", TextureAtlas.class);
        
        assetManager.finishLoading();
        
        playerRespawnPosition = new Vector2();
        portalPosition = new Vector2();
        
        remoteBombQueue = new LinkedList<Entity>();
    }
    
    public static GameManager getInstance() {
        return instance;
    }
    
    public AssetManager getAssetManager() {
        return assetManager;
    }
    
    public Queue<Entity> getRemoteBombDeque() {
        return remoteBombQueue;
    }
    
    public Vector2 getPlayerRespawnPosition() {
        return playerRespawnPosition;
    }
    
    public void setPlayerRespawnPosition(Vector2 position) {
        playerRespawnPosition.set(position);
    }

    public Vector2 getPortalPosition() {
        return portalPosition;
    }

    public void setPortalPosition(Vector2 position) {
        portalPosition.set(position);
    }
    
    @Override
    public void dispose() {
        assetManager.dispose();
    }
    
}
