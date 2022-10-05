package com.team1091.tanks.ai

import com.team1091.tanks.Control
import com.team1091.tanks.Sensor
import com.team1091.tanks.entity.Tank
import processing.core.PApplet

class TestAi() : AI {
    override fun act(sensor: Sensor, tank: Tank): Control {



        return Control(
            forward = 1.0,
            turn = -0.5,
            turnTurret = 1.0,
            fire = true,
            collect = true
        )
    }

}
