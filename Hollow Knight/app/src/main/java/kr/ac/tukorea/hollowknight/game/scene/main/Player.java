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
import kr.ac.tukorea.framework.util.CollisionHelper;
import kr.ac.tukorea.framework.view.Metrics;
import kr.ac.tukorea.hollowknight.R;



public class Player extends SheetSprite implements IBoxCollidable {

    private float startPosX = 9.0f;
    private float startPosY = 3.0f;
    private boolean maxRight;
    private boolean maxLeft;

    private boolean maxUp;
    private boolean maxDown;
    private int attackframeCount;
    private Canvas canvas;
    private boolean leftOn;
    private boolean rightOn;
    private boolean attackOn = false;

    private long hurtStartTime;  // hurt 상태에 들어간 시점
    private static final long HURT_DURATION = 3000; // hurt 상태 지속 시간 (밀리초)
    private static final long ATTACK_DURATION = 500;
    private boolean hurtOn = false;
    private long attackStartTime;
    private boolean delayOn = false;
    private boolean check = false;
    private boolean onPlatform = false;
    private Boss boss;
    private boolean godMode = false;
    private float moveDistance = 0.0f;
    private static final float MOVE_LIMIT = 8.0f;
    private boolean dashdelayOn = false;
    private long dashdelayTime;
    private long DASH_DURATION = 5000;

    public enum State{
        stay, running, jump, attack, falling, attackEffect, hurt, dead, dash,
    }

    private float jumpSpeed;
    private float moveSpeed = 4.0f;
    private static final float JUMP_POWER = 9.0f;
    private static final float GRAVITY = 17.0f;
    private final RectF collisionRect = new RectF();

