
package com.team1091.tanks;

import com.team1091.tanks.ai.AI;
import com.team1091.tanks.entity.Tank;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

//ben
public class BestTankEver implements AI {

    @NotNull
    @Override
    public Control act(@NotNull Sensor sensor, @NotNull Tank tank) {

        boolean isCollecting = true;
        boolean isShooting = false;
        boolean isDodging;
        int range = 300;

        var tankRotation = tank.getFacing();
        var tankPosition = tank.getPos();
        var ammoAmount = tank.getAmmoCount();

        var closestPickup =
                sensor.getPickups()
                        .stream()
                        .min(Comparator.comparingDouble((it) -> it.getPos().distanceTo(tank.getPos())));
        var targetPosition = closestPickup.get().getPos();
        var relativeAmmoPosition = targetPosition.minus(tankPosition).rotate(-tankRotation);
        var turnToAmmo = (relativeAmmoPosition.getY() > 0) ? 1 : -1;
        var collect = (tankPosition.distanceTo(targetPosition) <= 2);

        var
                enemy =
                sensor.getTargets()
                        .stream()
                        .min(Comparator.comparingDouble((it) -> it.getPos().distanceTo(tank.getPos())));
        //var archNemesis = sensor.getTargets().stream().filter((tank)->tank.getAi().getClass().getName().contains("Adrian"));
        var enemyPosition = enemy.get().getPos();
        var relativeTankPosition = enemyPosition.minus(tankPosition).rotate(-(tankRotation + tank.getTurretFacing()));
        var aimAtEnemy = (relativeTankPosition.getY() > 0) ? 1 : -1;

        var relativeEnemyPosition = enemyPosition.minus(tankPosition).rotate(-tankRotation);
        var turnToEnemy = (relativeEnemyPosition.getY() > 0) ? 1 : -1;

        var bullet =
                sensor.getProjectiles()
                        .stream()
                        .min(Comparator.comparingDouble((it) -> it.getPos().distanceTo(tank.getPos())));
        var relativeEnemyPositionTwo = enemyPosition.minus(tankPosition).rotate(-tankRotation-Math.PI/2);
        var dodge = (relativeEnemyPositionTwo.getY() > 0) ? 1 : -1;

        if (ammoAmount == 0){
            isCollecting = true;
        }
        if (enemy.get().getPos().distanceTo(tank.getPos()) <= range && ammoAmount > 0) {
            isCollecting = false;
            isShooting = true;
        }else if (ammoAmount == 10) {
            isCollecting = false;
        }
        if(bullet.isPresent() && bullet.get().getPos().distanceTo(tank.getPos()) < 50){
            isDodging = true;
        }else{
            isDodging = false;
        }
        return new Control(
                1,
                isDodging ? dodge: isCollecting? turnToAmmo: turnToEnemy,
                aimAtEnemy,
                isShooting,
                isCollecting ? collect : false
        );
    }
}
