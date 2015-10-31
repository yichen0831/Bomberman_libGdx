package com.ychstudio.builders;

import com.badlogic.gdx.physics.box2d.World;

public class WroldBuilder {
    
    private World b2dWorld;
    private com.artemis.World world;

    public WroldBuilder(World b2dWorld, com.artemis.World world) {
        this.b2dWorld = b2dWorld;
        this.world = world;
    }
    
    public void build() {
        
        
    }
    
}
