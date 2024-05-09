package kr.ac.tukorea.hollowknight.game.scene.main;

import android.graphics.Canvas;
import android.graphics.Rect;

import kr.ac.tukorea.framework.objects.AnimSprite;
import kr.ac.tukorea.framework.objects.Sprite;
import kr.ac.tukorea.framework.view.Metrics;
import kr.ac.tukorea.hollowknight.R;

public class Player extends AnimSprite {
    public Player()  {
        super(R.mipmap.player,8,1);
        setPosition(9.0f,7.0f, 1.8f, 2.0f);
    }

}
