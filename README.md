# Forklift

A forklift sim game.

In this game, you write an AI to control a robot forklift and deliver packages.

## Getting started

### Prereqs

You will need to install a few things to get this working.

* an editor (I recommend Intellij Community)
* a git client - we use this to share code.
* a JDK (intellij can install this for you, or we can manually set it up)

### Getting running

Make a local copy of the project

```bash
git clone https://github.com/stewsters/forklift.git
```

Then open it in intellij. It will download the dependencies which will take a minute or two.
To run it open the gradle window on the right side, and hit tasks>application>run

If it works, you should see some forklifts driving.

### Create an AI

Add that AI to the ai list in ForkliftSim.kt. Try running it again and see if that forklift appears.

### The AI

You will get some input each frame and have to return a Control object with what you want to do this frame.

```kotlin
    override fun act(sensor: Sensor, tank: Tank): Control {
        // sensor here has information on
        //  targets - a list of other forklifts out there.
        //  projectiles - a list of flying shots.  Avoid getting hit!
        //  pickups - a list of pickups.  Collect these up to regain ammo
    
        // tank is a reference to your tank
    
        // we process this information and return a control detailing what you want to do
        return Control(
            forward = 0.0, // -1.0 for full speed backward, 1.0 for forward.
            turn = 0.0, // turn the tank
            turnTurret = 1.0, // turn the turret
            fire = false, // shoot the gun.  It has a period of time after the shot that it takes to reload.
            collect = true // pick up ammo pickups you are on top of.  This slows you down, so only use it when you are near pickups.
        )
    }
```

Often when you are presented with a complicated programming problem it can be a bit overwhelming.

It makes sense to break it down and get one thing working at a time.

There are a few things you will likely want to handle first:

* Drive to and pick up ammo
* Turn your turret towards an opponent and fire
* Drive to avoid getting shot