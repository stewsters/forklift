package com.team1091.forklift.entity

import com.team1091.forklift.Vec2d
import com.team1091.forklift.ai.AI
import java.awt.Color

class Forklift(
    val ai: AI,
    var pos: Vec2d,
    var facing: Double,
    var carrying: Package?,
    val tint: Int = Color.WHITE.rgb
) {

    val displayName = ai.toString().substringBefore('@').substringAfterLast('.')
}