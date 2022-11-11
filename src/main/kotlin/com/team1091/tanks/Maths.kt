package com.team1091.tanks

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Given a current angle, find out how to turn to target angle.
 *  @param current The angle you are at in radians
 *  @param target The angle you want to turn to in radians
 *  @return the turn (positive is left, negative is right)
 */
fun turnLeftOrRight(current: Double, target: Double): Double {
    val alpha = target - current
    val beta = target - current + Math.PI * 2
    val gamma = target - current - Math.PI * 2

    val alphaAbs = abs(alpha)
    val betaAbs = abs(beta)
    val gammaAbs = abs(gamma)

    return if (alphaAbs <= betaAbs && alphaAbs <= gammaAbs) {
        alpha
    } else if (betaAbs <= alphaAbs && betaAbs <= gammaAbs) {
        beta
    } else {
        gamma
    }
}

/**
 * Allows you to calculate an intercept course
 */
fun calculateAimPoint(
    targetPos: Vec2,
    targetVel: Vec2,
    shooterPos: Vec2,
    projectileSpeed: Double
): Vec2? {
    val dP = targetPos - shooterPos
    val a = targetVel * targetVel - (projectileSpeed * projectileSpeed)
    val b = 2.0 * (dP * targetVel)
    val c = dP * dP
    var d = b * b - 4 * a * c
    if (d < 0.0) {
        return null
    }
    d = sqrt(d)
    var t0 = (-b - d) / (2.0 * a)
    var t1 = (-b + d) / (2.0 * a)
    val t: Double
    if (t0 > t1) {
        val c = t0
        t0 = t1
        t1 = c
    }
    if (t1 < 0.0) {
        return null
    }
    if (t0 >= 0.0) {
        t = t0
    } else {
        t = t1
    }
    val xAimPoint = targetPos + targetVel * t
    return xAimPoint
}

/**
 * Finds a vector on a unit circle of size length.
 */
fun facingDist(facing: Double, length: Double = 1.0) = Vec2(length * cos(facing), length * sin(facing))

/**
 * Distance from a line segment to a point
 */
fun distanceToLine(pos: Vec2, lineStart: Vec2, lineEnd: Vec2) = distanceToLine(
    pos.x, pos.y,
    lineStart.x, lineStart.y,
    lineEnd.x, lineEnd.y
)

fun distanceToLine(x: Double, y: Double, x1: Double, y1: Double, x2: Double, y2: Double): Double {
    val A = x - x1
    val B = y - y1
    val C = x2 - x1
    val D = y2 - y1
    val dot = A * C + B * D
    val lenSq = C * C + D * D
    var param = -1.0
    if (lenSq != 0.0) //in case of 0 length line
        param = dot / lenSq
    val xx: Double
    val yy: Double
    if (param < 0) {
        xx = x1
        yy = y1
    } else if (param > 1) {
        xx = x2
        yy = y2
    } else {
        xx = x1 + param * C
        yy = y1 + param * D
    }
    val dx = x - xx
    val dy = y - yy
    return sqrt((dx * dx + dy * dy))
}

data class Line(val start: Vec2, val end: Vec2)

fun intersection(a: Line, b: Line): Vec2? {
    val x1 = a.start.x
    val y1 = a.start.y
    val x2 = a.end.x
    val y2 = a.end.y
    val x3 = b.start.x
    val y3 = b.start.y
    val x4 = b.end.x
    val y4 = b.end.y
    val d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
    if (d == 0.0) {
        return null
    }
    val xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d
    val yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d
    return Vec2(xi, yi)
}