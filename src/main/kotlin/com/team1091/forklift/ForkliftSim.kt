package com.team1091.forklift

import com.team1091.forklift.ai.AI
import com.team1091.forklift.ai.ForkliftAi
import com.team1091.forklift.entity.Forklift
import com.team1091.forklift.entity.LoadingZone
import com.team1091.forklift.entity.Pallet
import com.team1091.forklift.map.TileType
import kaiju.mapgen.two.fill
import kaiju.mapgen.two.fillWithBorder
import kaiju.mapgen.two.predicate.and
import kaiju.mapgen.two.predicate.notNearCell
import kaiju.math.Matrix2d
import kaiju.math.Vec2
import kaiju.math.geom.Rectangle
import kaiju.math.matrix2dOf
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.core.PImage
import java.awt.Color
import kotlin.random.Random

val rectangle = Rectangle(Vec2(0, 0), Vec2(15, 15))
val scale = 64f

class ForkliftSim : PApplet() {

    lateinit var forkliftImage: PImage
    lateinit var packageImage: PImage

    lateinit var floorImage: PImage
    lateinit var shelfImage: PImage
    lateinit var wallImage: PImage
    lateinit var loadingZoneImage: PImage

    lateinit var background: PGraphics
    lateinit var game: Game

    override fun settings() {
        size((scale * (rectangle.upper.x)).toInt(), (scale * (rectangle.upper.y)).toInt())
    }

    override fun setup() {
        forkliftImage = loadImage("assets/forklift.png")
        packageImage = loadImage("assets/package.png")

        floorImage = loadImage("assets/floor.png")
        shelfImage = loadImage("assets/shelf.png")
        wallImage = loadImage("assets/wall.png")
        loadingZoneImage = loadImage("assets/loadingZone.png")

        // Add your ais here
        val ais = listOf(
            ForkliftAi(),
            ForkliftAi(),
            ForkliftAi(),
            ForkliftAi(),
            ForkliftAi(),
            ForkliftAi(),
            ForkliftAi(),
            ForkliftAi()
        )
        game = makeGame(ais)

        background = createGraphics(width, height)
        background.beginDraw()
        background.background(100)
        game.terrain.forEachIndexed { x, y, t ->
            val type = when (t) {
                TileType.FLOOR -> floorImage
                TileType.LOADING_ZONE -> loadingZoneImage
                TileType.SHELF -> shelfImage
                TileType.WALL -> wallImage
            }

            background.image(type, x * scale, y * scale, scale, scale)
        }


        background.endDraw()
    }

    override fun draw() {
        game.takeTurn(SECONDS_PER_FRAME)

        clear()
        imageMode(PConstants.CORNER)
        image(background, 0f, 0f)

        imageMode(CENTER)
        rectMode(CENTER)

        // render forklifts
        game.forklifts.forEach { forklift ->

            pushMatrix()
            translate(forklift.pos.x.toFloat() * scale, forklift.pos.y.toFloat() * scale)

            text(forklift.displayName, -30f, -10f)
            rotate((forklift.facing + Math.PI.toFloat() / 2.0).toFloat())
            tint(forklift.tint)
            image(forkliftImage, 0f, 0f)
            tint(Color.WHITE.rgb)

            stroke(Color.green.rgb)

            popMatrix()
        }

        // draw pickups
        game.pallets.forEach { pickup ->
            pushMatrix()
            translate(pickup.pos.x.toFloat() * scale, pickup.pos.y.toFloat() * scale)
            image(packageImage, 0f, 0f, scale / 2, scale / 2)
            popMatrix()
        }

    }

}


fun makeGame(ais: List<AI>): Game {

    val centerX = rectangle.center().x
    val centerY = rectangle.center().y

    val zones = listOf(
        Rectangle(lower = Vec2(centerX - 1, 0), upper = Vec2(centerX + 1, 0)), // top
        Rectangle(
            lower = Vec2(centerX - 1, rectangle.upper.y - 1),
            upper = Vec2(centerX + 1, rectangle.upper.y - 1)
        ), // bottom
        Rectangle(lower = Vec2(0, centerY - 1), upper = Vec2(0, centerY + 1)), // left
        Rectangle(
            lower = Vec2(rectangle.upper.x - 1, centerX - 1),
            upper = Vec2(rectangle.upper.x - 1, centerX + 1)
        ), // right
    ).mapIndexed { i, t -> LoadingZone(i, t) }


    val terrain = matrix2dOf(rectangle.upper.x, rectangle.upper.y) { x, y -> TileType.FLOOR }
    fillWithBorder(terrain, TileType.FLOOR, TileType.WALL)
    fill(
        terrain,
        predicate = and(
            notNearCell(TileType.WALL),
            { m, x, y -> x % 7 != 0 },
            { m, x, y -> y % 2 == 0 }
        ),
        brush2d = { m, x, y -> m[x, y] = TileType.SHELF }
    )

    fill(
        terrain,
        predicate = and(
            { m, x, y ->
                zones.any { it.area.contains(x, y) }
            }
        ),
        brush2d = { m, x, y -> m[x, y] = TileType.LOADING_ZONE }
    )

    var packageId = 0
    val pallets = zones.flatMap { zone ->
        zone.area.allPoints()
    }.map {
        Pallet(
            packageId++,
            Vec2d(x = it.x.toDouble() + 0.5, y = it.y.toDouble() + 0.5)
        )
    }.toMutableList()

    val validStartLocations = findMatchingCoordinates(terrain) { x, y, t ->
        t == TileType.FLOOR
    }.shuffled()

    val game = Game(
        bounds = rectangle,
        terrain = terrain,
        forklifts = ais.shuffled().mapIndexed { i, ai ->
            val angle = Random.nextDouble(Math.PI * 2)
            val pos = validStartLocations[i].toCenter()

            Forklift(
                ai = ai,
                pos = pos,
                facing = angle,
                carrying = null
            )
        }.toMutableList(),
        pallets = pallets,
        loadingZones = zones
    )

    return game
}

fun findMatchingCoordinates(terrain: Matrix2d<TileType>, predicate: (Int, Int, TileType) -> Boolean): List<Vec2> {
    val validStartLocations = mutableListOf<Vec2>()
    terrain.forEachIndexed { x, y, t ->
        if (predicate(x, y, t))
            validStartLocations.add(
                Vec2(x, y)
            )
    }
    return validStartLocations
}

private fun Rectangle.allPoints(): List<Vec2> {
    return cartesianProduct(
        (lower.x..upper.x).toList(),
        (lower.y..upper.y).toList()
    ).map { Vec2(it.first, it.second) }
}


fun <T, U> cartesianProduct(c1: Collection<T>, c2: Collection<U>): List<Pair<T, U>> {
    return c1.flatMap { lhsElem -> c2.map { rhsElem -> lhsElem to rhsElem } }
}