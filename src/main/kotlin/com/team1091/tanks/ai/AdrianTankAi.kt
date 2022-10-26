package com.team1091.tanks.ai

import com.team1091.tanks.Control
import com.team1091.tanks.PROJECTILE_MAX_FLIGHT_DIST
import com.team1091.tanks.PROJECTILE_VELOCITY
import com.team1091.tanks.Sensor
import com.team1091.tanks.TANK_BARREL_LENGTH
import com.team1091.tanks.TANK_MAX_AMMO
import com.team1091.tanks.TANK_PICKUP_RADIUS
import com.team1091.tanks.Vec2
import com.team1091.tanks.entity.Tank
import kotlin.math.abs
import kotlin.math.sqrt


class AdrianTankAi : AI {

    val memory = mutableMapOf<Tank, Vec2>()
    override fun act(sensor: Sensor, tank: Tank): Control {

        val closestEnemy = sensor.targets.filter { it.pos.distanceTo(tank.pos) > TANK_BARREL_LENGTH }
            .minByOrNull { it.pos.distanceTo(tank.pos) }
        val closestPickup = sensor.pickups
            .filter { closestEnemy != null && it.pos.distanceTo(tank.pos) <= it.pos.distanceTo(closestEnemy.pos) }
            .minByOrNull { it.pos.distanceTo(tank.pos) }

        var turn = 0.0
        var turnTurret = 0.0
        var forward = 1.0
        // if the closest projectile is within range, dodge
        // if the enemy is in range and we have ammo light em up
        // else gather ammo

        val closestProjectile = sensor.projectiles
            .filter { it.pos.distanceTo(tank.pos) < 80 }
            .filter { (tank.pos - it.pos).rotate(-it.facing).x > 0 } // in front of us
            .minByOrNull { it.pos.distanceTo(tank.pos) }


        // point turret at enemy
        if (closestEnemy != null) {
            val rememberedPos = memory[closestEnemy]
            if (rememberedPos != null) {
                val targetVel = (closestEnemy.pos - rememberedPos) * 10.0

                calculateAimPoint(
                    xP0 = closestEnemy.pos,
                    xV0 = targetVel,
                    xP1 = tank.pos,
                    fInterceptSpeed = PROJECTILE_VELOCITY
                )?.let { turnTurret = driveTowards(it - tank.pos, tank.facing + tank.turretFacing) }
            } else
                turnTurret = driveTowards(closestEnemy.pos - tank.pos, tank.facing + tank.turretFacing)
        }

        if (closestProjectile != null) {
            turn = driveHoriz(closestProjectile.pos, tank.facing, closestProjectile.facing)
            forward = driveDodge(tank.pos, tank.facing, closestProjectile.pos, closestProjectile.facing)
        } else if (closestPickup != null && tank.ammoCount < TANK_MAX_AMMO) {
            turn = driveTowards(closestPickup.pos - tank.pos, tank.facing)
        } else if (closestEnemy != null) {
            turn = driveTowards(closestEnemy.pos - tank.pos, tank.facing)
        }


        memory.clear()
        sensor.targets.forEach { memory[it] = it.pos }

        return Control(
            forward = forward,
            turn = turn,
            turnTurret = turnTurret,
            fire = closestEnemy != null && closestEnemy.pos.distanceTo(tank.pos) < PROJECTILE_MAX_FLIGHT_DIST,
            collect = closestPickup != null && closestPickup.pos.distanceTo(tank.pos) < TANK_PICKUP_RADIUS
        )
    }

    private fun driveDodge(
        tankPos: Vec2,
        tankFacing: Double,
        projectilePos: Vec2,
        projectileFacing: Double
    ): Double {
        val offsetInTankPerspective = (projectilePos - tankPos).rotate(-tankFacing)
        return if (offsetInTankPerspective.x < 0) 1.0 else -1.0
    }

    private fun driveTowards(offset: Vec2, facing: Double): Double {
        val prime = offset.rotate(-facing)
        return if (prime.y > 0) {
            1.0
        } else {
            -1.0
        }
    }

    private fun driveHoriz(offset: Vec2, tankFacing: Double, projectileFacing: Double): Double {

        val one = turnLeftOrRight(tankFacing, projectileFacing + Math.PI / 2)
        val two = turnLeftOrRight(tankFacing, projectileFacing - Math.PI / 2)

        return if (abs(one) < abs(two)) {
            one
        } else {
            two
        }
    }

    fun turnLeftOrRight(current: Double, target: Double): Double {
        val alpha = target - current
        val beta = target - current + Math.PI * 2
        val gamma = target - current - Math.PI * 2

        val alphaAbs = abs(alpha)
        val betaAbs = abs(beta)
        val gammaAbs = abs(gamma)

        return if (alphaAbs <= betaAbs && alphaAbs <= gammaAbs) {
            alpha
        } else if (beta <= alphaAbs && beta <= gammaAbs) {
            beta
        } else {
            gamma
        }
    }

    fun calculateAimPoint(
        xP0: Vec2,
        xV0: Vec2,
        xP1: Vec2,
        fInterceptSpeed: Double
    ): Vec2? {
        val dP = xP0 - xP1;
        val a = xV0 * xV0 - (fInterceptSpeed * fInterceptSpeed)
        val b = 2.0f * (dP * xV0)
        val c = dP * dP
        var d = b * b - 4 * a * c;
        if (d < 0.0f) {
            return null
        }
        d = sqrt(d) // float) Maths ::Sqrt(d);
        var t0 = (-b - d) / (2.0f * a);
        var t1 = (-b + d) / (2.0f * a);
        var t: Double
        if (t0 > t1) {
            val c = t0
            t0 = t1;
            t1 = c;
        }
        if (t1 < 0.0f) {
            return null
        }
        if (t0 >= 0.0f) {
            t = t0;
        } else {
            t = t1;
        }
        val xAimPoint = xP0 + xV0 * t;
        return xAimPoint;
    }

}