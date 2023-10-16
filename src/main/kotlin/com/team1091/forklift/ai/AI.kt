package com.team1091.forklift.ai

import com.team1091.forklift.Control
import com.team1091.forklift.Sensor
import com.team1091.forklift.entity.Forklift

interface AI {
    fun act(sensor: Sensor, forklift: Forklift): Control
}