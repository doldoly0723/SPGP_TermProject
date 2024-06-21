package kr.ac.tukorea.framework.objects;

import android.graphics.Canvas;

import java.util.ArrayList;

import kr.ac.tukorea.framework.interfaces.IGameObject;
import kr.ac.tukorea.framework.util.CollisionHelper;
import kr.ac.tukorea.hollowknight.game.scene.main.Boss;
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
        checkBoss();
    }

    public void checkEnemy(){
        ArrayList<IGameObject> enemies = scene.objectsAt(MainScene.Layer.enemy);
        for(int i = enemies.size() - 1; i >=0;i--){
            Enemy enemy = (Enemy) enemies.get(i);
            if(CollisionHelper.collides(player, enemy)){
                if(!player.getattackOn() && !player.getgodMode()){
                    player.hurt(enemy);
                }
                else if(player.getattackOn()){
                    // 몬스터 hurt
                    enemy.hurt();
                }
            }

            if(enemy.getdeadOn()){
                scene.remove(MainScene.Layer.enemy, enemy);
            }
        }
    }
    public void checkEnemy2(){
        ArrayList<IGameObject> enemies2 = scene.objectsAt(MainScene.Layer.enemy2);
        for(int i = enemies2.size() - 1; i >=0;i--){
            Enemy2 enemy2 = (Enemy2) enemies2.get(i);
            if(CollisionHelper.collides(player, enemy2)){
                if(!player.getattackOn()&& !player.getgodMode()){
                    player.hurt(enemy2);
                }
                else if(player.getattackOn()){
                    enemy2.hurt();
                }
            }
            if(enemy2.getdeadOn()){
                scene.remove(MainScene.Layer.enemy2, enemy2);
            }
        }
    }

    public void checkBoss(){
        ArrayList<IGameObject> boss = scene.objectsAt(MainScene.Layer.boss);
        for(int i = boss.size() - 1; i >=0;i--){
            Boss bos = (Boss) boss.get(i);
            if(CollisionHelper.collides(player, bos)){
                if(!player.getattackOn()&& !player.getgodMode()){
                    player.hurt(bos);
                }
                else if(player.getattackOn()){
                    bos.hurt();
                }
            }
            if(bos.getdeadOn()){
                scene.remove(MainScene.Layer.boss, bos);
            }
        }
    }

    public void draw(Canvas canvas) {

    }
}
