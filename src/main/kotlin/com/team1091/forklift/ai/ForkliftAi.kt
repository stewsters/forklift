package com.team1091.forklift.ai

import com.team1091.forklift.Control
import com.team1091.forklift.Sensor
import com.team1091.forklift.ai.commands.Complete
import com.team1091.forklift.ai.commands.Continue
import com.team1091.forklift.ai.commands.DoNothing
import com.team1091.forklift.ai.commands.DrivePath
import com.team1091.forklift.ai.commands.DriveTo
import com.team1091.forklift.ai.commands.DropOff
import com.team1091.forklift.ai.commands.Failure
import com.team1091.forklift.ai.commands.ForkliftCommand
import com.team1091.forklift.ai.commands.LookForWork
import com.team1091.forklift.ai.commands.PickUp
import com.team1091.forklift.ai.commands.Sequential
import com.team1091.forklift.entity.Forklift
import com.team1091.forklift.toCenter


class ForkliftAi : AI {

    private var command: ForkliftCommand = LookForWork()


    override fun act(sensor: Sensor, forklift: Forklift): Control {

        // if we are carrying a package
        //if()

        // TODO: store command

        val next = command.act(sensor, forklift)

        when (next) {
            is Complete -> {
                // should get new work
                command = generateInitialState(forklift,sensor)
            }

            is Failure -> {
                // should get new work
                command = generateInitialState(forklift,sensor)
            }

            is Continue -> {
                return next.control
            }

            is ForkliftCommand -> {
                command.teardown()
                next.initialize()
                command = next
            }

        }

        return DoNothing.basis


    }


//    private fun turnHorizontal(tankFacing: Double, projectileFacing: Double): Double {
//
//        val one = turnLeftOrRight(tankFacing, projectileFacing + Math.PI / 2)
//        val two = turnLeftOrRight(tankFacing, projectileFacing - Math.PI / 2)
//
//        return if (abs(one) < abs(two)) {
//            one
//        } else {
//            two
//        }
//    }
//
//    // Forwards or backwards to dodge.
//    private fun driveDodge(
//        tankPos: Vec2d,
//        tankFacing: Double,
//        projectilePos: Vec2d,
//        projectileFacing: Double
//    ): Double {
//
//        val driveLine = Line(tankPos, tankPos + facingDist(tankFacing))
//        val shotLine = Line(projectilePos, projectilePos + facingDist(projectileFacing))
//
//        val intersection = intersection(driveLine, shotLine) ?: return 1.0
//
//        // need to figure out if the intersection point is ahead or behind us
//        return if ((intersection - tankPos).rotate(-tankFacing).x < 0) 1.0 else -1.0
//    }

}


// default should be to ask mother for instructions

// drive to a destination, drop it off


// Drive to a target, pick it up
// [driveto target, pickup]


// Controller with a planner

// State machine / command pattern
// Wait for orders (spread out into unused parts? go charge?)
// Move package (source, destination)

private fun generateInitialState(forklift: Forklift, sensor: Sensor): ForkliftCommand {

    // TODO: we probably should have a search to find our next move

    // if we know where it goes, drive there and drop

    // if we don't know where it goes, store it somewhere

    if (forklift.carrying != null) {
        // we have a package, if it has a loading zone drop it off there
        val orderId = sensor.orders[forklift.carrying]
        if (orderId != null) {
            // we need to get this package to a loading zone
            val destinationZone = sensor.loadingZones.find { it.id == orderId }

            if (destinationZone != null) {
                return Sequential(
                    DriveTo(destinationZone.area.center().toCenter()),
                    DropOff()
                )
            }
        } else {
            // no loading zone yet, drop it off on a shelf

            val spotToPutIt = sensor.findNearestEmptyShelf(forklift.pos.toIntRep())
            if (spotToPutIt != null) {

                val possiblePath = sensor.findPathIgnoringLast(forklift.pos.toIntRep(), spotToPutIt)
                if (possiblePath != null) {
                    val path = possiblePath.map { it.toCenter() }

                    return Sequential(
                        DrivePath(path),
                        // face for dropoff
                        DropOff()
                    )
                }
            } else {
                return DropOff()
            }

        }

    } else {
        val misplacedPackages = sensor.misplacedPackages()
        val packageToGrab = misplacedPackages.minByOrNull { it.pos.distanceTo(forklift.pos) }
        if (packageToGrab != null) {
            val possiblePath = sensor.findPath(forklift.pos.toIntRep(), packageToGrab.pos.toIntRep())
            if (possiblePath != null) {
                val path = possiblePath.map { it.toCenter() }
                return Sequential(
                    DrivePath(path),
                    PickUp()
                )
            }

        }
        //
    }

    return LookForWork()
}


