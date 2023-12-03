package com.team1091.forklift.ai.commands

import com.team1091.forklift.Control
import com.team1091.forklift.Sensor
import com.team1091.forklift.entity.Forklift
import java.util.*

class Sequential : ForkliftCommand {

    private val commands: MutableList<ForkliftCommand>

    constructor(vararg commands: ForkliftCommand) {
        this.commands = commands.toMutableList()
    }

    constructor(commandList: ArrayList<ForkliftCommand>) {
        this.commands = commandList
    }

    override fun initialize() {
        if (commands.isNotEmpty())
            this.commands.first().initialize()
    }

    override fun act(sensor: Sensor, forklift: Forklift): Commands {

        if (commands.isEmpty()) {
            return Complete
        }

        // do the first one, if it's done remove it
        val first = commands.first()
        val next = first.act(sensor, forklift)

        return  when (next) {
            is Continue -> {
                // keep doing what we are doing
                return next
            }
            // Finish, move onto next
            is Complete, is Failure -> {
                // Current command is done, go to the next
                first.teardown()

                if (commands.size == 1)
                    return Complete // List done

                commands.removeAt(0)
                commands[0].initialize()
                Continue(Control(0.0,0.0,false,false))
            }
            // Replace
            is ForkliftCommand -> {
                // Replace current command
                commands[0].teardown()
                commands[0] = next
                commands[0].initialize()
                Continue(Control(0.0,0.0,false,false))
            }
        }

    }
}
