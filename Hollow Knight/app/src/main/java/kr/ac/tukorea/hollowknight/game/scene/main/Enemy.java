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
    private final Player player;
    private float startPosX = 20.0f;
    private float startPosY = 5.0f;
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
        stay, move, turn, hurt, dead, falling, attack,
    }

    private float jumpSpeed;
    private float moveSpeed = 4.0f;
    private static final float JUMP_POWER = 9.0f;
    private static final float GRAVITY = 17.0f;
    private final RectF collisionRect = new RectF();

    protected State state = State.move;

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
            // attack
            makemoveRects(0,1,2),
    };
    protected static float[][] edgeInsetRatios = {
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.stay
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.move
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.turn
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.hurt
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.dead
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.falling
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.attack
    };

    public Enemy(Player player)  {
        super(R.mipmap.crawlid,8);
        this.player = player;
        reverse = false;                // 처음에는 항상 오른쪽을 보고 시작
        setPosition(startPosX,startPosY, 1.8f, 2.0f);
        srcRects = srcRectArray[state.ordinal()];
        fixCollisionRect();
    }
    private float findNearestPlatformTop(float foot) {
        MainScene scene = (MainScene) Scene.top();
        if (scene == null)
            return Metrics.height;
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
//        MainScene scene = (MainScene) Scene.top();
//        if (scene == null)
//            return Float.POSITIVE_INFINITY; // 발판이 없으면 무한대 반환
//        ArrayList<IGameObject> platforms = scene.objectsAt(MainScene.Layer.platform);
//        float top = Float.POSITIVE_INFINITY; // 초기값을 무한대로 설정
//        for (IGameObject obj : platforms) {
//            Platform platform = (Platform) obj;
//            RectF rect = platform.getCollisionRect();
//            if (rect.left > x || x > rect.right) {
//                continue;
//            }
//            if (rect.top < foot) {
//                continue;
//            }
//            if (top > rect.top) {
//                top = rect.top;
//            }
//        }
//        return top;
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

                    if(distanceX <= MOVE_LIMIT && distanceY <= MOVE_LIMIT){
                        //state Attack로 할짖 생각해보기
                        setState(State.attack);
                    }
                }
                break;
            case attack:
                if (playerPosX > x) {
                    float dvx = moveSpeed * elapsedSeconds * 0.3f;
                    x += dvx;
                    dstRect.offset(dvx,0);
                    reverse = true;
                } else {
                    float dvx = moveSpeed * elapsedSeconds * 0.3f;
                    x -= dvx;
                    dstRect.offset(-dvx,0);
                    reverse = false;
                }
                break;
            case turn:
                break;
            case hurt:
                hurtFrameCount--;
                if(hurtFrameCount == 0){
                    setState(State.stay);
                }
                break;
            case dead:
                deadFrameCount--;
                if(deadFrameCount == 0){
                    deadOn = true;
                }
                break;
            case falling:
                float dy = jumpSpeed * elapsedSeconds;
                jumpSpeed += GRAVITY * elapsedSeconds;
                if(jumpSpeed >= 0){ // 낙하하고 있다면 발밑에 땅이 있는지 확인
                    foot = collisionRect.bottom;
                    floor = findNearestPlatformTop(foot);
                    if(foot + dy >= floor){
                        dy = floor - foot;
                        setState(Enemy.State.move);
                    }
                }
                y += dy;
                dstRect.offset(0, dy);
                break;
        }
        fixCollisionRect();
    }

    public void hurt(){
        hp--;
        if(hp <= 0){
            setState(Enemy.State.dead);
            deadFrameCount =srcRectArray[Enemy.State.dead.ordinal()].length;
        }
        else{
            setState(Enemy.State.hurt);
            hurtFrameCount = srcRectArray[Enemy.State.hurt.ordinal()].length;
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
