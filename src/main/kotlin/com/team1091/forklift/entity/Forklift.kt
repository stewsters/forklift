package com.team1091.forklift.entity

import com.team1091.forklift.FORKLIFT_PICKUP_DISTANCE
import com.team1091.forklift.FORWARD
import com.team1091.forklift.Vec2d
import com.team1091.forklift.ai.AI
import java.awt.Color

class Forklift(
    val ai: AI,
    var pos: Vec2d,
    var facing: Double,
    var carrying: Pallet?,
    val tint: Int = Color.WHITE.rgb
) {
    fun calculateEndEffector(): Vec2d {
        return  pos + FORWARD.times(FORKLIFT_PICKUP_DISTANCE).rotate(facing)
    }

    val displayName = ai.toString().substringBefore('@').substringAfterLast('.')
}