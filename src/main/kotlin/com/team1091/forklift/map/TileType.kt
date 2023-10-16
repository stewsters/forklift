package com.team1091.forklift.map

enum class TileType(
    val canMove: Boolean,
    val canHold: Boolean
) {
    FLOOR(true, true),
    SHELF(false, true),
    WALL(false, false)
}