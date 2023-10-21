package com.team1091.forklift

import kaiju.math.Vec2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


val FORWARD = Vec2d(0.0, 1.0)

data class Vec2d(
    val x: Double,
    val y: Double
) {
    fun distanceTo(other: Vec2d): Double {
        return sqrt((x - other.x).pow(2.0) + (y - other.y).pow(2.0))
    }

    operator fun minus(other: Vec2d): Vec2d {
        return Vec2d(this.x - other.x, this.y - other.y)
    }

    operator fun plus(other: Vec2d): Vec2d {
        return Vec2d(this.x + other.x, this.y + other.y)
    }

    fun rotate(angle: Double): Vec2d {
        return Vec2d(
            x = x * cos(angle) - y * sin(angle),
            y = y * cos(angle) + x * sin(angle)
        )

    }

    operator fun times(scale: Double): Vec2d {
        return Vec2d(
            x * scale,
            y * scale
        )
    }

    // Dot product
    operator fun times(other: Vec2d): Double {
        return x * other.x + y * other.y
    }

    fun toIntRep(): Vec2 {
        return Vec2(x.toInt(), y.toInt())
    }
}