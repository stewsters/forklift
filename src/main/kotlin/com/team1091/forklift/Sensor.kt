package com.team1091.forklift

import com.team1091.forklift.entity.Forklift
import com.team1091.forklift.entity.LoadingZone
import com.team1091.forklift.entity.Pallet
import com.team1091.forklift.map.TileType
import kaiju.math.Matrix2d
import kaiju.math.Vec2
import kaiju.math.getEuclideanDistance
import kaiju.pathfinder.findPath2d

data class Sensor(
    val terrain: Matrix2d<TileType>,
    val forklifts: List<Forklift>,
    val pallets: List<Pallet>,
    val loadingZones: List<LoadingZone>,
    val orders: MutableMap<Pallet, Int> // package to where it goes
) {
    fun misplacedPackages() =
        pallets.filter { packag ->

            val packageDestination = orders[packag]

            loadingZones
                .filter { it.id != packageDestination } // we are not where we need to be
                .any { loadingZone -> loadingZone.area.contains(packag.pos.x.toInt(), packag.pos.y.toInt()) }

        }

    fun findPath(start: Vec2, end: Vec2): List<Vec2>? =
        findPath2d(
            terrain.getSize(),
            cost = { 1.0 },
            heuristic = { p1, p2 -> getEuclideanDistance(p1, p2) },
            neighbors = { it.vonNeumanNeighborhood().filter { terrain.contains(it) && terrain[it].canMove } },
            start = start,
            end = end
        )

    fun findPathIgnoringLast(start: Vec2, end: Vec2): List<Vec2>? =
        findPath2d(
            terrain.getSize(),
            cost = { 1.0 },
            heuristic = { p1, p2 -> getEuclideanDistance(p1, p2) },
            neighbors = { it.vonNeumanNeighborhood().filter { terrain.contains(it) && (terrain[it].canMove || it == end ) } },
            start = start,
            end = end
        )


    fun findNearestEmptyShelf(start: Vec2): Vec2? = findMatchingCoordinates(terrain) { x, y, t ->
        t == TileType.SHELF && pallets.none { it.pos.x.toInt() == x && it.pos.y.toInt() == x }
    }.minByOrNull { p -> getEuclideanDistance(start, p) }

}