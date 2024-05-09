package kr.ac.tukorea.hollowknight.game.scene.main;

import android.graphics.Bitmap;

import kr.ac.tukorea.framework.objects.Sprite;
import kr.ac.tukorea.framework.res.BitmapPool;
import kr.ac.tukorea.framework.scene.RecycleBin;
import kr.ac.tukorea.hollowknight.R;

public class Platform extends MapObject {
    public enum Type{
        T_2x1, T_3x1, T_5x2;
        int resId() {return resIds[this.ordinal()];}
        int width() {return widths[this.ordinal()];}
        int height() {return heights[this.ordinal()];}
        static final int[] resIds = {
                R.mipmap.platform_93x59,
                R.mipmap.platform_120x66,
                R.mipmap.platform_341x68,
        };
        static int[] widths = {2,3,5};
        static int[] heights = {1,1,2};
    }

    protected MainScene.Layer getLayer() {
        return MainScene.Layer.platform;
    }



    private Platform(){}
    public static Platform get(Type type, float left, float top){
        Platform platform = (Platform) RecycleBin.get(Platform.class);
        if (platform == null) {
            platform = new Platform();
        }
        platform.init(type, left, top);
        return platform;
    }

    private void init(Type type, float left, float top) {

        bitmap = BitmapPool.get(type.resId());
        width = type.width();
        height = type.height();
        dstRect.set(left,top,left+width,top+height);
    }
}
