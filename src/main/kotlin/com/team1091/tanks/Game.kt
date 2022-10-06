package com.team1091.tanks

import com.team1091.tanks.entity.Pickup
import com.team1091.tanks.entity.Projectile
import com.team1091.tanks.entity.Tank
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class Game(
    val bounds: Vec2,
    val tanks: MutableList<Tank>,
    val projectiles: MutableList<Projectile> = mutableListOf(),
    val pickups: MutableList<Pickup> = mutableListOf()
) {

    var currentTime = 0.0

    fun takeTurn(dt: Double) {
        currentTime += dt
        // tanks ai processes
        val tanksToRemove = mutableListOf<Tank>()
        tanks.forEach { tank ->

            if (tank.life <= 0) {
                // You are dead, don't move
                tanksToRemove.add(tank)
                return@forEach
            }

            val control = tank.ai.act(
                Sensor(
                    targets = tanks.filter { it != tank },
                    projectiles = projectiles.toList(),
                    pickups = pickups.toList()
                ),
                tank
            )

            // apply control
            // turn
            tank.facing += control.turn.limit() * TANK_TURN_RATE * dt

            // turn turret speed
            tank.turretFacing += control.turnTurret.limit() * TURRET_TURN_RATE * dt

            // drive
            val speedModifier = (if (control.collect) TANK_VACUUM_SLOW * TANK_SPEED else TANK_SPEED)

            tank.pos = Vec2(
                tank.pos.x + cos(tank.facing) * control.forward.limit() * speedModifier * dt,
                tank.pos.y + sin(tank.facing) * control.forward.limit() * speedModifier * dt
            )

            // fire
            // spawn bullet
            if (control.fire && tank.ammoCount > 0 && tank.lastFired + TIME_TO_FIRE < currentTime) {
                // calculate barrel position
                val barrelEnd = Vec2(
                    tank.pos.x + cos(tank.facing + tank.turretFacing) * TANK_BARREL_LENGTH,
                    tank.pos.y + sin(tank.facing + tank.turretFacing) * TANK_BARREL_LENGTH
                )
                projectiles.add(
                    Projectile(
                        pos = barrelEnd,
                        facing = tank.facing + tank.turretFacing,
                        launchTime = currentTime
                    )
                )
                tank.ammoCount--
                tank.lastFired = currentTime
            }

            if (control.collect) {
                // grab any pickups we are on
                val capacity = TANK_MAX_AMMO - tank.ammoCount
                val pickedUp = pickups.filter { it.pos.distanceTo(tank.pos) < TANK_PICKUP_RADIUS }.take(capacity)

                tank.ammoCount += pickedUp.size
                pickups.removeAll(pickedUp)
            }
        }
        tanks.removeAll(tanksToRemove)

        val projectilesToRemove = mutableListOf<Projectile>()
        projectiles.forEach {

            val newPos = Vec2(
                it.pos.x + (dt * PROJECTILE_VELOCITY * cos(it.facing)),
                it.pos.y + (dt * PROJECTILE_VELOCITY * sin(it.facing))
            )

            val tanksInRange = tanks.filter { tank -> newPos.distanceTo(tank.pos) < TANK_RADIUS }

            if (tanksInRange.isNotEmpty() || it.launchTime + PROJECTILE_MAX_FLIGHT_TIME < currentTime) {
                tanksInRange.forEach {
                    it.life--
                }
                projectilesToRemove.add(it)
            } else if (newPos.x < 0 || newPos.x > bounds.x || newPos.y < 0 || newPos.y > bounds.y) {
                projectilesToRemove.add(it)
            }

            it.pos = newPos
        }

        projectiles.removeAll(projectilesToRemove)
    }

    fun isNotDone(): Boolean {
        return tanks.count { it.life > 0 } <= 1
    }
}

private fun Double.limit(): Double {
    return max(-1.0, min(1.0, this))
}
