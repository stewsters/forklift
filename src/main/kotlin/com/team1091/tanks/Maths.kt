package com.team1091.tanks

import kotlin.math.abs

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