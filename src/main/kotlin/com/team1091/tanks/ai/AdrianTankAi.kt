package com.team1091.tanks.ai

import com.team1091.tanks.Control
import com.team1091.tanks.PROJECTILE_MAX_FLIGHT_DIST
import com.team1091.tanks.Sensor
import com.team1091.tanks.TANK_MAX_AMMO
import com.team1091.tanks.TANK_PICKUP_RADIUS
import com.team1091.tanks.Vec2
import com.team1091.tanks.entity.Tank


class AdrianTankAi : AI {
    override fun act(sensor: Sensor, tank: Tank): Control {

        val closestEnemy = sensor.targets.minByOrNull { it.pos.distanceTo(tank.pos) }
        val closestPickup = sensor.pickups.minByOrNull { it.pos.distanceTo(tank.pos) }
        val closestProjectile = sensor.projectiles.minByOrNull { it.pos.distanceTo(tank.pos) }

        // if the closest projectile is within range, dodge
        // if the enemy is in range and we have ammo light em up
        // else gather ammo
        var turn = 0.0
        var turnTurret = 0.0

        // point turret at enemy
        if (closestEnemy != null) {
            turnTurret = driveTowards(closestEnemy.pos - tank.pos, tank.facing + tank.turretFacing)
        }

        if (closestPickup != null && tank.ammoCount < TANK_MAX_AMMO) {
            turn = driveTowards(closestPickup.pos - tank.pos, tank.facing)
        } else if (closestEnemy != null) {
            turn = driveTowards(closestEnemy.pos - tank.pos, tank.facing)
        }

        return Control(
            forward = 1.0,
            turn = turn,
            turnTurret = turnTurret,
            fire = closestEnemy != null && closestEnemy.pos.distanceTo(tank.pos) < PROJECTILE_MAX_FLIGHT_DIST,
            collect = closestPickup != null && closestPickup.pos.distanceTo(tank.pos) < TANK_PICKUP_RADIUS
        )
    }

    private fun driveTowards(offset: Vec2, facing: Double): Double {
        val prime = offset.rotate(-facing)
        return if (prime.y > 0) {
            1.0
        } else {
            -1.0
        }
    }


}
