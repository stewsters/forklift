package com.team1091.tanks

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Vec2(
    val x: Double,
    val y: Double
) {
    fun distanceTo(other: Vec2): Double {
        return sqrt((x - other.x).pow(2.0) + (y - other.y).pow(2.0))
    }

    operator fun minus(other: Vec2): Vec2 {
        return Vec2(this.x - other.x, this.y - other.y)
    }

    operator fun plus(other: Vec2): Vec2 {
        return Vec2(this.x + other.x, this.y + other.y)
    }

    fun rotate(angle: Double): Vec2 {
        return Vec2(
            x = x * cos(angle) - y * sin(angle),
            y = y * cos(angle) + x * sin(angle)
        )

    }

    operator fun times(scale: Double): Vec2 {
        return Vec2(
            x * scale,
            y * scale
        )
    }

    // Dot product
    operator fun times(other: Vec2): Double {
        return x * other.x + y * other.y
    }
}