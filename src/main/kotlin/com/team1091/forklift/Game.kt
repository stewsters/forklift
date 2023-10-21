package com.team1091.forklift

import com.team1091.forklift.entity.Forklift
import com.team1091.forklift.entity.LoadingZone
import com.team1091.forklift.entity.Pallet
import com.team1091.forklift.map.TileType
import kaiju.math.Matrix2d
import kaiju.math.geom.Rectangle
import kotlin.math.cos
import kotlin.math.sin

class Game(
    val bounds: Rectangle,
    val terrain: Matrix2d<TileType>,
    val forklifts: MutableList<Forklift>,
    val pallets: MutableList<Pallet> = mutableListOf(),
    val loadingZones: List<LoadingZone> = mutableListOf()
) {

    var currentTime = 0.0

    fun takeTurn(dt: Double) {
        currentTime += dt
        // forklift ai processes
        val liftsToRemove = mutableListOf<Forklift>()
        forklifts.forEach { forklift ->

            val control = try {
                forklift.ai.act(
                    Sensor(
                        terrain = terrain,
                        forklifts = forklifts.filter { it != forklift },
                        pallets = pallets.toList(),
                        loadingZones = loadingZones,
                        orders = mutableMapOf()
                    ),
                    forklift
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Control(0.0, 0.0, false, false)
            }
            // Set target
            // forklift.targetPos = control.target

            // apply control
            // turn
            forklift.facing += control.turn.limit() * FORKLIFT_TURN_RATE * dt

            // turn turret speed
            //forklift.turretFacing += control.turnTurret.limit() * TURRET_TURN_RATE * dt

            // drive
            val speedModifier = (if (control.pickUp) FORKLIFT_VACUUM_SLOW * FORKLIFT_SPEED else FORKLIFT_SPEED)

            val newPos = Vec2d(
                forklift.pos.x + cos(forklift.facing) * control.forward.limit() * speedModifier * dt,
                forklift.pos.y + sin(forklift.facing) * control.forward.limit() * speedModifier * dt
            )

            // Dont let forklifts leave the map or clip through walls
            if (bounds.inside(newPos) && terrain[newPos].canMove) {
                forklift.pos = newPos
            }

            if (control.pickUp && forklift.carrying == null) {

                val pickupPos = forklift.pos + FORWARD.times(FORKLIFT_PICKUP_DISTANCE).rotate(forklift.facing)
                val pickedUp = pallets.firstOrNull { it.pos.distanceTo(pickupPos) < PACKAGE_PICKUP_RADIUS }

                if (pickedUp != null) {
                    forklift.carrying = pickedUp
                    pallets.remove(pickedUp)
                }

            }

            if (control.place && forklift.carrying != null) {
                val pickupPos = forklift.pos + FORWARD.rotate(forklift.facing)
                val pack = forklift.carrying!!
                if (terrain[pickupPos].canHold) {
                    pack.pos = pickupPos
                    pallets.add(pack)
                    forklift.carrying = null
                }
            }
        }
        forklifts.removeAll(liftsToRemove)

        // TODO: score

    }

}

private fun Rectangle.inside(point: Vec2d): Boolean {
    return point.x >= lower.x && point.y >= lower.y && point.x <= upper.x && point.y <= upper.y
}

private operator fun <T> Matrix2d<T>.get(pos: Vec2d): T {
    return this.get(pos.x.toInt(), pos.y.toInt())
}