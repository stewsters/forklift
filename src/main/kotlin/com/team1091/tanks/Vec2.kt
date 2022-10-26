package com.team1091.tanks

import kotlin.math.cos
import kotlin.math.sin

data class Vec2(
    val x: Double,
    val y: Double
) {
    fun distanceTo(other: Vec2): Double {
        return Math.sqrt(Math.pow(x - other.x, 2.0) + Math.pow(y - other.y, 2.0))
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