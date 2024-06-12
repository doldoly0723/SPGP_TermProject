package kr.ac.tukorea.hollowknight.game.scene.main;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.util.Log;

import kr.ac.tukorea.framework.objects.Button;
import kr.ac.tukorea.framework.objects.HorzScrollBackground;
import kr.ac.tukorea.framework.objects.ScrollBackground;
import kr.ac.tukorea.framework.interfaces.IGameObject;
import kr.ac.tukorea.framework.objects.Sprite;
import kr.ac.tukorea.hollowknight.R;
import kr.ac.tukorea.framework.objects.VertScrollBackground;
import kr.ac.tukorea.framework.scene.Scene;

public class MainScene extends Scene {
    private final int Max_Width = 850;
    private final int Max_Height = 478;
    private final Player player;

    private final ScrollBackground background;

    private static final String TAG = MainScene.class.getSimpleName();
    private final Enemy enemy;
    private final Enemy2 enemy2;
    //private final Enemy1 enemy;

    public enum Layer{
        bg,platform, player,enemy, enemy2,ui, touch, controller, COUNT,
    }
    public MainScene(){
        initLayers(Layer.COUNT);

        //add(Layer.player,new Player());
        player = new Player();
        add(Layer.player,player);

        enemy = new Enemy();
        add(Layer.enemy, enemy);

        enemy2 = new Enemy2();
        add(Layer.enemy2, enemy2);

        Rect zoomArea = new Rect(0, Max_Height/4, Max_Width/4, Max_Height); // 비트맵에서 확대할 영역 지정
        //Rect zoomArea = new Rect(0, 300, 100, 478);
        background = new ScrollBackground(R.mipmap.sameple,zoomArea, player);
        add(Layer.bg,background);
        //add(Layer.bg, new ScrollBackground(R.mipmap.sameple,zoomArea));

        add(Layer.platform, Platform.get(Platform.Type.T_5x2, 0, 7));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 8, 7));
        add(Layer.platform, Platform.get(Platform.Type.T_3x1, 10, 7));

        add(Layer.touch, new Button(R.mipmap.controller_left, 2.0f, 7.5f, 1.0f, 1.0f, new Button.Callback(){
            public boolean onTouch(Button.Action action){
                Log.d(TAG, "Button: left");
                player.leftMove(action == Button.Action.pressed);
                return true;
            }
        }));
        add(Layer.touch, new Button(R.mipmap.controller_right, 5.0f, 7.5f, 1.0f, 1.0f, new Button.Callback(){
            public boolean onTouch(Button.Action action){
                Log.d(TAG, "Button: right");
                player.rightMove(action == Button.Action.pressed);
                return true;
            }
        }));

        add(Layer.touch, new Button(R.mipmap.controller_x, 16.0f, 7.5f, 1.0f, 1.0f, new Button.Callback(){
            public boolean onTouch(Button.Action action){
                Log.d(TAG, "Button: jump");
                player.jump();
                return true;
            }
        }));

        add(Layer.touch, new Button(R.mipmap.controller_a, 18.0f, 7.5f, 1.0f, 1.0f, new Button.Callback(){
            public boolean onTouch(Button.Action action){
                player.attack();
                return true;
            }
        }));
    }

    public void update(float frameTime) {
        super.update(frameTime);
        // 플레이어가 특정 경계에 도달하면 같이 화면이 스크롤 되도록 구현
    }

    protected int getTouchLayerIndex() {
        return Layer.touch.ordinal();
    }
}
