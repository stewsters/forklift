package com.team1091.tanks.ai

import com.team1091.tanks.Control
import com.team1091.tanks.Sensor
import com.team1091.tanks.TANK_MAX_AMMO
import com.team1091.tanks.entity.Tank
import com.team1091.tanks.turnLeftOrRight

class Mary : AI {
    override fun act(sensor: Sensor, tank: Tank): Control {

        var closestTank: Tank? = null
        var closestDistance = 9999999999999.0
        var closestPickup = sensor.pickups.minByOrNull { it.pos.distanceTo(tank.pos) }
        var shoot = false
        var turretTurn = 0.0;
        var angleOfEnemyPos :Double?
        if (closestTank != null) {
            angleOfEnemyPos = Math.atan2(closestTank.pos.y - tank.pos.y, closestTank.pos.x - tank.pos.x)
        } else angleOfEnemyPos = null

        for (enemyTank in sensor.targets) {
            var distance =
                Math.pow(Math.pow(enemyTank.pos.x - tank.pos.x, 2.0) + Math.pow(enemyTank.pos.y - tank.pos.y, 2.0), 0.5)

            if (distance < closestDistance) {
                closestTank = enemyTank
                closestDistance = distance
            }
        }

        var turn = 0.0


//        tank.turretFacing


        if (tank.ammoCount < TANK_MAX_AMMO && closestPickup != null) {
            var angleOfPickupPos = Math.atan2(closestPickup.pos.y - tank.pos.y, closestPickup.pos.x - tank.pos.x)
            turn = turnLeftOrRight(tank.facing, angleOfPickupPos)

        } else if (closestTank != null && angleOfEnemyPos != null) {

            turn = turnLeftOrRight(tank.turretFacing + tank.facing, angleOfEnemyPos)
        }
        if (closestTank != null && closestTank.pos.distanceTo(tank.pos) < 300) {
            shoot = true
        }


        var collect = false
        if (closestPickup != null && closestPickup.pos.distanceTo(tank.pos) <= 1.0) {
            collect = true
        }

        if (closestTank != null && angleOfEnemyPos != null) {
            turretTurn = turnLeftOrRight(tank.turretFacing + tank.facing, angleOfEnemyPos)
        }

        //put your code here
        var forward = 1.0


        return Control(
            forward, turn, turretTurn, shoot, collect
        )
    }
}