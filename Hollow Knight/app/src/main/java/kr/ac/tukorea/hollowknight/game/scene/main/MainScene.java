package kr.ac.tukorea.hollowknight.game.scene.main;

import android.graphics.Rect;
import android.view.MotionEvent;

import kr.ac.tukorea.framework.objects.HorzScrollBackground;
import kr.ac.tukorea.framework.objects.ScrollBackground;
import kr.ac.tukorea.hollowknight.R;
import kr.ac.tukorea.framework.objects.VertScrollBackground;
import kr.ac.tukorea.framework.scene.Scene;

public class MainScene extends Scene {
    private final int Max_Width = 850;
    private final int Max_Height = 478;
    private final Player player;


    public enum Layer{
        bg,platform, player,COUNT
    }
    public MainScene(){
        initLayers(Layer.COUNT);
        Rect zoomArea = new Rect(0, Max_Height/4, Max_Width/4, Max_Height); // 비트맵에서 확대할 영역 지정
        //Rect zoomArea = new Rect(0, 300, 100, 478);
        add(Layer.bg, new ScrollBackground(R.mipmap.sameple,zoomArea));

        //add(Layer.player,new Player());
        player = new Player();
        add(Layer.player,player);

        add(Layer.platform, Platform.get(Platform.Type.T_5x2, 0, 7));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 10, 7));
        add(Layer.platform, Platform.get(Platform.Type.T_3x1, 12, 7));

    }
    public boolean onTouch(MotionEvent event){
        return player.onTouch(event);
    }
}