    protected Enemy enemy;
    protected Enemy2 enemy2;

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
            makeRects(1213, 1214, 1215),
            // hurt
            makeRects(200,201,202,203),
            //dead
            makeRects(9),
            //dash
            makeRects(1000,1001,1002,1003,1004),
    };
    protected static float[][] edgeInsetRatios = {
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.stay
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.running
            { 0.1f, 0.2f, 0.1f, 0.0f }, // State.jump
            { -1.0f, 0.0f, -1.0f, 0.0f }, // State.attack
            { 0.1f, 0.0f, 0.1f, 0.0f }, // State.falling
            {0.0f, 0.0f, 0.0f, 0.0f},   // attack effect
            {0.0f, 0.0f, 0.0f, 0.0f},   // hurt
            {0.0f, 0.0f, 0.0f, 0.0f},   // dead
            {0.0f, 0.0f, 0.0f, 0.0f},   // dash
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
        //float top = Float.POSITIVE_INFINITY; // 문제점 : 일정 이상 내려가면 안그려짐
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
            if (top >= rect.top) {
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

    private void scrollPlayform(){
        MainScene scene = (MainScene) Scene.top();
        ArrayList<IGameObject> platforms = scene.objectsAt(MainScene.Layer.platform);
        for(IGameObject obj : platforms){
            Platform platform = (Platform) obj;
            if(maxLeft && leftOn){
                platform.scrollLeft();
            }
            else if(maxRight && rightOn){
                platform.scrollRight();
            }
            if(maxUp){
                platform.scrollUp();
            }
            else if(maxDown&& !maxLeft && !maxRight){
                platform.scrollDown();
            }
        }
    }

    private void scrollEnemy(){
        MainScene scene = (MainScene) Scene.top();
        ArrayList<IGameObject> enemies = scene.objectsAt(MainScene.Layer.enemy);
        for(IGameObject obj : enemies){
            Enemy enemy = (Enemy) obj;
            if(maxLeft && leftOn){
                enemy.scrollLeft();
            }
            else if(maxRight && rightOn){
                enemy.scrollRight();
            }
            if(maxUp)
                enemy.scrollUp();
            else if(maxDown)
                enemy.scrollDown();
        }
        ArrayList<IGameObject> enemies2 = scene.objectsAt(MainScene.Layer.enemy2);
        for(IGameObject obj : enemies2){
            Enemy2 enemy2 = (Enemy2) obj;
            if(maxLeft && leftOn){
                enemy2.scrollLeft();
            }
            else if(maxRight && rightOn){
                enemy2.scrollRight();
            }
            if(maxUp)
                enemy2.scrollUp();
            else if(maxDown)
                enemy2.scrollDown();
        }

        ArrayList<IGameObject> boss = scene.objectsAt(MainScene.Layer.boss);
        for(IGameObject obj : boss){
            Boss bos = (Boss) obj;
            if(maxLeft && leftOn){
                bos.scrollLeft();
            }
            else if(maxRight && rightOn){
                bos.scrollRight();
            }
            if(maxUp)
                bos.scrollUp();
            else if(maxDown)
                bos.scrollDown();
        }
    }


    @Override
    public void update(float elapsedSeconds) {
        switch (state){
        case jump:
        case falling:
            onPlatform = false;
            float dy = jumpSpeed * elapsedSeconds;
            jumpSpeed += GRAVITY * elapsedSeconds;
            if(jumpSpeed >= 0){ // 낙하하고 있다면 발밑에 땅이 있는지 확인
                float foot = collisionRect.bottom;
                float floor = findNearestPlatformTop(foot);         // 여기 무한대가 들어올때 안그려짐
                if(foot + dy >= floor ){
                    dy = floor - foot ;
                    if(dy < 0.2f){
                        setState(State.stay);
                        y += dy;

                        dstRect.offset(0, dy);
                        check = true;
                        onPlatform = true;
                    }

                }
            }
            if(rightOn){
                float dx = moveSpeed * elapsedSeconds;
                x += dx;
                dstRect.offset(dx, 0);
            }
            else if(leftOn){
                float dx = moveSpeed * elapsedSeconds;
                x -= dx;
                dstRect.offset(-dx,0);
            }
            // 화면 상하 스크롤 되도록
            // 만약 점프했을때 화면의 위쪽에 닿으면 maxUp을 true
            if(y -dy < 0 + 0.3) {
                maxUp= true;
                maxDown = false;
            }
            else if(y + dy + dstRect.height() > Metrics.height +0.5){
                /////////////////////////////////////////////////////// 여기가 문제인듯
                //
                dy = 0;
                maxDown = true;
                maxUp = false;
            }
            else{
                maxUp = false;
                maxDown = false;
            }
            // 만약 화면의 아래쪽에 닿으면 maxDown을 true


            if(!check){
                y += dy;
                dstRect.offset(0, dy);
            }
            else{
                check = false;
            }

            if(!onPlatform){
                scrollPlayform();
                scrollEnemy();
            }


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
            if(!onPlatform){
                scrollPlayform();
                scrollEnemy();
            }
            else if(maxRight || maxLeft){
                scrollPlayform();
                scrollEnemy();
            }
            break;

        case attack:
            attackframeCount--;
            if(attackframeCount == 0){
                setState(State.stay);
                attackOn = false;
            }

            // 공격 이펙트를 플레이어의 우측에 그리기 srcRectArray[State.attackEffect.ordinal()]

            break;
        case hurt:
            if(!hurtOn){
                hurtStartTime = System.currentTimeMillis();  // hurt 상태 시작 시간 기록
                hurtOn = true;
            }

            if (System.currentTimeMillis() - hurtStartTime >= HURT_DURATION) {
                setState(State.stay);  // 3초가 지난 후 stay 상태로 전환
                hurtOn = false;
            }

            if(rightOn){
                dx = moveSpeed * elapsedSeconds;
                x += dx;
                dstRect.offset(dx, 0);
            }
            else if(leftOn){
                dx = moveSpeed * elapsedSeconds;
                x -= dx;
                dstRect.offset(-dx,0);
            }
            ///////////////////////////////////////////////////////////
            dy = jumpSpeed * elapsedSeconds;
            jumpSpeed += GRAVITY * elapsedSeconds;
            if(jumpSpeed >= 0){ // 낙하하고 있다면 발밑에 땅이 있는지 확인
                foot = collisionRect.bottom;
                floor = findNearestPlatformTop(foot);         // 여기 무한대가 들어올때 안그려짐
                if(foot + dy >= floor ){
                    dy = floor - foot ;
                    if(dy < 0.2f){
                        //setState(State.stay);
                        y += dy;

                        dstRect.offset(0, dy);
                        check = true;
                        onPlatform = true;
                    }

                }
            }
            // 화면 상하 스크롤 되도록
            // 만약 점프했을때 화면의 위쪽에 닿으면 maxUp을 true
            if(y -dy < 0 + 0.3) {
                maxUp= true;
                maxDown = false;
            }
            else if(y + dy + dstRect.height() > Metrics.height +0.5){
                /////////////////////////////////////////////////////// 여기가 문제인듯
                //
                dy = 0;
                maxDown = true;
                maxUp = false;
            }
            else{
                maxUp = false;
                maxDown = false;
            }
            // 만약 화면의 아래쪽에 닿으면 maxDown을 true


            if(!check){
                y += dy;
                dstRect.offset(0, dy);
            }
            else{
                check = false;
            }

            if(!onPlatform){
                scrollPlayform();
                scrollEnemy();
            }


            break;
        case dash:
            float dvx = moveSpeed * elapsedSeconds * 2;
            if (reverse) {
                x -= dvx; // 왼쪽으로 1 픽셀 이동
                dstRect.offset(-dvx, 0);
            } else {
                x += dvx; // 오른쪽으로 1 픽셀 이동
                dstRect.offset(dvx, 0);
            }
            moveDistance += dvx; // 이동 거리 카운터 증가

            if (moveDistance >= MOVE_LIMIT) {
                setState(State.stay);
                godMode = false;
                moveDistance = 0;  // 이동 거리 카운터 리셋
            }

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
        if(state == State.stay || state == State.running || state == State.jump || state == State.falling){
            if(!attackOn && !delayOn){
                attackStartTime = System.currentTimeMillis();
                attackOn = true;
            }

            // 0.5초가 지났다면
            if(System.currentTimeMillis() - attackStartTime >= ATTACK_DURATION){
                delayOn = false;
            }

            if(attackOn && !delayOn){
                setState(State.attack);
                attackframeCount = srcRectArray[State.attack.ordinal()].length;

                if(reverse){
                    edgeInsetRatios[3][2] =  0.0f; // State.attack right
                    edgeInsetRatios[3][0] =  -1.0f; // State.attack left
                }
                else{
                    edgeInsetRatios[3][0] =  0.0f; // State.attack left
                    edgeInsetRatios[3][2] =  -1.0f; // State.attack right
                }
                delayOn = true;
            }

        }
    }

    public void dash(){
        if(!dashdelayOn){
            godMode = true;
            setState(State.dash);
            dashdelayTime = System.currentTimeMillis();
            dashdelayOn = true;
        }
        if(System.currentTimeMillis() - dashdelayTime >= DASH_DURATION){
            dashdelayOn = false;
        }
    }

    public boolean getgodMode(){return godMode;}

    public void rightMove(boolean startright){
        if(state == State.stay && startright) {

            setState(State.running);
        }
        if(state == State.running && !startright){
            setState(State.stay);
        }
        reverse = false;
        rightOn = startright;
    }

    public void leftMove(boolean startLeft){
        if(state == State.stay && startLeft) {

            setState(State.running);
        }
        if(state == State.running && !startLeft){
            setState(State.stay);
        }
        reverse = true;
        leftOn = startLeft;
    }


    public void hurt(Enemy enemy){
        if(state == State.hurt) return;
        setState(State.hurt);
        fixCollisionRect();
        this.enemy = enemy;
    }
    public void hurt(Enemy2 enemy2){
        if(state == State.hurt) return;
        setState(State.hurt);
        fixCollisionRect();
        this.enemy2 = enemy2;
    }
    public void hurt(Boss boss){
        if(state == State.hurt) return;
        setState(State.hurt);
        fixCollisionRect();
        this.boss = boss;
    }

    public boolean getattackOn(){
        return attackOn;
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
        if(rightOn){
            return this.maxRight;
        }
        return false;
    }
    public boolean isMaxLeft() {
        if(leftOn){
            return this.maxLeft;
        }
        return false;
    }

    public boolean isMaxTop(){
        return this.maxUp;
    }

    public boolean isMaxDown(){
        return this.maxDown;
    }

    public boolean getonPlatform(){
        return this.onPlatform;
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
