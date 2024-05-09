package kr.ac.tukorea.hollowknight.game.scene.main;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import kr.ac.tukorea.framework.objects.AnimSprite;
import kr.ac.tukorea.framework.interfaces.IBoxCollidable;
import kr.ac.tukorea.framework.objects.SheetSprite;
import kr.ac.tukorea.framework.objects.Sprite;
import kr.ac.tukorea.framework.view.Metrics;
import kr.ac.tukorea.hollowknight.R;



public class Player extends SheetSprite implements IBoxCollidable {

    private float startPosX = 9.0f;
    private float startPosY = 7.0f;

    public enum State{
        stay, running, jump, attack
    }

    private final float ground;
    private float jumpSpeed;
    private static final float JUMP_POWER = 9.0f;
    private static final float GRAVITY = 17.0f;

    protected State state = State.stay;

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
            makeRects(400,401,402,403,404,405)
    };


    public Player()  {
        super(R.mipmap.player_sheet,8);
        setPosition(startPosX,startPosY, 1.8f, 2.0f);
        ground = y;
        srcRects = srcRectArray[state.ordinal()];
    }

    private void setState(State state){
        this.state = state;
        srcRects = srcRectArray[state.ordinal()];
    }

    @Override
    public void update(float elapsedSeconds) {
        if(state == State.jump){
            float dy = jumpSpeed * elapsedSeconds;
            jumpSpeed += GRAVITY * elapsedSeconds;
            if (y + dy >= ground) {
                dy = ground - y;
                setState(State.stay);
            }
            y += dy;
            setPosition(x, y, width, height);
        }
    }

    public void running(){
        if(state == State.stay){
            jumpSpeed = -JUMP_POWER;
            setState(State.jump);
        }
        //state = State.values()[ord]; // int 로부터 enum 만들기
        //srcRects = srcRectArray[ord];
    }
    public boolean onTouch(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            running();
        }
        return false;
    }

    public RectF getCollisionRect(){
        return dstRect;
    }

}
