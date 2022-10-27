package com.team1091.tanks.ai

import com.team1091.tanks.Control
import com.team1091.tanks.Sensor
import com.team1091.tanks.entity.Tank

class Mary : AI {
    override fun act(sensor: Sensor, tank: Tank): Control {

        var closestTank: Tank? = null
        var closestDistance = 9999999999999.0

        for ( enemyTank in sensor.targets) {
            var distance = Math.pow(  Math.pow(enemyTank.pos.x - tank.pos.x,2.0) + Math.pow(enemyTank.pos.y - tank.pos.y,2.0) ,0.5)

            if (distance < closestDistance) {
                closestTank = enemyTank
                closestDistance = distance

            }
        }

        var slope =   (closestTank.pos.y - tank.pos.y) /  (closestTank.pos.x - tank.pos.x)
        var angleOfEnemyPos = Math.atan(slope)

        if (tank.turretFacing < angleOfEnemyPos) {

        }

        tank.turretFacing



        //put your code here
        var forward = 1.0
        forward = forward/2


        return Control(
            forward,0.0,0.0,false,false        )
    }
}