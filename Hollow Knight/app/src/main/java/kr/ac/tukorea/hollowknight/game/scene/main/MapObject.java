package kr.ac.tukorea.hollowknight.game.scene.main;

import kr.ac.tukorea.framework.interfaces.IRecyclable;
import kr.ac.tukorea.framework.objects.Sprite;

public class MapObject extends Sprite implements IRecyclable {
    public MapObject() {
        super(0);
    }

    @Override
    public void onRecycle() {

    }
}
