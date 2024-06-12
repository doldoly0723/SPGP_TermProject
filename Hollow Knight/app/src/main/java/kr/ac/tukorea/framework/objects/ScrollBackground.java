package kr.ac.tukorea.framework.objects;

import android.graphics.Canvas;
import android.graphics.Rect;

import kr.ac.tukorea.framework.view.Metrics;
import kr.ac.tukorea.hollowknight.game.scene.main.Player;


public class ScrollBackground extends Sprite{
    private Rect srcRect; // 확대할 영역을 정의하는 소스 직사각형
    private float moveSpeed = 4.0f;
    private final float speed;
    private float xPos = 0.0f;

    private Player player;


    public ScrollBackground(int bitmapResId, Rect zoomArea, Player player) {
        super(bitmapResId);
        this.srcRect = zoomArea; // 확대할 영역 설정
        this.player = player;
        // 확대된 이미지가 화면에 맞게 조정됩니다.
        this.width = srcRect.width() * Metrics.height / srcRect.height();
        setPosition(Metrics.width / 4, Metrics.height / 2, width, Metrics.height);
        speed = 3.0f;
    }
    public void update(float elapsedSeconds) {
        this.x  += moveSpeed * elapsedSeconds;
//        if(maxLeft){
//            srcRect.offset(-1, 0);
//        }
//        if(maxRight){
//            srcRect.offset(1, 0);
//        }
        // 화면 스크롤 테스트 해보기
        //srcRect.offset(1,0);
    }


    @Override
    public void draw(Canvas canvas) {
        // 화면 전체에 확대된 이미지를 그립니다.
        dstRect.set(0 , 0, Metrics.width, Metrics.height);
        if(player.isMaxLeft()){         // 플레이어가 왼쪽 경계선에 도달
            if(srcRect.left > 0){
                srcRect.offset(-1,0);
            }
        }else if(player.isMaxRight()){      // 플레이어가 오른쪽 경계선에 도달
            if(srcRect.right < bitmap.getWidth()){
                srcRect.offset(1,0);
            }
        }
        else if(player.isMaxTop()){
            if(srcRect.top < bitmap.getHeight()){
                srcRect.offset(0, -1);
            }

        }
        else{
            srcRect.offset(0,0);
        }
        canvas.drawBitmap(bitmap, srcRect, dstRect, null);
    }
}
