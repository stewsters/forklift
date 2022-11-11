package com.team1091.tanks.ai

import com.team1091.tanks.BestTankEver
import com.team1091.tanks.SIMULATION_SPEED
import com.team1091.tanks.makeGame
import org.junit.jupiter.api.Test
import java.text.DecimalFormat


class RunSim {
    var df = DecimalFormat("##.##%")

    @Test
    fun runTests() {
        val totalRuns = 1000
        val ais = listOf(
//            DoNothingAi(),
            AdrianTankAi(),
            EthanTankAi(),
            BestTankEver(),
            BraedenTankAi(),
            Mary()
        )

        val winners = mutableMapOf<String, Int>()
        ais.forEach { winners.putIfAbsent(it.javaClass.name, 0) }

        repeat(totalRuns) {
            val game = makeGame(ais)

            // Let it go for 10 minutes, or until done
            while (game.isNotDone() && game.currentTime < 600) {
                game.takeTurn(SIMULATION_SPEED)
            }

            val survivors = game.tanks.filter { it.life > 0 }.map { it.ai.javaClass.name }

            println("Survivors round ${it}:")
            println(survivors)
            survivors.forEach { survivor ->
                winners[survivor] = (winners[survivor] ?: 0) + 1
            }
        }

        println("Winners:")
        winners.entries
            .sortedByDescending { it.value }
            .map { "${it.key} : ${it.value} (${df.format(it.value.toDouble() / totalRuns.toDouble())})" }
            .forEach { println(it) }
    }

}