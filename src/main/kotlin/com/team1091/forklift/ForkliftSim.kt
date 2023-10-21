package com.team1091.forklift

import com.team1091.forklift.ai.AI
import com.team1091.forklift.ai.ForkliftAi
import com.team1091.forklift.entity.Forklift
import com.team1091.forklift.entity.LoadingZone
import com.team1091.forklift.entity.Package
import com.team1091.forklift.map.TileType
import kaiju.mapgen.two.fill
import kaiju.mapgen.two.fillWithBorder
import kaiju.mapgen.two.predicate.and
import kaiju.mapgen.two.predicate.notNearCell
import kaiju.math.Vec2
import kaiju.math.geom.Rectangle
import kaiju.math.matrix2dOf
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.core.PImage
import java.awt.Color
import kotlin.random.Random

val rectangle = Rectangle(Vec2(0, 0), Vec2(13, 13))
val scale = 32f

class ForkliftSim : PApplet() {

    lateinit var forkliftImage: PImage
    lateinit var packageImage: PImage

    lateinit var floorImage: PImage
    lateinit var shelfImage: PImage
    lateinit var wallImage: PImage

    lateinit var background: PGraphics
    lateinit var game: Game

//    private val size = Vec2d(, )

    override fun settings() {
        size((scale * (rectangle.upper.x)).toInt(), (scale * (rectangle.upper.y)).toInt())
    }

    override fun setup() {
        forkliftImage = loadImage("assets/tank.png")
        packageImage = loadImage("assets/pickup.png")

        floorImage = loadImage("assets/floor.png")
        shelfImage = loadImage("assets/shelf.png")
        wallImage = loadImage("assets/wall.png")


        // Add your ais here
        val ais = listOf(
            ForkliftAi()
        )
        game = makeGame(ais)

        background = createGraphics(width, height)
        background.beginDraw()
        background.background(100)
        game.terrain.forEachIndexed { x, y, t ->
            val type = when (t) {
                TileType.FLOOR -> floorImage
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

        background.beginDraw()
        background.stroke(Color.DARK_GRAY.rgb)

        // draw tracks
        game.forklifts.forEach { forklift ->
            val leftF = Vec2d(4.0, -6.0).rotate(forklift.facing)
            val leftB = Vec2d(-4.0, -6.0).rotate(forklift.facing)
            val rightF = Vec2d(4.0, 6.0).rotate(forklift.facing)
            val rightB = Vec2d(-4.0, 6.0).rotate(forklift.facing)

            background.stroke(Color.DARK_GRAY.rgb)

            background.line(
                (forklift.pos.x + leftF.x).toFloat(), (forklift.pos.y + leftF.y).toFloat(),
                (forklift.pos.x + leftB.x).toFloat(), (forklift.pos.y + leftB.y).toFloat()
            )

            background.line(
                (forklift.pos.x + rightF.x).toFloat(), (forklift.pos.y + rightF.y).toFloat(),
                (forklift.pos.x + rightB.x).toFloat(), (forklift.pos.y + rightB.y).toFloat()
            )

        }
        background.endDraw()


        imageMode(CENTER)
        rectMode(CENTER)

        // draw target
//        game.forklifts.forEach { tank ->
//            tank.targetPos?.let { pos ->
//                val x = pos.x.toFloat()
//                val y = pos.y.toFloat()
//                tint(tank.faction.color.rgb)
//                image(crossHairsImage, x, y)
//            }
//        }
        // render forklifts
        game.forklifts.forEach { forklift ->
//            tint(tank.faction.color.rgb)
            pushMatrix()
            translate(forklift.pos.x.toFloat() * scale, forklift.pos.y.toFloat() * scale)

            text(forklift.displayName, -30f, -10f)
//            text("${tank.life} / ${tank.ammoCount}", -15f, 20f)
            rotate((forklift.facing + Math.PI.toFloat() / 2.0).toFloat())
            tint(forklift.tint)
            image(forkliftImage, 0f, 0f)
            tint(Color.WHITE.rgb)

            stroke(Color.green.rgb)
            //line(0.0f, 0.0f, 0f, -70f) //line to see what direction tank is facing

            popMatrix()
        }

        // draw projectile
//        game.projectiles.forEach { projectile ->
//            pushMatrix()
//            translate(projectile.pos.x.toFloat(), projectile.pos.y.toFloat())
//            rotate((projectile.facing + Math.PI.toFloat() / 2.0).toFloat())
//            image(shellImage, 0f, 0f)
//            popMatrix()
//        }

        // draw pickups
        game.packages.forEach { pickup ->
            pushMatrix()
            translate(pickup.pos.x.toFloat() * scale, pickup.pos.y.toFloat() * scale)
            image(packageImage, 0f, 0f)
            popMatrix()
        }

    }

}


fun makeGame(ais: List<AI>): Game {

    val terrain = matrix2dOf(rectangle.upper.x, rectangle.upper.y) { x, y -> TileType.FLOOR }
    fillWithBorder(terrain, TileType.FLOOR, TileType.WALL)
    fill(
        terrain,
        predicate = and(
            notNearCell(TileType.WALL),
            { m, x, y -> x % 6 != 0 },
            { m, x, y -> y % 2 == 0 }
        ),
        brush2d = { m, x, y -> m[x, y] = TileType.SHELF }
    )


    // TODO: add loading zones

    val centerX = terrain.getSize().x / 2
    val centerY = terrain.getSize().y / 2


    val zones = listOf(
        Rectangle(lower = Vec2(centerX - 3, 0), upper = Vec2(centerX + 3, 2)) // top
    ).mapIndexed { i, t -> LoadingZone(i, t) }


    // TODO: half of loading zones have packages, half need packages


    val game = Game(
        bounds = rectangle,
        terrain = terrain,
        forklifts = ais.shuffled().mapIndexed { i, ai ->
            val angle = Random.nextDouble(Math.PI * 2)
            val pos = Vec2d(1 + i.toDouble(), 1 + i.toDouble())

            Forklift(
                ai = ai,
                pos = pos,
                facing = angle,
                carrying = null
            )
        }.toMutableList(),
        packages = (0 until MAX_PICKUPS).map {
            Package(
                it,
                Vec2d(
                    x = Random.nextDouble(rectangle.upper.x.toDouble()),
                    y = Random.nextDouble(rectangle.upper.y.toDouble())
                )
            )
        }.toMutableList(),
        loadingZones = zones
    )

    return game
}