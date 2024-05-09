package kr.ac.tukorea.hollowknight.app;

import android.os.Bundle;

import kr.ac.tukorea.framework.activity.GameActivity;
import kr.ac.tukorea.framework.scene.Scene;
import kr.ac.tukorea.framework.view.Metrics;
import kr.ac.tukorea.hollowknight.BuildConfig;
import kr.ac.tukorea.hollowknight.game.scene.main.MainScene;


public class HollowKnightActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Scene.drawDebugInfo = BuildConfig.DEBUG;
        Metrics.setGameSize(19,9);
        super.onCreate(savedInstanceState);
        // Scene.drawsDebugInfo 변경 시점에 주의한다.
        // new GameView() 가 호출되는 super.onCreate() 보다 이전에 해야 한다.
        new MainScene().push();
    }
}