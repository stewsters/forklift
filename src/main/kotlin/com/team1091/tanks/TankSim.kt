package com.team1091.tanks

import com.team1091.tanks.ai.TestAi
import com.team1091.tanks.entity.Pickup
import com.team1091.tanks.entity.Tank
import processing.core.PApplet
import processing.core.PImage

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
            tanks = listOf(
                Tank(
                    ai = TestAi(),
                    life = 10,
                    pos = Vec2(100.0, 100.0),
                    facing = 0.0,
                    ammoCount = 5
                ),

                Tank(
                    ai = TestAi(),
                    life = 10,
                    pos = Vec2(size.x - 100.0, size.y - 100.0),
                    facing = Math.PI,
                    ammoCount = 5
                )
            ),
            pickups = (0..100).map {
                Pickup(
                    Vec2(
                        x = random(size.x.toFloat()).toDouble(),
                        y = random(size.y.toFloat()).toDouble()
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
            pushMatrix()
            translate(tank.pos.x.toFloat(), tank.pos.y.toFloat())
            rotate((tank.facing + Math.PI.toFloat() / 2.0).toFloat())
            image(tankImage, 0f, 0f)

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