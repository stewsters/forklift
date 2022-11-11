package com.team1091.tanks.ai

import com.team1091.tanks.Control
import com.team1091.tanks.Sensor
import com.team1091.tanks.entity.Tank

class ExampleAi : AI {
    override fun act(sensor: Sensor, tank: Tank): Control {
        // sensor here has information on
        //  targets - a list of other tanks out there.
        //  projectiles - a list of flying shots.  Avoid getting hit!
        //  pickups - a list of pickups.  Collect these up to regain ammo

        // tank is a reference to your tank

        // we process this information and return a control detailing what you want to do
        return Control(
            forward = 0.0, // -1.0 for full speed backward, 1.0 for forward.
            turn = 0.0, // turn the tank
            turnTurret = 1.0, // turn the turret
            fire = false, // shoot the gun.  It has a period of time after the shot that it takes to reload.
            collect = true // pick up ammo pickups you are on top of.  This slows you down, so only use it when you are near pickups.
        )
    }

}
