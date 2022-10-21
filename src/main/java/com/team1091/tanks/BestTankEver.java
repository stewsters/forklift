
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
        boolean isAdvancing = false;
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
        var collect = (tankPosition.distanceTo(targetPosition) <= 1);

        var
                enemy =
                sensor.getTargets()
                        .stream()
                        .min(Comparator.comparingDouble((it) -> it.getPos().distanceTo(tank.getPos())));
        var enemyPosition = enemy.get().getPos();
        var relativeTankPosition = enemyPosition.minus(tankPosition).rotate(-(tankRotation + tank.getTurretFacing()));
        var aimAtEnemy = (relativeTankPosition.getY() > 0) ? 1 : -1;

        var relativeTurretPosition = enemyPosition.minus(tankPosition).rotate(-tankRotation);
        var turnToEnemy = (relativeTurretPosition.getY() > 0) ? 1 : -1;

        if (ammoAmount == 10) {
            if (enemy.get().getPos().distanceTo(tank.getPos()) <= range) {
                isCollecting = false;
                isShooting = true;
            }
        }
        return new Control(
                isShooting ? 0 : 1.0,
                isCollecting ? turnToAmmo : turnToEnemy,
                aimAtEnemy,
                isShooting,
                isCollecting ? collect : false
        );
    }
}
