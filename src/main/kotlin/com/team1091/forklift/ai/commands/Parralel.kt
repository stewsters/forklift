package com.team1091.forklift.ai.commands

import com.team1091.forklift.Sensor
import com.team1091.forklift.entity.Forklift

// Not sure if this name really captures what this is
class Parralel : ForkliftCommand {

    private val commands: List<ForkliftCommand>

    constructor(vararg commands: ForkliftCommand) {
        this.commands = commands.toMutableList()
    }

    constructor(commandList: ArrayList<ForkliftCommand>) {
        this.commands = commandList
    }

    override fun initialize() {
        commands.forEach { it.initialize() }
    }

    override fun act(sensor: Sensor, forklift: Forklift): Commands {

        if (commands.isEmpty()) {
            return Complete
        }

        // do them all, until one returns null.  Right now this doesn't clean up after itself
        for (command in commands) {
            val results = command.act(sensor, forklift)
            if (results is Complete || results is Failure) {
                return results
            }
        }
        return this
    }

    override fun teardown() {
        commands.forEach { it.teardown() }
    }

}
