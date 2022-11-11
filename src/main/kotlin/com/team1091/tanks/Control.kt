package com.team1091.tanks

data class Control(
    val forward: Double,
    val turn: Double,
    val turnTurret: Double,
    val fire: Boolean,
    val collect: Boolean,
    val target: Vec2? = null
)