package kr.ac.tukorea.hollowknight.game.scene.main;

import android.graphics.Canvas;
import android.graphics.Rect;

import kr.ac.tukorea.framework.objects.Sprite;
import kr.ac.tukorea.framework.view.Metrics;
import kr.ac.tukorea.hollowknight.R;

public class Player extends Sprite {
    private Rect srcRect;
    private Rect dstRect;
    public Player(){
        super(R.mipmap.player);
        srcRect = new Rect(0, 0, 128, 128); // Source rectangle for the sprite

        float desiredWidth = 2.0f;
        float desiredHeight = 2.0f;
        float posX = 8.0f ;
        float posY = 6.0f ;

        dstRect = new Rect((int) posX, (int) posY, (int) (posX + desiredWidth), (int) (posY + desiredHeight));
    }
    public void draw(Canvas canvas) {
        if (bitmap != null && !bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, srcRect, dstRect, null);
        }
    }

}
