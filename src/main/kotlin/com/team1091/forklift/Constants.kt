package com.team1091.forklift

const val FORKLIFT_PICKUP_DISTANCE = 0.25
const val PACKAGE_PICKUP_RADIUS = 0.25

const val FORKLIFT_SPEED = 0.6
const val FORKLIFT_VACUUM_SLOW = 0.6
const val FORKLIFT_TURN_RATE = 2.0

// We calculate positions 10 times per simulated second.
const val FRAMES_PER_SECOND = 10.0
const val SECONDS_PER_FRAME = 1.0 / FRAMES_PER_SECOND // 0.1