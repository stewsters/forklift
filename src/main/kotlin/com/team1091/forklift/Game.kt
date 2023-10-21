package com.team1091.forklift

import com.team1091.forklift.entity.Forklift
import com.team1091.forklift.entity.LoadingZone
import com.team1091.forklift.entity.Package
import com.team1091.forklift.map.TileType
import kaiju.math.Matrix2d
import kaiju.math.geom.Rectangle
import kotlin.math.cos
import kotlin.math.sin

class Game(
    val bounds: Rectangle,
    val terrain: Matrix2d<TileType>,
    val forklifts: MutableList<Forklift>,
    val packages: MutableList<Package> = mutableListOf(),
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
                        forklifts = forklifts.filter { it != forklift },
                        packages = packages.toList()
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
            forklift.facing += control.turn.limit() * TANK_TURN_RATE * dt

            // turn turret speed
            //forklift.turretFacing += control.turnTurret.limit() * TURRET_TURN_RATE * dt

            // drive
            val speedModifier = (if (control.pickUp) TANK_VACUUM_SLOW * TANK_SPEED else TANK_SPEED)

            val newPos = Vec2d(
                forklift.pos.x + cos(forklift.facing) * control.forward.limit() * speedModifier * dt,
                forklift.pos.y + sin(forklift.facing) * control.forward.limit() * speedModifier * dt
            )

            // Dont let forklifts leave the map or clip through walls
            if (bounds.inside(newPos) && terrain[newPos].canMove) {
                forklift.pos = newPos
            }
            // TODO: collision with terrain


//            // fire
//            // spawn bullet
//            if (control.fire && forklift.ammoCount > 0 && forklift.lastFired + TIME_TO_FIRE < currentTime) {
//                // calculate barrel position
//                val barrelEnd = Vec2d(
//                    forklift.pos.x + cos(forklift.facing + forklift.turretFacing) * TANK_BARREL_LENGTH,
//                    forklift.pos.y + sin(forklift.facing + forklift.turretFacing) * TANK_BARREL_LENGTH
//                )
//                projectiles.add(
//                    Projectile(
//                        pos = barrelEnd,
//                        facing = forklift.facing + forklift.turretFacing,
//                        launchTime = currentTime
//                    )
//                )
//                forklift.ammoCount--
//                forklift.lastFired = currentTime
//            }

            if (control.pickUp && forklift.carrying == null) {
                // grab any pickups we are on
//                val capacity = TANK_MAX_AMMO - forklift.ammoCount

                val pickupPos = forklift.pos + FORWARD.rotate(forklift.facing)
                val pickedUp = packages.firstOrNull { it.pos.distanceTo(pickupPos) < PACKAGE_PICKUP_RADIUS }

                if (pickedUp != null) {
                    forklift.carrying = pickedUp
                    packages.remove(pickedUp)
                }

            }

            if (control.place && forklift.carrying != null) {
                val pickupPos = forklift.pos + FORWARD.rotate(forklift.facing)
                val pack = forklift.carrying!!
                if (terrain[pickupPos].canHold) {
                    pack.pos = pickupPos
                    packages.add(pack)
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