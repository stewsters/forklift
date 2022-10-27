package com.team1091.tanks.ai

import com.team1091.tanks.Control
import com.team1091.tanks.Sensor
import com.team1091.tanks.Vec2
import com.team1091.tanks.entity.Tank
import com.team1091.tanks.turnLeftOrRight

class EthanTankAi() : AI {
    override fun act(sensor: Sensor, tank: Tank): Control {

        val closesttank = sensor.targets.minByOrNull { it.pos.distanceTo(tank.pos) }

        val closestpickup = sensor.pickups.minByOrNull { it.pos.distanceTo(tank.pos) }

        val closestbullet = sensor.projectiles.minByOrNull { it.pos.distanceTo(tank.pos) }

        var turn = 0.0

        if (closestbullet!=null && (closestbullet.pos.distanceTo(tank.pos)) < 40){
            turn = turnLeftOrRight(tank.facing, closestbullet.facing + Math.PI/2)


        } else if (closestpickup != null && tank.ammoCount<10) {
            //val closest = (closestpickup.pos - tank.pos).rotate(-tank.facing)
            //turn = if (closest.y > 0) 1.0 else -1.0
            turn = turnto(closestpickup.pos - tank.pos, tank.facing)
        }else if(closesttank!=null){
            turn = turnto(closesttank.pos - tank.pos, tank.facing)
        }


        var turret = 0.0
        if (closesttank != null) {
            turret = turnto(closesttank.pos - tank.pos, tank.facing + tank.turretFacing)
        }
        return Control(
            forward = 1.0,
            turn = turn,
            turnTurret = turret,
            fire = (closesttank?.pos?.distanceTo(tank.pos) ?: Double.MAX_VALUE) < 300,
            collect = closestpickup?.pos?.distanceTo(tank.pos) ?: Double.MAX_VALUE < 5
        )
    }

    fun turnto(offset: Vec2, angle: Double): Double {
        return if (offset.rotate(-angle).y > 0) 1.0 else -1.0
    }

}