//    private fun elder(forklift: Forklift, sensor: Sensor): ForkliftCommand {
//
//
//        // if we have no package, lets be useful.
//        // go get one and store it
//        if (forklift.carrying == null) {
//            // find all packages that are in a loading zone but should not be
//            if (targetPickup == null) {
//                val misplacedPackages = sensor.misplacedPackages()
//                val packageToGrab = misplacedPackages.minByOrNull { it.pos.distanceTo(forklift.pos) }
//
//                if (packageToGrab != null) {
//                    val possiblePath = sensor.findPath(forklift.pos.toIntRep(), packageToGrab.pos.toIntRep())
//                    if (possiblePath != null) {
//                        path = possiblePath.map { it.toCenter() }
//                        targetPickup = packageToGrab
//                    }
//                }
//            }
//            if (targetPickup != null) {
//                (path?.firstOrNull() ?: targetPickup?.pos)?.apply {
//
//                    // shorten path
//                    if (path!!.isNotEmpty() && forklift.pos.distanceTo(this) < 0.25) {
//                        // we are close enough, go to the next one
//                        path = path!!.subList(1, path!!.size)
//                    }
//
//                    val turn = driveTowards(this - forklift.pos, forklift.facing)
//
//                    val pickup = forklift.pos.distanceTo(targetPickup!!.pos) < PACKAGE_PICKUP_RADIUS
//
//                    if (pickup) {
//                        targetPickup = null
//                        path = null
//                    }
//
//                    return Control(
//                        forward = 1.0,
//                        turn = turn,
//                        pickUp = pickup,
//                        place = false
//                    )
//                }
//            }
//        } else {
//            // we are carrying a package
//            // Does that package have a destination?  if so, put it there
//            val orderId = sensor.orders[forklift.carrying]
//            if (orderId != null) {
//                // we need to get this package to a loading zone
//                val destinationZone = sensor.loadingZones.find { it.id == orderId }
//
//                if (path == null && destinationZone != null) { // path to destination
//
//                    val possiblePath = sensor.findPath(forklift.pos.toIntRep(), destinationZone.area.center())
//                    if (possiblePath != null) {
//                        path = possiblePath.map { it.toCenter() }
//                        targetPickup = null
//                        targetDropoff = null
//                    }
//                } else if (path != null) {
//
//                    val nextDest = path!!.firstOrNull() ?: destinationZone?.area?.center()?.toCenter()
//
//                    // shorten path
//                    if (forklift.pos.distanceTo(nextDest!!) < 0.25) {
//                        // we are close enough, go to the next one
//                        path = path!!.subList(1, path!!.size)
//                    }
//
//                    val dest = path!!.firstOrNull() ?: targetDropoff!!
//
//                    return Control(
//                        forward = 0.25,// forklift.pos.distanceTo(dest) - FORKLIFT_PICKUP_DISTANCE,
//                        turn = driveTowards(dest - forklift.pos, forklift.facing),
//                        pickUp = false,
//                        place = forklift.calculateEndEffector().distanceTo(dest) < PACKAGE_PICKUP_RADIUS,
//                    )
//
//                }
//            } else { // This package has nowhere to go, find a shelf and drop it there
//
//
//                if (targetDropoff != null && path != null && path!!.isNotEmpty()) {
//                    // drive that path
//
//                    val nextDest = path!!.firstOrNull() ?: targetDropoff
//
//                    // shorten path
//                    if (forklift.pos.distanceTo(nextDest!!) < 0.25) {
//                        // we are close enough, go to the next one
//                        path = path!!.subList(1, path!!.size)
//                    }
//
//                    val dest = path!!.firstOrNull() ?: targetDropoff!!
//
//                    return Control(
//                        forward = 0.25,// forklift.pos.distanceTo(dest) - FORKLIFT_PICKUP_DISTANCE,
//                        turn = driveTowards(dest - forklift.pos, forklift.facing),
//                        pickUp = false,
//                        place = forklift.calculateEndEffector().distanceTo(dest) < PACKAGE_PICKUP_RADIUS,
//                    )
//                } else {
//                    val spotToPutIt = sensor.findNearestEmptyShelf(forklift.pos.toIntRep())
//                    if (spotToPutIt != null) {
//
//                        val possiblePath = sensor.findPathIgnoringLast(forklift.pos.toIntRep(), spotToPutIt)
//                        if (possiblePath != null) {
//                            path = possiblePath.map { it.toCenter() }
//                            targetDropoff = spotToPutIt.toCenter()
//                            targetPickup = null
//                        }
//                    }
//                }
//            }
//
//
//        }
//
//
//        // If it doesn't, we need to store it
//
//        // find one we need to move, that will either be one in
//
//
//        //  find the closest one in the array
//        //  path to it
//
//        // if we have a package to deliver, do it if possible
//
//
//        return Control(
//            forward = 0.1,
//            turn = -0.1,
//            pickUp = false,
//            place = false
//        )
//
//    }