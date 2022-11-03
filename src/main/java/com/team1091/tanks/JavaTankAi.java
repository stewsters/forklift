package com.team1091.tanks;

import com.team1091.tanks.ai.AI;
import com.team1091.tanks.entity.Tank;
import org.jetbrains.annotations.NotNull;

public class JavaTankAi implements AI {
    @NotNull
    @Override
    public Control act(@NotNull Sensor sensor, @NotNull Tank tank) {
        // sensor here has information on
        //  getTargets() - a list of other tanks out there.
        //  getProjectiles() - a list of flying shots.  Avoid getting hit!
        //  getPickups() - a list of pickups.  Collect these up to regain ammo

        // tank is a reference to your tank

        // we process this information and return a control detailing what you want to do
        return new Control(
                0.0,
                1.0,
                -1.0,
                true,
                false
        );
    }
}
