package kr.ac.tukorea.framework.objects;

import android.graphics.Canvas;
import android.graphics.Rect;

public class SheetSprite extends AnimSprite {
    protected Rect[] srcRects;
    protected boolean reverse;

    public void scrollLeft(){
        float vx = 0.1f;
        x += vx;
        dstRect.offset(vx, 0.0f);
    }
    public void scrollRight(){
        float vx = 0.1f;
        x -= vx;
        dstRect.offset(-vx, 0.0f);
    }
    public void scrollUp(){
        float vy = 0.1f;
        y -= vy;
        dstRect.offset(0.0f,vy);
    }

    public void scrollDown(){
        float vy = 0.1f;
        y += vy;
        dstRect.offset(0.0f, -vy);
    }

    public SheetSprite(int mipmapResId, float fps) {
        super(mipmapResId, fps, 1);
    }

    @Override
    public void draw(Canvas canvas) {
        long now = System.currentTimeMillis();
        float time = (now - createdOn) / 1000.0f;
        int index = Math.round(time * fps) % srcRects.length;
        if (reverse) {
            // 스프라이트를 좌우로 뒤집기 위해 캔버스 상태 저장
            canvas.save();
            // 캔버스의 스케일을 변경하여 이미지를 수평으로 반전
            // dstRect.centerX()는 뒤집기를 위한 수평 축 중심점
            canvas.scale(-1, 1, dstRect.centerX(), dstRect.centerY());
            canvas.drawBitmap(bitmap, srcRects[index], dstRect, null);
            // 캔버스 상태 복원
            canvas.restore();
        } else {
            // reverse가 false일 경우, 기본적인 방식으로 이미지를 그림
            canvas.drawBitmap(bitmap, srcRects[index], dstRect, null);
        }
    }
}