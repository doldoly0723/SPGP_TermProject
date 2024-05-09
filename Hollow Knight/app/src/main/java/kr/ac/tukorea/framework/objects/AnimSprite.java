package kr.ac.tukorea.framework.objects;

import android.graphics.Canvas;
import android.graphics.Rect;

import kr.ac.tukorea.framework.res.BitmapPool;

public class AnimSprite extends Sprite {
    protected Rect srcRect = new Rect();
    protected float fps;
    protected int frameWidth, frameHeight;
    protected int frameCount;
    protected final long createdOn;

    public AnimSprite(int mipmapId, float fps, int count) {
        super(0);
        if (mipmapId != 0) {
            setAnimationResource(mipmapId, fps, count);
        }
        createdOn = System.currentTimeMillis();
    }
    public AnimSprite(int mipmapId, float fps){
        this(mipmapId, fps, 0);
    }
    public void setAnimationResource(int mipmapId, float fps) {
        setAnimationResource(mipmapId, fps, 0);
    }
    public void setAnimationResource(int mipmapId, float fps, int frameCount) {
        if (mipmapId != 0) {
            bitmap = BitmapPool.get(mipmapId);
        }
        this.fps = fps;
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        if (frameCount == 0) {
            this.frameWidth = imageHeight;
            this.frameHeight = imageHeight;
            this.frameCount = imageWidth / imageHeight;
        } else {
            this.frameWidth = imageWidth / frameCount;
            this.frameHeight = imageHeight;
            this.frameCount = frameCount;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // AnimSprite 는 단순반복하는 이미지이므로 time 을 update 에서 꼼꼼히 누적하지 않아도 된다.
        // draw 에서 생성시각과의 차이로 frameIndex 를 계산한다.
        long now = System.currentTimeMillis();
        float time = (now - createdOn) / 1000.0f;
        int frameIndex = Math.round(time * fps) % frameCount;
        srcRect.set(frameIndex * frameWidth, 0, (frameIndex + 1) * frameWidth, frameHeight);
        canvas.drawBitmap(bitmap, srcRect, dstRect, null);
    }
}
