package com.team1091.tanks.entity

import com.team1091.tanks.Vec2
import com.team1091.tanks.ai.AI

class Tank(
    val ai: AI,
    var life: Int,
    var pos: Vec2,
    var facing : Double,
    var turretFacing: Double = 0.0,
    var ammoCount: Int = 0,
    var lastFired: Double = 0.0,
    val faction: Faction
)