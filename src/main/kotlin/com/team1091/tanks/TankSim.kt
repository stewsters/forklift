package com.team1091.tanks

import com.team1091.tanks.ai.AdrianTankAi
import com.team1091.tanks.ai.BraedenTankAi
import com.team1091.tanks.ai.Test76Ai
import com.team1091.tanks.entity.Faction
import com.team1091.tanks.entity.Pickup
import com.team1091.tanks.entity.Tank
import processing.core.PApplet
import processing.core.PImage
import java.awt.Color
import kotlin.random.Random

class TankSim : PApplet() {

    lateinit var tankImage: PImage
    lateinit var turretImage: PImage
    lateinit var shellImage: PImage
    lateinit var pickupImage: PImage

    lateinit var game: Game

    private val size = Vec2(800.0, 800.0)

    override fun settings() {
        size(size.x.toInt(), size.y.toInt())
    }

    override fun setup() {
        tankImage = loadImage("tank.png")
        turretImage = loadImage("turret.png")
        shellImage = loadImage("shell.png")
        pickupImage = loadImage("pickup.png")

        game = Game(
            bounds = size,
            tanks = mutableListOf(
                Tank(
                    ai = AdrianTankAi(),
                    life = TANK_MAX_LIFE,
                    pos = Vec2(100.0, 100.0),
                    facing = 0.0,
                    ammoCount = 5,
                    faction = Faction.RED
                ),
                Tank(
                    ai = Test76Ai(),
                    life = TANK_MAX_LIFE,
                    pos = Vec2( 100.0, size.y - 100.0),
                    facing = 0.0,
                    ammoCount = 5,
                    faction = Faction.BLUE
                ),
                Tank(
                    ai = BestTankEver(),
                    life = TANK_MAX_LIFE,
                    pos = Vec2(size.x - 100.0,  100.0),
                    facing = Math.PI,
                    ammoCount = 5,
                    faction = Faction.GREEN
                ),
                Tank(
                    ai = BraedenTankAi(),
                    life = TANK_MAX_LIFE,
                    pos = Vec2(size.x - 100.0, size.y - 100.0),
                    facing = Math.PI,
                    ammoCount = 5,
                    faction = Faction.PINK
                )

            ),
            pickups = (0..100).map {
                Pickup(
                    Vec2(
                        x = Random.nextDouble(size.x),
                        y = Random.nextDouble(size.y)
                    )
                )
            }.toMutableList()
        )
    }

    override fun draw() {
        game.takeTurn(0.1)

        clear()
        imageMode(CENTER)
        game.tanks.forEach { tank ->
            tint(tank.faction.color.rgb)
            pushMatrix()
            translate(tank.pos.x.toFloat(), tank.pos.y.toFloat())

            text(tank.displayName, -30f, -10f)
            text(  "${tank.life} / ${tank.ammoCount}", -15f, 20f)
            rotate((tank.facing + Math.PI.toFloat() / 2.0).toFloat())
            image(tankImage, 0f, 0f)
            tint(Color.WHITE.rgb)
            rotate(tank.turretFacing.toFloat())
            image(turretImage, 0f, 0f)

            popMatrix()
        }

        game.projectiles.forEach { projectile ->
            // draw projectile
            pushMatrix()
            translate(projectile.pos.x.toFloat(), projectile.pos.y.toFloat())
            rotate((projectile.facing + Math.PI.toFloat() / 2.0).toFloat())
            image(shellImage, 0f, 0f)
            popMatrix()
        }

        game.pickups.forEach { pickup ->
            pushMatrix()
            translate(pickup.pos.x.toFloat(), pickup.pos.y.toFloat())
            image(pickupImage, 0f, 0f)
            popMatrix()
        }

    }

}