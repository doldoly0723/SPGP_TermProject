package kr.ac.tukorea.framework.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import kr.ac.tukorea.framework.interfaces.IGameObject;
import kr.ac.tukorea.framework.res.BitmapPool;
import kr.ac.tukorea.framework.view.Metrics;

public class JoyStick implements IGameObject {
    private static final String TAG = JoyStick.class.getSimpleName();
    private final Bitmap bgBitmap;
    private final Bitmap thumbBitmap;

    private float centerX = 2.0f;
    private float centerY = 14.0f;
    private float bgRadius = 2.0f;
    private float thumbRadius = 0.57f;
    private float thumbMoveRadius = 1.7f;

    private final RectF bgRect = new RectF();
    private final RectF thumbRect = new RectF();

    private boolean visible;
    private float startX, startY;
    public float power, angle_radian;

    public JoyStick(int bgBmpId, int thumbBmpId) {
        bgBitmap = BitmapPool.get(bgBmpId);
        thumbBitmap = BitmapPool.get(thumbBmpId);

        setRects(centerX, centerY, bgRadius, thumbRadius, thumbMoveRadius);
    }

    public void setRects(float cx, float cy, float bgRadius, float thumbRadius, float moveRadius) {
        this.centerX = cx;
        this.centerY = cy;
        this.bgRadius = bgRadius;
        this.thumbRadius = thumbRadius;
        this.thumbMoveRadius = moveRadius;
        bgRect.set(centerX - this.bgRadius, centerY - this.bgRadius, centerX + this.bgRadius, centerY + this.bgRadius);
        thumbRect.set(centerX - this.thumbRadius, centerY - this.thumbRadius, centerX + this.thumbRadius, centerY + this.thumbRadius);
    }

    @Override
    public void update(float elapsedSeconds) {

    }

    @Override
    public void draw(Canvas canvas) {
        if (!visible) return;
        canvas.drawBitmap(bgBitmap, null, bgRect, null);
        canvas.drawBitmap(thumbBitmap, null, thumbRect, null);
    }

    public boolean onTouch(MotionEvent event) {
        float[] pts;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                visible = true;
                pts = Metrics.fromScreen(event.getX(), event.getY());
                startX = pts[0];
                startY = pts[1];
                return true;
            case MotionEvent.ACTION_MOVE:
                pts = Metrics.fromScreen(event.getX(), event.getY());
                float dx = Math.max(-bgRadius, Math.min(pts[0] - startX, bgRadius));
                float dy = Math.max(-bgRadius, Math.min(pts[1] - startY, bgRadius));
                double radius = Math.sqrt(dx * dx + dy * dy);
                angle_radian = (float) Math.atan2(dy, dx);
                if (radius > thumbMoveRadius) {
                    dx = (float) (thumbMoveRadius * Math.cos(angle_radian));
                    dy = (float) (thumbMoveRadius * Math.sin(angle_radian));
                    radius = thumbMoveRadius;
                }
                power = (float) (radius / thumbMoveRadius);

                float cx = centerX + dx, cy = centerY + dy;
                Log.d(TAG, "sx="+startX+" sy="+startY+" dx="+dx + " dy="+dy);
                thumbRect.set(cx - thumbRadius, cy - thumbRadius, cx + thumbRadius, cy + thumbRadius);
                break;

            case MotionEvent.ACTION_UP:
                visible = false;
                power = 0;
                return true;
        }
        return false;
    }
}
