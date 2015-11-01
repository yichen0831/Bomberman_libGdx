package com.ychstudio.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;

public class Anim extends Component {
    private HashMap<String, Animation> anims;

    public Anim() {
        anims = new HashMap<String, Animation>();
    }
    
    public Anim(HashMap<String, Animation> anims) {
        this.anims = anims;
    } 
    
    public void putAnimation(String state, Animation anim) {
        anims.put(state, anim);
    }
    
    public Animation getAnimation(String state) {
        return anims.get(state);
    }
    
    public TextureRegion getTextureRegion(String state, float stateTime) {
        return anims.get(state).getKeyFrame(stateTime);
    }
    
    public TextureRegion getTextureRegion(String state, float stateTime, boolean looping) {
        return anims.get(state).getKeyFrame(stateTime, looping);
    }

}
