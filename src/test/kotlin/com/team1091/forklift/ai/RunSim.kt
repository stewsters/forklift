package com.team1091.forklift.ai

import com.team1091.forklift.BestTankEver
import com.team1091.forklift.SECONDS_PER_FRAME
import com.team1091.forklift.makeGame
import org.junit.jupiter.api.Test
import java.text.DecimalFormat


class RunSim {
    var df = DecimalFormat("##.##%")

    @Test
    fun runTests() {
        val totalRuns = 1000
        val ais = listOf(
//            DoNothingAi(),
            FokliftAi(),
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
                game.takeTurn(SECONDS_PER_FRAME)
            }

            val survivors = game.forklifts.filter { it.life > 0 }.map { it.ai.javaClass.name }

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