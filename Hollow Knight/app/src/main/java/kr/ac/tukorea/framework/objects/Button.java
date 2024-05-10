package kr.ac.tukorea.framework.objects;

import android.util.Log;
import android.view.MotionEvent;

import kr.ac.tukorea.framework.interfaces.ITouchable;
import kr.ac.tukorea.framework.view.Metrics;

public class Button extends Sprite implements ITouchable {
    public interface Callback {
        public boolean onTouch();
        public boolean offTouch();
    }
    private static final String TAG = Button.class.getSimpleName();
    private final Callback callback;
    public Button(int bitmapResId, float cx, float cy, float width, float height, Callback callback) {
        super(bitmapResId, cx, cy, width, height);
        this.callback = callback;
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float[] pts = Metrics.fromScreen(e.getX(), e.getY());
        if (!dstRect.contains(pts[0], pts[1])) {
            return false;
        }
        //Log.d(TAG, "Button.onTouch(" + System.identityHashCode(this) + ", " + e.getAction() + ", " + e.getX() + ", " + e.getY());
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return callback.offTouch();  // 사용자가 버튼을 눌렀을 때
            case MotionEvent.ACTION_UP:
                return callback.onTouch();    // 사용자가 버튼에서 손을 뗐을 때
        }
        return true;
    }
}