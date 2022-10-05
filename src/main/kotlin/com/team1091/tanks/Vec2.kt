package com.team1091.tanks

data class Vec2(
    val x: Double,
    val y: Double
) {
    fun distanceTo(other: Vec2): Double {
        return Math.sqrt(Math.pow(x - other.x, 2.0) + Math.pow(y - other.y, 2.0))
    }
}