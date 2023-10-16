package com.team1091.forklift

data class Control(
    val forward: Double,
    val turn: Double,
    val pickUp: Boolean,
    val place: Boolean,
)