package com.team1091.tanks

import com.team1091.tanks.ai.AdrianTankAi
import com.team1091.tanks.ai.BraedenTankAi
import com.team1091.tanks.ai.EthanTankAi
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

        // Add your tank here
        val ais = listOf(
            AdrianTankAi(),
            EthanTankAi(),
            BestTankEver(),
            BraedenTankAi()
        )

        val rotation = Random.nextDouble(Math.PI * 2)
        game = Game(
            bounds = size,
            tanks = ais.shuffled().mapIndexed { i, ai ->
                val angle = rotation + (i * (Math.PI * 2) / (ais.size))
                Tank(
                    ai = ai,
                    life = TANK_MAX_LIFE,
                    pos = Vec2(
                        START_RADIUS * Math.cos(angle) + size.x / 2,
                        START_RADIUS * Math.sin(angle) + size.y / 2
                    ),
                    facing = angle + Math.PI / 2,
                    ammoCount = 5,
                    faction = Faction.values()[i]
                )
            }.toMutableList(),
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
        game.takeTurn(SECONDS_PER_FRAME)

        clear()
        imageMode(CENTER)
        game.tanks.forEach { tank ->
            tint(tank.faction.color.rgb)
            pushMatrix()
            translate(tank.pos.x.toFloat(), tank.pos.y.toFloat())

            text(tank.displayName, -30f, -10f)
            text("${tank.life} / ${tank.ammoCount}", -15f, 20f)
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