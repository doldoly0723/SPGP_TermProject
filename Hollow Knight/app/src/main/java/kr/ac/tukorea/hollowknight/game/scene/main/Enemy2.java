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


public class Enemy2 extends SheetSprite implements IBoxCollidable {

    private static final float MOVE_LIMIT = 5.0f;
    private final Player player;
    private float startPosX = 13.0f;
    private float startPosY = 3.0f;
    private boolean maxRight;
    private boolean maxLeft;
    private int attackframeCount;
    private Canvas canvas;
    private float moveDistance = 0.0f;
    private int hurtFrameCount;

    private int hp = 10;
    private int deadFrameCount;
    private boolean deadOn = false;

    public enum State{
        stay, move, turn, hurt, dead,
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
            int l = 3 + (idx % 100) * 86;
            int t = 22;
            rects[i] = new Rect(l,t,l+83, t+90);
        }
        return rects;
    }

    protected static Rect[] makehurtRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 86;
            int t = 134;
            rects[i] = new Rect(l,t,l+83, t+90);
        }
        return rects;
    }
    protected static Rect[] makedeadRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 142;
            int t = 246;
            rects[i] = new Rect(l,t,l+139, t+161);
        }
        return rects;
    }
    protected static Rect[][]srcRectArray = {
            //stay  3 22 116 85
            makemoveRects(0,1,2,3), //3 22 83 90   89
            // move
            makemoveRects(0,1,2,3),
            //turn
            makehurtRects(100, 101),
            //hurt
            makehurtRects(100, 101),
            // death
            makedeadRects(200, 201, 202, 203, 204),     //3 246 139 161 145
    };
    protected static float[][] edgeInsetRatios = {
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.stay
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.move
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.turn
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.hurt
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.dead
    };

    public Enemy2(Player player, float startPosX, float startPosY)  {
        super(R.mipmap.aspid,8);
        this.player = player;
        this.startPosX = startPosX;
        this.startPosY = startPosY;
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
        float playerPosX = player.getPosX();  // 플레이어의 x 위치를 가져옵니다.
        float playerPosY = player.getPosY();  // 플레이어의 y 위치를 가져옵니다.
        float distanceX = Math.abs(playerPosX - x);  // 플레이어와의 X축 거리 계산
        float distanceY = Math.abs(playerPosY - y);  // 플레이어와의 Y축 거리 계산
        switch (state){
            case stay:
                // 플레이어가 X축과 Y축에서 5 픽셀 이내에 있으면 move 상태로 변경
                if (distanceX <= MOVE_LIMIT && distanceY <= MOVE_LIMIT) {
                    setState(State.move);
                }
                break;

            case move:
                // 플레이어의 위치에 따라 이동
                if (playerPosX > x) {
                    float dx = moveSpeed * elapsedSeconds * 0.3f;
                    x += dx;
                    dstRect.offset(dx,0);
                    reverse = true;
                } else {
                    float dx = moveSpeed * elapsedSeconds * 0.3f;
                    x -= dx;
                    dstRect.offset(-dx,0);
                    reverse = false;
                }

                // 플레이어가 위에 있으면 위로 이동, 아래에 있으면 아래로 이동
                if (playerPosY > y) {
                    float dy = moveSpeed * elapsedSeconds * 0.3f;
                    y += dy;
                    dstRect.offset(0, dy);
                } else {
                    float dy = moveSpeed * elapsedSeconds * 0.3f;
                    y -= dy;
                    dstRect.offset(0, -dy);
                }
            case turn:
                break;
            case hurt:
                hurtFrameCount--;
                if(hurtFrameCount == 0){
                    setState(State.stay);
                }
                break;
            case dead:
                fps=1;
                deadFrameCount--;
                if(deadFrameCount == 0){
                    deadOn = true;
                }
                break;
        }
        fixCollisionRect();
    }


    public void hurt(){
        hp--;
        if(hp <= 0){
            setState(State.dead);
            deadFrameCount =srcRectArray[State.dead.ordinal()].length;
        }
        else{
            setState(State.hurt);
            hurtFrameCount = srcRectArray[Enemy2.State.hurt.ordinal()].length;
        }
    }
    public boolean getdeadOn(){
        return deadOn;
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
