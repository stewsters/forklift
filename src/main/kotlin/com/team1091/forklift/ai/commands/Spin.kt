package com.team1091.forklift.ai.commands

import com.team1091.forklift.Control
import com.team1091.forklift.Sensor
import com.team1091.forklift.entity.Forklift

class Spin : ForkliftCommand {
    override fun act(sensor: Sensor, forklift: Forklift): Commands {
        return Continue(
            Control(
                forward = 0.1,
                turn = -0.1,
                pickUp = false,
                place = false
            )
        )
    }
}