package com.team1091.forklift

import com.team1091.forklift.entity.Forklift
import com.team1091.forklift.entity.Package

data class Sensor(
    val forklifts: List<Forklift>,
    val packages: List<Package>
)