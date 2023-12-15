package com.team1091.forklift.ai.commands

import com.team1091.forklift.Control
import com.team1091.forklift.FORKLIFT_PICKUP_DISTANCE
import com.team1091.forklift.Sensor
import com.team1091.forklift.entity.Forklift

class PickUp() : ForkliftCommand {

    // drive to location
    // point at
    // drop off
    override fun act(sensor: Sensor, forklift: Forklift): Commands {
        // drive from where we are to the destination
        if (forklift.carrying != null)
          Complete

        // TODO: we get stuck attempting to pickup where we cannot.  Need to maneuver into position
        val closestPallet = sensor.pallets.minByOrNull { it.pos.distanceTo(forklift.pos) }

        if (closestPallet == null) {
            return Failure
        }

        val distanceToTarget = closestPallet.pos.distanceTo(forklift.pos)

        if (distanceToTarget > 1.5) {
            return Failure
        }

        val turn = driveTowards(closestPallet.pos - forklift.pos, forklift.facing)
        val forward = distanceToTarget - FORKLIFT_PICKUP_DISTANCE

        return  Continue(Control(forward, turn, true, false))

    }

}