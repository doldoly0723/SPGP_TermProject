package kr.ac.tukorea.framework.objects;

import android.graphics.Canvas;

import java.util.ArrayList;

import kr.ac.tukorea.framework.interfaces.IGameObject;
import kr.ac.tukorea.framework.util.CollisionHelper;
import kr.ac.tukorea.hollowknight.game.scene.main.Enemy;
import kr.ac.tukorea.hollowknight.game.scene.main.Enemy2;
import kr.ac.tukorea.hollowknight.game.scene.main.MainScene;
import kr.ac.tukorea.hollowknight.game.scene.main.Player;

public class CollisionChecker implements IGameObject {
    private final MainScene scene;
    private final Player player;

    public CollisionChecker(MainScene scene, Player player) {
        this.scene = scene;
        this.player = player;
    }

    public void update(float elapsedSeconds){
        // enemy와 충돌 체크
        checkEnemy();
        checkEnemy2();

    }

    public void checkEnemy(){
        ArrayList<IGameObject> enemies = scene.objectsAt(MainScene.Layer.enemy);
        for(int i = enemies.size() - 1; i >=0;i--){
            Enemy enemy = (Enemy) enemies.get(i);
            if(CollisionHelper.collides(player, enemy)){
                if(!player.getattackOn()){
                    player.hurt(enemy);
                }
                else if(player.getattackOn()){
                    // 몬스터 hurt
                    enemy.hurt();
                }
            }
        }
    }
    public void checkEnemy2(){
        ArrayList<IGameObject> enemies2 = scene.objectsAt(MainScene.Layer.enemy2);
        for(int i = enemies2.size() - 1; i >=0;i--){
            Enemy2 enemy2 = (Enemy2) enemies2.get(i);
            if(CollisionHelper.collides(player, enemy2)){
                if(!player.getattackOn()){
                    player.hurt(enemy2);
                }
                else if(player.getattackOn()){
                    enemy2.hurt();
                }
            }
        }
    }

    public void draw(Canvas canvas) {

    }
}
