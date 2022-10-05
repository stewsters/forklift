package com.team1091.tanks.ai

import com.team1091.tanks.Control
import com.team1091.tanks.Sensor
import com.team1091.tanks.entity.Tank

interface AI {
    fun act(sensor: Sensor, tank: Tank): Control
}