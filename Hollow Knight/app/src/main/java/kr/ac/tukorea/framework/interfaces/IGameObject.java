package kr.ac.tukorea.framework.interfaces;

import android.graphics.Canvas;

public interface IGameObject {
    public void update(float elapsedSeconds);
    public void draw(Canvas canvas);
}
