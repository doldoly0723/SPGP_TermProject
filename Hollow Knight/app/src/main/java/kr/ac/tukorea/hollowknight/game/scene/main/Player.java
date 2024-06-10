package kr.ac.tukorea.hollowknight.game.scene.main;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import java.util.ArrayList;

import kr.ac.tukorea.framework.objects.AnimSprite;
import kr.ac.tukorea.framework.interfaces.IBoxCollidable;
import kr.ac.tukorea.framework.interfaces.IGameObject;
import kr.ac.tukorea.framework.objects.SheetSprite;
import kr.ac.tukorea.framework.objects.Sprite;
import kr.ac.tukorea.framework.scene.Scene;
import kr.ac.tukorea.framework.view.Metrics;
import kr.ac.tukorea.hollowknight.R;



public class Player extends SheetSprite implements IBoxCollidable {

    private float startPosX = 9.0f;
    private float startPosY = 3.0f;
    private boolean maxRight;
    private boolean maxLeft;
    private int attackframeCount;
    private Canvas canvas;

    public enum State{
        stay, running, jump, attack, falling, attackEffect
    }

    private float jumpSpeed;
    private float moveSpeed = 4.0f;
    private static final float JUMP_POWER = 9.0f;
    private static final float GRAVITY = 17.0f;
    private final RectF collisionRect = new RectF();

    protected State state = State.stay;

    //private boolean reverse = false;

