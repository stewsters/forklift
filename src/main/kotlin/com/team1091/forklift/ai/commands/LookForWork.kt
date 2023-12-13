package com.team1091.forklift.ai.commands

import com.team1091.forklift.Control
import com.team1091.forklift.Sensor
import com.team1091.forklift.entity.Forklift




class LookForWork : ForkliftCommand {
    override fun act(sensor: Sensor, forklift: Forklift): Commands {
        return Complete
    }
}