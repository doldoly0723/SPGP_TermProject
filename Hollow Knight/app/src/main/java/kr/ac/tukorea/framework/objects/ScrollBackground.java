package kr.ac.tukorea.framework.objects;

import android.graphics.Canvas;
import android.graphics.Rect;

import kr.ac.tukorea.framework.view.Metrics;

public class ScrollBackground extends Sprite{
    private Rect srcRect; // 확대할 영역을 정의하는 소스 직사각형

    public ScrollBackground(int bitmapResId, Rect zoomArea) {
        super(bitmapResId);
        this.srcRect = zoomArea; // 확대할 영역 설정

        // 확대된 이미지가 화면에 맞게 조정됩니다.
        this.width = srcRect.width() * Metrics.height / srcRect.height();
        setPosition(Metrics.width / 2, Metrics.height / 2, width, Metrics.height);
    }
    public void update(float elapsedSeconds) {
        // 정적 배경이므로 위치 업데이트 로직이 필요 없습니다.
    }

    @Override
    public void draw(Canvas canvas) {
        // 화면 전체에 확대된 이미지를 그립니다.
        dstRect.set(0, 0, Metrics.width, Metrics.height);
        canvas.drawBitmap(bitmap, srcRect, dstRect, null);
    }
}
