package com.ychstudio.builders;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;

public class WorldBuilder {

    private final World b2dWorld;
    private final com.artemis.World world;
    
    private Sprite groundSprite;
    
    private int mapWidth;
    private int mapHeight;

    public WorldBuilder(World b2dWorld, com.artemis.World world) {
        this.b2dWorld = b2dWorld;
        this.world = world;
    }

    public void build(String level) {
        MapLoader mapLoader;
        if (level.equals("level_1")) {
            mapLoader = new MapLoader(b2dWorld, world, level);
            mapLoader.loadMap();
            groundSprite = mapLoader.createGroundSprite();
            
            mapWidth = mapLoader.getMapWidth();
            mapHeight = mapLoader.getMapHeight();
        }
        
    }
    
    public int getMapWidth() {
        return mapWidth;
    }
    
    public int getMapHeight() {
        return mapHeight;
    }
    
    public Sprite getGroundSprite() {
        return groundSprite;
    }

}