    protected static Rect[] makeRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 0 + (idx % 100) * 128;
            int t = 0 + (idx / 100) * 128;
            rects[i] = new Rect(l,t,l+128, t+128);
        }
        return rects;
    }
    protected static Rect[][]srcRectArray = {
            //stay
            makeRects(0),
            // running
            makeRects(0,1,2,3,4,5,6,7),
            //jump
            makeRects(900,901,902,903,904,905,906,907),
            //attack
            makeRects(400,401,402,403,404,405),
            // fall
            makeRects(905,906,907),
            // attack effect
            makeRects(1213, 1214, 1215)
    };
    protected static float[][] edgeInsetRatios = {
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.stay
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.running
            { 0.1f, 0.2f, 0.1f, 0.0f }, // State.jump
            { -1.0f, 0.0f, -1.0f, 0.0f }, // State.attack
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.falling
            {0.0f, 0.0f, 0.0f, 0.0f},   // attack effect
    };

    public Player()  {
        super(R.mipmap.player_sheet,8);
        reverse = false;                // 처음에는 항상 오른쪽을 보고 시작
        setPosition(startPosX,startPosY, 1.8f, 2.0f);
        srcRects = srcRectArray[state.ordinal()];
        fixCollisionRect();
    }
    private float findNearestPlatformTop(float foot) {
        MainScene scene = (MainScene) Scene.top();
        if (scene == null) return Metrics.height;
        ArrayList<IGameObject> platforms = scene.objectsAt(MainScene.Layer.platform);
        float top = Metrics.height;
        for (IGameObject obj: platforms) {
            Platform platform = (Platform) obj;
            RectF rect = platform.getCollisionRect();
            if (rect.left > x || x > rect.right) {
                continue;
            }
            //Log.d(TAG, "foot:" + foot + " platform: " + rect);
            if (rect.top < foot) {
                continue;
            }
            if (top > rect.top) {
                top = rect.top;
            }
            //Log.d(TAG, "top=" + top + " gotcha:" + platform);
        }
        return top;
    }
    private void fixCollisionRect(){
        float[] insets = edgeInsetRatios[state.ordinal()];
        collisionRect.set(
                dstRect.left + width * insets[0],
                dstRect.top + height * insets[1],
                dstRect.right - width * insets[2],
                dstRect.bottom - height * insets[3]);
    }

    private void setState(State state){
        this.state = state;
        srcRects = srcRectArray[state.ordinal()];
    }

    @Override
    public void update(float elapsedSeconds) {
        switch (state){
        case jump:
        case falling:
            float dy = jumpSpeed * elapsedSeconds;
            jumpSpeed += GRAVITY * elapsedSeconds;
            if(jumpSpeed >= 0){ // 낙하하고 있다면 발밑에 땅이 있는지 확인
                float foot = collisionRect.bottom;
                float floor = findNearestPlatformTop(foot);
                if(foot + dy >= floor){
                    dy = floor - foot;
                    setState(State.stay);
                }
            }
            y += dy;
            dstRect.offset(0, dy);
            break;
        case stay:
            float foot = collisionRect.bottom;
            float floor = findNearestPlatformTop(foot);
            if (foot < floor) {
                setState(State.falling);
                jumpSpeed = 0;
            }
            break;
        case running:
            foot = collisionRect.bottom;
            floor = findNearestPlatformTop(foot);
            if (foot < floor) {
                setState(State.falling);
                jumpSpeed = 0;
            }
            float dx = moveSpeed * elapsedSeconds;
            if(!reverse){
                // 오른쪽으로 이동하는 경우: 플레이어가 화면의 오른쪽 끝을 넘지 않도록 조정
                if (x + dx + dstRect.width() > Metrics.width - 1) {
                    dx = 0;  // 화면 끝까지만 이동
                    maxRight = true;        // 오른쪽 끝에 도달했을때 화면 이동
                    maxLeft = false;
                }
                else {
                    maxRight = false;
                    maxLeft = false;
                }
                x += dx;
                dstRect.offset(dx, 0);
            }
            else{
                // 왼쪽으로 이동하는 경우: 플레이어가 화면의 왼쪽 끝을 넘지 않도록 조정
                if (x - dx < 0 + 2) {
                    dx = 0;  // 화면 끝까지만 이동 (0까지 이동)
                    maxLeft = true;
                    maxRight = false;
                }
                else {
                    maxRight = false;
                    maxLeft = false;
                }
                x -= dx;
                dstRect.offset(-dx, 0);

            }
            break;

        case attack:
            attackframeCount--;
            if(attackframeCount == 0){
                setState(State.stay);
            }

            // 공격 이펙트를 플레이어의 우측에 그리기 srcRectArray[State.attackEffect.ordinal()]

            break;

        }
        fixCollisionRect();
    }

    public void jump(){
        if(state == State.stay || state == State.running){
            jumpSpeed = -JUMP_POWER;
            setState(State.jump);
        }
        //state = State.values()[ord]; // int 로부터 enum 만들기
        //srcRects = srcRectArray[ord];
    }
    public void attack(){
        if(state == State.stay || state == State.running){
            setState(State.attack);
            attackframeCount = srcRectArray[State.attack.ordinal()].length;
        }
    }
    public void rightMove(boolean startright){
        if(state == State.stay && startright) {
            reverse = false;
            setState(State.running);
        }
        if(state == State.running && !startright){
            setState(State.stay);
        }
    }

    public void leftMove(boolean startLeft){
        if(state == State.stay && startLeft) {
            reverse = true;
            setState(State.running);
        }
        if(state == State.running && !startLeft){
            setState(State.stay);
        }
    }



    public void stay(){
        setState(State.stay);
    }

    public float getPosX(){
        return x;
    }

    public float getPosY(){
        return y;
    }

    public boolean isMaxRight() {
        return this.maxRight;
    }
    public boolean isMaxLeft() {
        return this.maxLeft;
    }


    public RectF getCollisionRect(){
        return collisionRect;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);  // 기본 플레이어 그리기

        if (state == State.attack && srcRectArray[State.attackEffect.ordinal()] != null) {
            int effectIndex = Math.round((float) srcRectArray[State.attackEffect.ordinal()].length * (1.0f - ((float)attackframeCount / srcRectArray[State.attack.ordinal()].length)));
            Rect effectSrcRect = srcRectArray[State.attackEffect.ordinal()][effectIndex % srcRectArray[State.attackEffect.ordinal()].length];
            RectF effectDstRect;

            if (reverse) {
                // reverse가 true인 경우, 이펙트를 플레이어의 왼쪽
                effectDstRect = new RectF(
                        dstRect.left - dstRect.width(), // 이펙트의 시작 위치를 플레이어의 왼쪽 끝으로 설정
                        dstRect.top,
                        dstRect.left, // 이펙트의 폭을 플레이어의 폭과 동일하게 설정
                        dstRect.bottom
                );

                canvas.save();
                // 좌우 반전
                canvas.scale(-1, 1, effectDstRect.centerX(), effectDstRect.centerY());
            } else {
                // reverse가 false인 경우, 이펙트를 플레이어의 오른쪽
                effectDstRect = new RectF(
                        dstRect.right, // 이펙트의 시작 위치를 플레이어의 오른쪽 끝으로 설정
                        dstRect.top,
                        dstRect.right + dstRect.width(), // 이펙트의 폭을 플레이어의 폭과 동일하게 설정
                        dstRect.bottom
                );
            }

            // 이펙트 그리기
            canvas.drawBitmap(bitmap, effectSrcRect, effectDstRect, null);
            if (reverse) {
                canvas.restore();  // 캔버스 상태 복원
            }
        }
    }

}
