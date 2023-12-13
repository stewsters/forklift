package com.team1091.forklift.ai.commands

import com.team1091.forklift.Control
import com.team1091.forklift.Sensor
import com.team1091.forklift.Vec2d
import com.team1091.forklift.entity.Forklift

class PickUp() : ForkliftCommand {

    // drive to location
    // point at
    // drop off
    override fun act(sensor: Sensor, forklift: Forklift): Commands {
        // drive from where we are to the destination

        return if (forklift.carrying == null)
            Continue(Control(0.0, 0.0, true, false))
        else
            Complete
    }

}