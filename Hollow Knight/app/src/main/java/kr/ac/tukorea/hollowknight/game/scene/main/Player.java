package kr.ac.tukorea.hollowknight.game.scene.main;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

import kr.ac.tukorea.framework.objects.AnimSprite;
import kr.ac.tukorea.framework.objects.SheetSprite;
import kr.ac.tukorea.framework.objects.Sprite;
import kr.ac.tukorea.framework.view.Metrics;
import kr.ac.tukorea.hollowknight.R;



public class Player extends SheetSprite {
    private int runState = 8;
    private int stayState = 1;

    private float startPosX = 9.0f;
    private float startPosY = 7.0f;

    public enum State{
        stay, running
    }
    protected static Rect[][]srcRectArray = {
            //stay
            new Rect[]{
                    new Rect(0+0*128, 0*128, 1*128, 0*128 + 1*128)
            },
            // running
            new Rect[]{
                    new Rect(0+0*128, 0*128, 1*128, 0*128 + 1*128),
                    new Rect(0+1*128, 0*128, 2*128, 0*128 + 1*128),
                    new Rect(0+2*128, 0*128, 3*128, 0*128 + 1*128),
                    new Rect(0+3*128, 0*128, 4*128, 0*128 + 1*128),
                    new Rect(0+4*128, 0*128, 5*128, 0*128 + 1*128),
                    new Rect(0+5*128, 0*128, 6*128, 0*128 + 1*128),
                    new Rect(0+6*128, 0*128, 7*128, 0*128 + 1*128),
                    new Rect(0+7*128, 0*128, 8*128, 0*128 + 1*128),
            }
    };
    protected State state = State.stay;
    public Player()  {
        super(R.mipmap.player,8);
        setPosition(startPosX,startPosY, 1.8f, 2.0f);
        srcRects = srcRectArray[state.ordinal()];
    }



    public void running(){
        if(state == State.stay){
            //setPosition();
            state = State.running;
        }
        else {
            //setPosition();
            state = State.stay;
        }
        srcRects = srcRectArray[state.ordinal()];
    }
    public boolean onTouch(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            running();
        }
        return false;
    }
}
