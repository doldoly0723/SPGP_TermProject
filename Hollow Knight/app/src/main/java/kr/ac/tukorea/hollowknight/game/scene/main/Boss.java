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


public class Boss extends SheetSprite implements IBoxCollidable {

    private static final float MOVE_LIMIT = 5.0f;
    private final Player player;
    private float startPosX = 15.0f;
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
        stay, move, rollStart,roll ,attackStart,attack,attack2Start,attack2,attackRecover,  hurt, dead, falling,
    }

    private float jumpSpeed;
    private float moveSpeed = 4.0f;
    private static final float JUMP_POWER = 9.0f;
    private static final float GRAVITY = 17.0f;
    private final RectF collisionRect = new RectF();

    protected State state = State.hurt;

    //private boolean reverse = false;

    protected static Rect[] makeidleRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 300;
            int t = 424;
            rects[i] = new Rect(l,t,l+297, t+347);
        }
        return rects;
    }
    protected static Rect[] makemoveRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 125;
            int t = 793;
            rects[i] = new Rect(l,t,l+422, t+356);
        }
        return rects;
    }

    protected static Rect[] makerollStartRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 443;
            int t = 1535;
            rects[i] = new Rect(l,t,l+440, t+380);
        }
        return rects;
    }

    protected static Rect[] makerollRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 393;
            int t = 1937;
            rects[i] = new Rect(l,t,l+390, t+353);
        }
        return rects;
    }
    protected static Rect[] makeattackStartRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 483;
            int t = 2498;
            rects[i] = new Rect(l,t,l+338, t+486);
        }
        return rects;
    }
    protected static Rect[] makeattackRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100);
            int t = 3199;
            rects[i] = new Rect(l,t,l+594, t+298);
        }
        return rects;
    }
    protected static Rect[] makeattack2StartRects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 539;
            int t = 3518;
            rects[i] = new Rect(l,t,l+535, t+358);
        }
        return rects;
    }
    protected static Rect[] makeattack2Rects(int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = 3 + (idx % 100) * 539;
            int t = 3518;
            rects[i] = new Rect(l,t,l+535, t+358);
        }
        return rects;
    }

    protected static Rect[] makeRects(int a, int b, int c, int d,int e, int... indices){
        Rect[] rects = new Rect[indices.length];
        for(int i = 0; i < indices.length; i++){
            int idx = indices[i];
            int l = a + (idx % 100) * (e-a);
            int t = b;
            rects[i] = new Rect(l,t,l+c, t+d);
        }
        return rects;
    }

    protected static Rect[][]srcRectArray = {
            //stay  3 22 116 85
            makeidleRects(100, 101, 102, 103, 104, 105), //3 424 297 347 303
            makemoveRects(200,201,202,203,204,205),            // 3 793 422 356 428
            makerollStartRects(400,401,402,403,404),       // 3 1535 440 380 446
            makerollRects(500,501,502),            // 3 1937 390 353 396
            makeattackStartRects(700,701,702,703,704),             // 3 2498 480 338 486
            makeattackRects(900),      // 3 3199 594 298
            makeattack2StartRects(1000,1001,1002,1003),        // 3 3518 535 358 542
            makeattack2Rects(1100),     //3 3897 459 386
            makeRects(497,3897,491,381,991,1100,1101,1102),     //497 3897 491 381 991   attackRecover
            makeRects(3, 4305, 435, 368, 441, 1200,1201,1202),    //3 4305 435 368 441 hurt
            makeRects(3, 4695,361,343,367,1300,1301,1302),        // dead
            makeRects(3, 4305, 435, 368, 441, 1200,1201,1202),      //falling
    };


    protected static float[][] edgeInsetRatios = {
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.stay
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.move
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.rollStart
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.roll
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.attackStart
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.attack
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.attack2Start
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.attack2
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.attackRecover
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.hurt
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.dead
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.falling
    };

    public Boss(Player player)  {
        super(R.mipmap.boss,8);
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
        //stay, move, rollStart,roll ,attackStart,attack,attack2Start,attack2,attackRecover,  hurt, dead, falling,
        switch (state){
            case stay:
                break;
            case move:
                break;
            case rollStart:
                break;
            case roll:
                break;
            case attackStart:
                break;
            case attack:
                break;
            case attack2Start:
                break;
            case attack2:
                break;
            case attackRecover:
                break;
            case hurt:
                break;
            case dead:
                break;
            case falling:
                break;
        }
        fixCollisionRect();
    }

    public void hurt(){
        hp--;
        if(hp <= 0){
            setState(Boss.State.dead);
            deadFrameCount =srcRectArray[Boss.State.dead.ordinal()].length;
        }
        else{
            setState(Boss.State.hurt);
            hurtFrameCount = srcRectArray[Boss.State.hurt.ordinal()].length;
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
