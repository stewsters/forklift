package com.team1091.forklift.ai

import com.team1091.forklift.Control
import com.team1091.forklift.Line
import com.team1091.forklift.PACKAGE_PICKUP_RADIUS
import com.team1091.forklift.Sensor
import com.team1091.forklift.Vec2d
import com.team1091.forklift.entity.Forklift
import com.team1091.forklift.entity.Pallet
import com.team1091.forklift.facingDist
import com.team1091.forklift.intersection
import com.team1091.forklift.toCenter
import com.team1091.forklift.turnLeftOrRight
import kotlin.math.abs


class ForkliftAi : AI {

//    private val memory = mutableMapOf<Forklift, Vec2d>()

    private var targetPickup: Pallet? = null
    private var path: List<Vec2d>? = null

    override fun act(sensor: Sensor, forklift: Forklift): Control {

        // if we are carrying a package
        //if()


        // if we know where it goes, drive there and drop

        // if we dont know where it goes, store it somewhere


        // if we have no package, lets be useful.
        // go get one and store it
        if (forklift.carrying == null) {
            // find all packages that are
            // in a loading zone
            // should not be

            if (targetPickup == null) {
                val misplacedPackages = sensor.misplacedPackages()
                val packageToGrab = misplacedPackages.minByOrNull { it.pos.distanceTo(forklift.pos) }

                if (packageToGrab != null) {
                    val possiblePath = sensor.findPath(forklift.pos.toIntRep(), packageToGrab.pos.toIntRep())
                    if (possiblePath != null) {
                        path = possiblePath.map { it.toCenter() }
                        targetPickup = packageToGrab
                    }
                }
            }

            (path?.firstOrNull() ?: targetPickup?.pos)?.apply {

                // shorten path
                if (path!!.isNotEmpty() && forklift.pos.distanceTo(this) < 0.25) {
                    // we are close enough, go to the next one
                    path = path!!.subList(1, path!!.size)
                }

                val turn = driveTowards(this - forklift.pos, forklift.facing)

                return Control(
                    forward = 1.0,
                    turn = turn,
                    pickUp = forklift.pos.distanceTo(targetPickup!!.pos) < PACKAGE_PICKUP_RADIUS,
                    place = false
                )
            }
        } else {
            // we are carrying a package
            // Does that package have a destination?  if so, put it there
            val orderId = sensor.orders[forklift.carrying]
            if (orderId != null) {
                // we need to get this package to a loading zone

                val destinationZone = sensor.loadingZones.find { it.id == orderId }

                if (path == null && destinationZone != null) { // path to destination

                    val possiblePath = sensor.findPath(forklift.pos.toIntRep(), destinationZone.area.center())
                    if (possiblePath != null) {
                        path = possiblePath.map { it.toCenter() }
                        targetPickup = null
                    }


                }


            }


            // If it doesnt, we need to store it
        }


        // find one we need to move, that will either be one in


        //  find the closest one in the array
        //  path to it

        // if we have a package to deliver, do it if possible


        return Control(
            forward = 0.1,
            turn = -0.1,
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

}