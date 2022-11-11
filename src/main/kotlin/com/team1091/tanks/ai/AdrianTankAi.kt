package com.team1091.tanks.ai

import com.team1091.tanks.Control
import com.team1091.tanks.FRAMES_PER_SECOND
import com.team1091.tanks.Line
import com.team1091.tanks.PROJECTILE_MAX_FLIGHT_DIST
import com.team1091.tanks.PROJECTILE_VELOCITY
import com.team1091.tanks.SECONDS_PER_FRAME
import com.team1091.tanks.Sensor
import com.team1091.tanks.TANK_BARREL_LENGTH
import com.team1091.tanks.TANK_MAX_AMMO
import com.team1091.tanks.TANK_PICKUP_RADIUS
import com.team1091.tanks.TANK_TURN_RATE
import com.team1091.tanks.Vec2
import com.team1091.tanks.calculateAimPoint
import com.team1091.tanks.distanceToLine
import com.team1091.tanks.entity.Tank
import com.team1091.tanks.facingDist
import com.team1091.tanks.intersection
import com.team1091.tanks.turnLeftOrRight
import kotlin.math.abs


class AdrianTankAi : AI {

    private val memory = mutableMapOf<Tank, Vec2>()

    override fun act(sensor: Sensor, tank: Tank): Control {

        val closestEnemy = sensor.targets
            .filter { it.pos.distanceTo(tank.pos) > TANK_BARREL_LENGTH }
            .minByOrNull { it.pos.distanceTo(tank.pos) }
        val closestPickup = sensor.pickups
            .filter { closestEnemy != null && it.pos.distanceTo(tank.pos) <= it.pos.distanceTo(closestEnemy.pos) }
            .minByOrNull { it.pos.distanceTo(tank.pos) }

        val closestProjectile = sensor.projectiles
            .filter { it.pos.distanceTo(tank.pos) < MAX_PROJECTILE_DIST }
            .filter { (tank.pos - it.pos).rotate(-it.facing).x > 0 } // in front of us
            .filter {
                distanceToLine(
                    tank.pos,
                    it.pos,
                    it.pos + facingDist(it.facing, MAX_PROJECTILE_DODGE_DIST)
                ) < MAX_PROJECTILE_DIST
            } // in front of us
            .minByOrNull { it.pos.distanceTo(tank.pos) }

        var turn = 0.0
        var turnTurret = 0.0
        var forward = 1.0


        // Control driving
        if (closestProjectile != null) {
            // if the closest projectile is within range, dodge
            turn = turnHorizontal(tank.facing, closestProjectile.facing)
            forward = driveDodge(tank.pos, tank.facing, closestProjectile.pos, closestProjectile.facing)
        } else if (closestPickup != null && tank.ammoCount < TANK_MAX_AMMO) {
            // else gather ammo i
            turn = driveTowards(closestPickup.pos - tank.pos, tank.facing)
        } else if (closestEnemy != null) {
            // we are full, time to hunt
            turn = driveTowards(closestEnemy.pos - tank.pos, tank.facing)
        }

        // if both the enemy is in range and we have ammo then light em up
        // point turret at enemy
        var targetIntercept: Vec2? = null
        if (closestEnemy != null) {
            val rememberedPos = memory[closestEnemy]
            if (rememberedPos != null) {
                val targetVel = (closestEnemy.pos - rememberedPos) * (FRAMES_PER_SECOND)

                targetIntercept = calculateAimPoint(
                    targetPos = closestEnemy.pos,
                    targetVel = targetVel,
                    shooterPos = tank.pos,
                    projectileSpeed = PROJECTILE_VELOCITY
                )

                targetIntercept?.let {
                    turnTurret =
                        driveTowards(it - tank.pos, tank.facing + tank.turretFacing) - (turn * SECONDS_PER_FRAME * TANK_TURN_RATE)
                }
            } else {
                targetIntercept = closestEnemy.pos
                turnTurret = driveTowards(
                    closestEnemy.pos - tank.pos,
                    tank.facing + tank.turretFacing
                ) - (turn * SECONDS_PER_FRAME * TANK_TURN_RATE)
            }
        }

        // Reset the memory
        memory.clear()
        sensor.targets.forEach { memory[it] = it.pos }

        return Control(
            forward = forward,
            turn = turn,
            turnTurret = turnTurret,
            fire = targetIntercept != null && targetIntercept.distanceTo(tank.pos) < MAX_SHOT_TAKE_DISTANCE,
            collect = closestPickup != null && closestPickup.pos.distanceTo(tank.pos) < TANK_PICKUP_RADIUS,
            target = targetIntercept
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

    private fun turnHorizontal(tankFacing: Double, projectileFacing: Double): Double {

        val one = turnLeftOrRight(tankFacing, projectileFacing + Math.PI / 2)
        val two = turnLeftOrRight(tankFacing, projectileFacing - Math.PI / 2)

        return if (abs(one) < abs(two)) {
            one
        } else {
            two
        }
    }

    // Forwards or backwards to dodge.
    private fun driveDodge(
        tankPos: Vec2,
        tankFacing: Double,
        projectilePos: Vec2,
        projectileFacing: Double
    ): Double {

        val driveLine = Line(tankPos, tankPos + facingDist(tankFacing))
        val shotLine = Line(projectilePos, projectilePos + facingDist(projectileFacing))

        val intersection = intersection(driveLine, shotLine) ?: return 1.0

        // need to figure out if the intersection point is ahead or behind us
        return if ((intersection - tankPos).rotate(-tankFacing).x < 0) 1.0 else -1.0
    }

    companion object {
        const val MAX_PROJECTILE_DIST = 80
        const val MAX_PROJECTILE_DODGE_DIST = 30.0
        const val MAX_SHOT_TAKE_DISTANCE = PROJECTILE_MAX_FLIGHT_DIST * 3 / 4
    }


}