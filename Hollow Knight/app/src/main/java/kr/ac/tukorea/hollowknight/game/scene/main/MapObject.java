package kr.ac.tukorea.hollowknight.game.scene.main;

import android.util.Log;

import kr.ac.tukorea.framework.interfaces.IRecyclable;
import kr.ac.tukorea.framework.objects.Sprite;
import kr.ac.tukorea.framework.scene.Scene;

public abstract class MapObject extends Sprite implements IRecyclable {
    public MapObject() {
        super(0);
    }
    private static final String TAG = MapObject.class.getSimpleName();

    @Override
    public void update(float elapsedSeconds) {
        // 플레이어 이동에 따른 맵 오브젝트 위치 변화
    }

    abstract protected MainScene.Layer getLayer();
    public void addToScene() {
        Scene scene = Scene.top();
        if (scene == null) {
            Log.e(TAG, "Scene stack is empty in addToScene() " + this.getClass().getSimpleName());
            return;
        }
        scene.add(getLayer(), this);
    }
    public void removeFromScene() {
        Scene scene = Scene.top();
        if (scene == null) {
            Log.e(TAG, "Scene stack is empty in removeFromScene() " + this.getClass().getSimpleName());
            return;
        }
        scene.remove(getLayer(), this);
    }
    @Override
    public void onRecycle() {

    }
}
