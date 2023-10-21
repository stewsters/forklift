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
    var orders = mutableMapOf<Pallet, Int>()

    fun takeTurn(dt: Double) {
        currentTime += dt
        // forklift ai processes
        val liftsToRemove = mutableListOf<Forklift>()

        val sensor = Sensor(
            terrain = terrain,
            forklifts = forklifts,
            pallets = pallets.toList(),
            loadingZones = loadingZones,
            orders = orders
        )
        forklifts.forEach { forklift ->
            val control = try {
                forklift.ai.act(
                    sensor,
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

            // Don't let forklifts leave the map or clip through walls
            if (bounds.inside(newPos) && terrain[newPos].canMove) {
                forklift.pos = newPos
            }

            if (control.pickUp && forklift.carrying == null) {

                val pickupPos = forklift.calculateEndEffector()
                val pickedUp = pallets.firstOrNull { it.pos.distanceTo(pickupPos) < PACKAGE_PICKUP_RADIUS }

                if (pickedUp != null) {
                    forklift.carrying = pickedUp
                    pallets.remove(pickedUp)
                }

            }

            if (control.place && forklift.carrying != null) {
                val endOfFork = forklift.calculateEndEffector()
                val pack = forklift.carrying!!
                if (terrain.contains(endOfFork.toIntRep()) && terrain[endOfFork].canHold) {
                    pack.pos = endOfFork
                    pallets.add(pack)
                    forklift.carrying = null
                }
            }
        }
        forklifts.removeAll(liftsToRemove)

        // TODO: score

        // if an order is resolved, resolve it, and give some more package
        if (orders.isEmpty()) {
            val load = pallets.shuffled().subList(0, 2)

            load.forEach {
                orders[it] = loadingZones.random().id
            }

        }


        // send orders if they are complete
        loadingZones.forEach { zone ->

            val requiredPallets = orders.filter { it.value == zone.id }.map { it.key }
            if(requiredPallets.isEmpty())
                return@forEach

            val palletsHere = pallets.filter { zone.area.contains(it.pos) }

            if (palletsHere.size == requiredPallets.size && palletsHere.containsAll(requiredPallets)) {

                pallets.removeAll(palletsHere)

                println("Got em")
            }


            // all pallets in the zone should be be there
//            val allPalletsAreHere =


            // close orders for these pallets,

            // drop off new pallets, open new orders

        }

        // if any loading zones are empty, add more packages
//        loadingZones.forEach {loadingZone->
//
//            if (pallets.none { loadingZone.area.contains(it.pos) })
//
//
//
//        }

//        orders.forEach {order->
//
//        }


    }

}

private fun Rectangle.inside(point: Vec2d): Boolean {
    return point.x >= lower.x && point.y >= lower.y && point.x <= upper.x && point.y <= upper.y
}

private operator fun <T> Matrix2d<T>.get(pos: Vec2d): T {
    return this.get(pos.x.toInt(), pos.y.toInt())
}