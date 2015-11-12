package com.ychstudio.scenes.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.HashMap;

public class AnimImage extends Image {
    
    private HashMap<String, Animation> anims;
    
    private String currentAnim;
    private float stateTime;
    
    public AnimImage(TextureRegion region) {
        super(region);
        anims = new HashMap<>();
        stateTime = 0;
        currentAnim = "";
    }
    
    public void put(String key, Animation anim) {
        anims.put(key, anim);
        setCurrentAnim(key);
    }
    
    public void setCurrentAnim(String key) {
        if (key.equals(currentAnim)) {
            return;
        }
        currentAnim = key;
        stateTime = 0;
    }
    
    public void reset() {
        stateTime = 0;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        setDrawable(new TextureRegionDrawable(anims.get(currentAnim).getKeyFrame(stateTime)));
    }
}
