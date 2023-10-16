package com.team1091.forklift.ai

import com.team1091.forklift.Control
import com.team1091.forklift.Sensor
import com.team1091.forklift.entity.Forklift

class ExampleAi : AI {
    override fun act(sensor: Sensor, forklift: Forklift): Control {
        // sensor here has information on
        //  forklifts - a list of other forklifts out there
        //  pickups - a list of pickups.  Collect these up to regain ammo

        // we process this information and return a control detailing what you want to do
        return Control(
            forward = 0.0, // -1.0 for full speed backward, 1.0 for forward.
            turn = 0.0, // turn the vehicle
            pickUp = false,
            place = false,
        )
    }

}
