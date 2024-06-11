package kr.ac.tukorea.hollowknight.game.scene.main;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;

import kr.ac.tukorea.framework.interfaces.IBoxCollidable;
import kr.ac.tukorea.framework.interfaces.IGameObject;
import kr.ac.tukorea.framework.objects.SheetSprite;
import kr.ac.tukorea.framework.scene.Scene;
import kr.ac.tukorea.framework.view.Metrics;
import kr.ac.tukorea.hollowknight.R;


public class Enemy extends SheetSprite implements IBoxCollidable {

    private static final float MOVE_LIMIT = 5.0f;
    private float startPosX = 16.0f;
    private float startPosY = 3.0f;
    private boolean maxRight;
    private boolean maxLeft;
    private int attackframeCount;
    private Canvas canvas;
    private float moveDistance = 0.0f;

    public enum State{
        stay, move, turn, hurt, dead, falling,
    }

    private float jumpSpeed;
    private float moveSpeed = 4.0f;
    private static final float JUMP_POWER = 9.0f;
    private static final float GRAVITY = 17.0f;
    private final RectF collisionRect = new RectF();

    protected State state = State.stay;

    //private boolean reverse = false;

    protected static Rect[] makemoveRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 119;
            int t = 22 + (idx / 100) * 85;
            rects[i] = new Rect(l,t,l+116, t+85);
        }
        return rects;
    }
    protected static Rect[] maketurnRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 99;
            int t = 129;
            rects[i] = new Rect(l,t,l+96, t+83);
        }
        return rects;
    }
    protected static Rect[] makehurtRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 120;
            int t = 234;
            rects[i] = new Rect(l,t,l+117, t+121);
        }
        return rects;
    }
    protected static Rect[] makedeadRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 131;
            int t = 376;
            rects[i] = new Rect(l,t,l+128, t+90);
        }
        return rects;
    }
    protected static Rect[][]srcRectArray = {
            //stay  3 22 116 85
            makemoveRects(0), //3  122  241  360
            // move
            makemoveRects(0,1,2),
            //turn
            maketurnRects(100, 101),    // 3 129 96 83  102
            //hurt
            makehurtRects(200, 201),        // 3 234 117 121    123
            // death
            makedeadRects(300, 301),    //3 376 128 90  134
            // falling
            makehurtRects(200, 201),
    };
    protected static float[][] edgeInsetRatios = {
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.stay
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.move
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.turn
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.hurt
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.dead
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.falling
    };

    public Enemy()  {
        super(R.mipmap.crawlid,8);
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
            case stay:
                float foot = collisionRect.bottom;
                float floor = findNearestPlatformTop(foot);

                if (foot < floor) {
                    setState(Enemy.State.falling);
                    jumpSpeed = 0;
                }
                break;
            case move:
                foot = collisionRect.bottom;
                floor = findNearestPlatformTop(foot);
                float dx = moveSpeed * elapsedSeconds;
                if (foot < floor) {
                    setState(Enemy.State.falling);
                    jumpSpeed = 0;
                }
                else {
                    // 이동 로직
                    if (!reverse) {
                        x -= dx; // 왼쪽으로 1 픽셀 이동
                        dstRect.offset(-dx, 0);
                    } else {
                        x += dx; // 오른쪽으로 1 픽셀 이동
                        dstRect.offset(dx, 0);
                    }
                    moveDistance += dx; // 이동 거리 카운터 증가

                    // 이동 한계 도달 시 방향 전환
                    if (moveDistance >= MOVE_LIMIT) {
                        reverse = !reverse;  // 방향 전환
                        moveDistance = 0;  // 이동 거리 카운터 리셋
                    }
                }
                break;
            case turn:
                break;
            case hurt:
                break;
            case dead:
                break;
            case falling:
                float dy = jumpSpeed * elapsedSeconds;
                jumpSpeed += GRAVITY * elapsedSeconds;
                if(jumpSpeed >= 0){ // 낙하하고 있다면 발밑에 땅이 있는지 확인
                    foot = collisionRect.bottom;
                    floor = findNearestPlatformTop(foot);
                    if(foot + dy >= floor){
                        dy = floor - foot;
                        setState(Enemy.State.stay);
                    }
                }
                y += dy;
                dstRect.offset(0, dy);
                break;
        }
        fixCollisionRect();
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


}
