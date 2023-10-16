package com.team1091.forklift

const val TANK_MAX_LIFE = 4.0
const val TIME_TO_FIRE = 2.0
const val TANK_BARREL_LENGTH = 9.0
const val PACKAGE_PICKUP_RADIUS = 8.0
const val TANK_RADIUS = 8.0
const val TANK_SPEED = 1.0
const val TANK_VACUUM_SLOW = 0.6
const val TANK_TURN_RATE = 1.0

const val PROJECTILE_VELOCITY: Double = 30.0
const val PROJECTILE_MAX_FLIGHT_TIME = 10.0
const val PROJECTILE_MAX_FLIGHT_DIST = PROJECTILE_VELOCITY * PROJECTILE_MAX_FLIGHT_TIME

const val START_RADIUS = 300.0

// We calculate positions 10 times per simulated second.
const val FRAMES_PER_SECOND = 10.0
const val SECONDS_PER_FRAME = 1.0 / FRAMES_PER_SECOND // 0.1

const val MAX_PICKUPS = 100
const val MIN_PICKUPS = 30