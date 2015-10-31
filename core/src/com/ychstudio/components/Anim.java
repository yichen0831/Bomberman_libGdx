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
    
    public void putAnim(String key, Animation anim) {
        anims.put(key, anim);
    }
    
    public Animation getAnim(String key) {
        return anims.get(key);
    }
    
    public TextureRegion getTextureRegion(String key, float stateTime, boolean looping) {
        return anims.get(key).getKeyFrame(stateTime, looping);
    }

}
