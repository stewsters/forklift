package com.team1091.tanks

import com.team1091.tanks.entity.Pickup
import com.team1091.tanks.entity.Projectile
import com.team1091.tanks.entity.Tank

data class Sensor(
    val targets: List<Tank>,
    val projectiles: List<Projectile>,
    val pickups:List<Pickup>
)