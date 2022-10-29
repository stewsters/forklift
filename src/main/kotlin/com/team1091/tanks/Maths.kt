package com.team1091.tanks

import kotlin.math.abs
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
    val dP = targetPos - shooterPos;
    val a = targetVel * targetVel - (projectileSpeed * projectileSpeed)
    val b = 2.0 * (dP * targetVel)
    val c = dP * dP
    var d = b * b - 4 * a * c;
    if (d < 0.0) {
        return null
    }
    d = sqrt(d)
    var t0 = (-b - d) / (2.0 * a);
    var t1 = (-b + d) / (2.0 * a);
    var t: Double
    if (t0 > t1) {
        val c = t0
        t0 = t1;
        t1 = c;
    }
    if (t1 < 0.0) {
        return null
    }
    if (t0 >= 0.0) {
        t = t0;
    } else {
        t = t1;
    }
    val xAimPoint = targetPos + targetVel * t;
    return xAimPoint;
}
