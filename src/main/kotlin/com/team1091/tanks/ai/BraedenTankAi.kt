package com.team1091.tanks.ai

import com.team1091.tanks.Control
import com.team1091.tanks.Sensor
import com.team1091.tanks.Vec2
import com.team1091.tanks.entity.Tank

//Braeden
class BraedenTankAi() : AI {
    override fun act(sensor: Sensor, tank: Tank): Control {
        val closedenemy = sensor.targets.minBy { it.pos.distanceTo(tank.pos) }

        val closedpickup = sensor.pickups.minBy { it.pos.distanceTo(tank.pos) }
        val relative = (closedpickup.pos - tank.pos).rotate(-tank.facing)

        val turn = turntotank(closedpickup.pos - tank.pos, tank.facing)
        val turret = turntotank(closedenemy.pos - tank.pos, tank.facing + tank.turretFacing)
        return Control(
            forward = 5.0,
            turn = turn,
            turnTurret = turret,
            fire = closedenemy.pos.distanceTo(tank.pos) < 420694206942069,
            collect = closedpickup.pos.distanceTo(tank.pos) < 1
        )
    }

    fun turntotank(offset: Vec2, angle: Double): Double {
        return if (offset.rotate(-angle).y > 0) 1.0 else -1.0
    }
}
