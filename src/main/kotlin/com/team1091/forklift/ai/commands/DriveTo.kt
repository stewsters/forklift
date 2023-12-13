package com.team1091.forklift.ai.commands

import com.team1091.forklift.Sensor
import com.team1091.forklift.Vec2d
import com.team1091.forklift.entity.Forklift
import com.team1091.forklift.toCenter

// drive from where we are to the destination
class DriveTo(
    val dest: Vec2d
) : ForkliftCommand {
    override fun act(sensor: Sensor, forklift: Forklift): Commands {

        val possiblePath = sensor.findPath(forklift.pos.toIntRep(), dest.toIntRep())
        return if (possiblePath != null) {
            val path = possiblePath.map { it.toCenter() }

            DrivePath(path)
        } else {
            Failure
        }
    }

}

