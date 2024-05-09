package kr.ac.tukorea.hollowknight.game.scene.main;

import kr.ac.tukorea.framework.objects.Sprite;
import kr.ac.tukorea.hollowknight.R;

public class Platform extends Sprite {
    public Platform(){
        super(R.mipmap.platform_341x68);
        setPosition(6.0f,5.0f,10.0f,2.0f);
    }
}
