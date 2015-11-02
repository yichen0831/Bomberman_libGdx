package com.ychstudio.builders;

import com.badlogic.gdx.physics.box2d.World;

public class WorldBuilder {

    private final World b2dWorld;
    private final com.artemis.World world;

    public WorldBuilder(World b2dWorld, com.artemis.World world) {
        this.b2dWorld = b2dWorld;
        this.world = world;
    }

    public void build(String level) {
        if (level.equals("level_1")) {
            MapLoader mapLoader = new MapLoader(b2dWorld, world, level);
            mapLoader.loadMap();
        }
    }

}
