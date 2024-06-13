package kr.ac.tukorea.hollowknight.game.scene.main;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.util.Log;

import kr.ac.tukorea.framework.objects.Button;
import kr.ac.tukorea.framework.objects.CollisionChecker;
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
//    private final Enemy enemy;
//    private final Enemy2 enemy2;
//    private final Boss boss;
    //private final Enemy1 enemy;

    public enum Layer{
        bg,platform, enemy, enemy2,boss, player,ui, touch, controller, COUNT,
    }
    public MainScene(){
        initLayers(Layer.COUNT);

        //add(Layer.player,new Player());
        player = new Player();
        add(Layer.player,player);

//        enemy = new Enemy(player);
//        add(Layer.enemy, enemy);
//
//        enemy2 = new Enemy2(player);
//        add(Layer.enemy2, enemy2);
//
//        boss = new Boss(player);
//        add(Layer.boss, boss);

        Rect zoomArea = new Rect(0, Max_Height/4, Max_Width/4, Max_Height); // 비트맵에서 확대할 영역 지정
        //Rect zoomArea = new Rect(0, 300, 100, 478);
        background = new ScrollBackground(R.mipmap.sameple,zoomArea, player);
        add(Layer.bg,background);
        //add(Layer.bg, new ScrollBackground(R.mipmap.sameple,zoomArea));

        // 시작 위치
        //add(Layer.platform, Platform.get(Platform.Type.T_30x5, 0, 7));

        // 위쪽
        add(Layer.platform, Platform.get(Platform.Type.T_5x2, 28, 5));
        add(Layer.platform, Platform.get(Platform.Type.T_5x2, 33, 3));
        add(Layer.platform, Platform.get(Platform.Type.T_10x2, 39, 5));
        add(Layer.platform, Platform.get(Platform.Type.T_5x2, 38, 1));
        add(Layer.platform, Platform.get(Platform.Type.T_5x2, 44, 1));
        add(Layer.platform, Platform.get(Platform.Type.T_10x2, 53, 4));

        // 가운데
        add(Layer.platform, Platform.get(Platform.Type.T_10x2, 40, 7));

        //아래쪽
        add(Layer.platform, Platform.get(Platform.Type.T_5x2, 34, 9));
        add(Layer.platform, Platform.get(Platform.Type.T_3x1, 40,11));

        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 30, 18));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 30, 16));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 30, 14));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 30, 12));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 30, 10));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 30, 8));

        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 50, 18));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 50, 16));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 50, 14));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 50, 12));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 50, 10));
        add(Layer.platform, Platform.get(Platform.Type.T_2x1, 50, 8));

        // 밑바닥
        add(Layer.platform, Platform.get(Platform.Type.T_30x5, 0, 20));
        add(Layer.platform, Platform.get(Platform.Type.T_30x5, 30, 20));
        add(Layer.platform, Platform.get(Platform.Type.T_30x5, 60, 20));


        // 보스 위치
        add(Layer.platform, Platform.get(Platform.Type.T_30x5, 50, 7));
        add(Layer.platform, Platform.get(Platform.Type.T_30x5, 80, 7));


        add(Layer.controller, new CollisionChecker(this,player));

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
