package com.team1091.forklift.ai

import com.team1091.forklift.*
import com.team1091.forklift.entity.Forklift
import kotlin.math.abs


class ForkliftAi : AI {

    private val memory = mutableMapOf<Forklift, Vec2d>()

    override fun act(sensor: Sensor, forklift: Forklift): Control {

        // if we have no package, go get one.
        //  find the closest one in the array
        //  path to it

        // if we have a package to deliver, do it if possible


        return Control(
            forward = 1.0,
            turn = 0.5,
            pickUp = false,
            place = false
        )


//        val closestEnemy = sensor.targets
//            .filter { it.pos.distanceTo(forklift.pos) > TANK_BARREL_LENGTH }
//            .minByOrNull { it.pos.distanceTo(forklift.pos) }
//        val closestPickup = sensor.packages
//            .filter { closestEnemy != null && it.pos.distanceTo(forklift.pos) <= it.pos.distanceTo(closestEnemy.pos) }
//            .minByOrNull { it.pos.distanceTo(forklift.pos) }
//
//        val closestProjectile = sensor.projectiles
//            .filter { it.pos.distanceTo(forklift.pos) < MAX_PROJECTILE_DIST }
//            .filter { (forklift.pos - it.pos).rotate(-it.facing).x > 0 } // in front of us
//            .filter {
//                distanceToLine(
//                    forklift.pos,
//                    it.pos,
//                    it.pos + facingDist(it.facing, MAX_PROJECTILE_DODGE_DIST)
//                ) < MAX_PROJECTILE_DIST
//            } // in front of us
//            .minByOrNull { it.pos.distanceTo(forklift.pos) }
//
//        var turn = 0.0
//        var turnTurret = 0.0
//        var forward = 1.0
//
//
//        // Control driving
//        if (closestProjectile != null) {
//            // if the closest projectile is within range, dodge
//            turn = turnHorizontal(forklift.facing, closestProjectile.facing)
//            forward = driveDodge(forklift.pos, forklift.facing, closestProjectile.pos, closestProjectile.facing)
//        } else if (closestPickup != null && forklift.ammoCount < TANK_MAX_AMMO) {
//            // else gather ammo i
//            turn = driveTowards(closestPickup.pos - forklift.pos, forklift.facing)
//        } else if (closestEnemy != null) {
//            // we are full, time to hunt
//            turn = driveTowards(closestEnemy.pos - forklift.pos, forklift.facing)
//        }
//
//        // if both the enemy is in range and we have ammo then light em up
//        // point turret at enemy
//        var targetIntercept: Vec2d? = null
//        if (closestEnemy != null) {
//            val rememberedPos = memory[closestEnemy]
//            if (rememberedPos != null) {
//                val targetVel = (closestEnemy.pos - rememberedPos) * (FRAMES_PER_SECOND)
//
//                targetIntercept = calculateAimPoint(
//                    targetPos = closestEnemy.pos,
//                    targetVel = targetVel,
//                    shooterPos = forklift.pos,
//                    projectileSpeed = PROJECTILE_VELOCITY
//                )
//
//                targetIntercept?.let {
//                    turnTurret =
//                        driveTowards(
//                            it - forklift.pos,
//                            forklift.facing + forklift.turretFacing
//                        ) - (turn * SECONDS_PER_FRAME * TANK_TURN_RATE)
//                }
//            } else {
//                targetIntercept = closestEnemy.pos
//                turnTurret = driveTowards(
//                    closestEnemy.pos - forklift.pos,
//                    forklift.facing + forklift.turretFacing
//                ) - (turn * SECONDS_PER_FRAME * TANK_TURN_RATE)
//            }
//        }
//
//        // Reset the memory
//        memory.clear()
//        sensor.targets.forEach { memory[it] = it.pos }
//
//        return Control(
//            forward = forward,
//            turn = turn,
//            turnTurret = turnTurret,
//            fire = targetIntercept != null && targetIntercept.distanceTo(forklift.pos) < MAX_SHOT_TAKE_DISTANCE,
//            collect = closestPickup != null && closestPickup.pos.distanceTo(forklift.pos) < TANK_PICKUP_RADIUS,
//            target = targetIntercept
//        )
    }

    private fun driveTowards(offset: Vec2d, facing: Double): Double {
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
        tankPos: Vec2d,
        tankFacing: Double,
        projectilePos: Vec2d,
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