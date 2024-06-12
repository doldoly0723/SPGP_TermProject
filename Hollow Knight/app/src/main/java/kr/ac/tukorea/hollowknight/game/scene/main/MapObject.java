package kr.ac.tukorea.hollowknight.game.scene.main;

import android.graphics.RectF;
import android.util.Log;

import kr.ac.tukorea.framework.interfaces.IRecyclable;
import kr.ac.tukorea.framework.objects.Sprite;
import kr.ac.tukorea.framework.scene.Scene;
import kr.ac.tukorea.framework.interfaces.IBoxCollidable;

public abstract class MapObject extends Sprite implements IBoxCollidable, IRecyclable {
    public MapObject() {
        super(0);
    }
    private static final String TAG = MapObject.class.getSimpleName();

    private float elapsedTime;
    @Override
    public void update(float elapsedSeconds) {
        // 플레이어 이동에 따른 맵 오브젝트 위치 변화
        elapsedTime = elapsedSeconds;
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
    public RectF getCollisionRect(){
        return dstRect;
    }
    @Override
    public void onRecycle() {

    }

    public void scrollLeft(){
        dstRect.offset(elapsedTime * scrollSpeed, 0.0f);
    }
    public void scrollRight(){
        dstRect.offset(-elapsedTime * scrollSpeed, 0.0f);
    }
}
