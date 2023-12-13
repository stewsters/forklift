package com.team1091.forklift.ai.commands

import com.team1091.forklift.Control
import com.team1091.forklift.PACKAGE_PICKUP_RADIUS
import com.team1091.forklift.Sensor
import com.team1091.forklift.Vec2d
import com.team1091.forklift.entity.Forklift
import com.team1091.forklift.toCenter

// drive from where we are to the destination
class DrivePath(
    var path: List<Vec2d>,
    val distance: Double = PACKAGE_PICKUP_RADIUS
) : ForkliftCommand {

    override fun initialize() {
    }

    override fun act(sensor: Sensor, forklift: Forklift): Commands {

        path.firstOrNull()?.apply {

            // shorten path
            if (path.isNotEmpty() && forklift.pos.distanceTo(this) < 0.25) {
                // we are close enough, go to the next one
                path = path.subList(1, path.size)
            }

            val turn = driveTowards(this - forklift.pos, forklift.facing)

            if (path.size==1 && forklift.pos.distanceTo(this)<distance) {
                return Complete
            }

            return Continue(
                Control(
                    forward = 1.0,
                    turn = turn,
                    pickUp = false,
                    place = false
                )
            )
        }

        // if we get here, the path is not possible, correct?
        return Failure

    }

}


fun driveTowards(offset: Vec2d, facing: Double): Double {
    val prime = offset.rotate(-facing)
    return if (prime.y > 0) {
        1.0
    } else {
        -1.0
    }
}
