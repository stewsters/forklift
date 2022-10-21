package com.team1091.tanks;

import com.team1091.tanks.ai.AI;
import com.team1091.tanks.entity.Tank;
import org.jetbrains.annotations.NotNull;

public class JavaTankAi implements AI {
    @NotNull
    @Override
    public Control act(@NotNull Sensor sensor, @NotNull Tank tank) {



        return new Control(0.0, 1.0, -1.0, true, false);
    }
}
