package com.team1091.forklift.ai.commands

import com.team1091.forklift.Control
import com.team1091.forklift.Sensor
import com.team1091.forklift.entity.Forklift

sealed interface Commands

object Complete : Commands
object Failure : Commands
data class Continue(val control: Control) : Commands

//data class Replace(val newCommand: ForkliftCommand) : Commands

interface ForkliftCommand : Commands {
    fun initialize() {}
    fun act(sensor: Sensor, forklift: Forklift): Commands

    fun teardown() {}
}


// commands
// completed
// failure
// change to task