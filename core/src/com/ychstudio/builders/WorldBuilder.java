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

    public void build(int level) {
        MapLoader mapLoader;
        switch (level) {
            case 1:
            default:
                mapLoader = new MapLoader(b2dWorld, world, level);
                mapLoader.loadMap();
                groundSprite = mapLoader.createGroundSprite();
                mapWidth = mapLoader.getMapWidth();
                mapHeight = mapLoader.getMapHeight();
                break;
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
