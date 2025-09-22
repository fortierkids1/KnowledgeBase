---
aliases:
  - Backlash
tags: []
---
Backlash refers to the small loss of mechanical control induced by small gaps in gears, belts, or other attachment points. This is also referred to as "Slop" or "play".

A small amount of backlash will always be present in systems. However, severe backlash can stem from poor mechanical build or design, or from excessive system wear. For systems in motion, backlash also causes significant wear, leading to even _more_ backlash. 

![[gear-backlash.gif]]

## Impact of Backlash

For many FRC systems, small amounts of backlash can be mostly ignored on the code side. The standard equipment and precision requirements generally handle it.

If backlash gets to be severe, it can start impacting the bot in different ways
- Reduced performance and precision
- Increased wear + worsening backlash
- Increased risk of severe damage due to high forces in the gear train
- Difficulty in controlling systems
- Potential for dropped game pieces due to bouncing.

## Systems of Concern

Backlash can be built up in any system, but it's only a problem when the system changes loading directions; Either when going from accelerating and decelerating, changing directions, or colliding with the environment.

As such, it's mostly a concern for systems where you care about the position or precise rotation, rather than rate of motion. 

The most notable backlash-heavy system is [[SuperStructure Arm|Arms]] or other pivoting systems. By their nature, these magnify the small angular difference along their length, making it far more visible. These systems also handle significant torque, resulting high wear and a mechanical challenge to minimize backlash, and have range of motions that show backlash in several different loading states.

![[backlash-arm-deviation.png]]

[[Differential Drive]] systems also often have notable backlash due to the heavy wear in the gearboxes. In the case where you need precision movement (mostly autos or assisted sequences), this can result in struggles. 

[[SuperStructure Rollers|Rollers]] and [[Superstructure Indexer|Indexers]] used for game piece alignment sometimes encounter backlash due to construction details and precision requirements. 

## Minimizing Backlash

- [[Motion Profiles|Motion Profiling]] helps reduce the impact of backlash by controlling and reducing the torque applied to your geartrain. This reduces wear, meaning your system is simply less prone to developing and worsening backlash.
- [[FeedForwards]] by their nature do not consider the effects of backlash in their output. This does not help with precision loss, but prevents 
- [[Slew Rate Limiting]] on the output can reduce high-frequency changes to the output. This helps reduce system wear, and fixes system response to such wear.
- [[PID|PIDs]] and other [[Closed Loop Controller|Closed Loop Controllers]], can be used to help recapture precision, but typically require measurement of the system output (height, arm rotation, etc).

If this sounds suspiciously similar to "just having good motion code", you'd be correct! Good motion is robust against backlash errors, and also helps prevent it. 

## Resolving Symptoms of Backlash

> [!BUG] Mechanical Problem, mechanical solution
> Like all mechanical faults, backlash is best resolved on the mechanical side at the source.
> Code fixes should generally aim to reduce wear caused by the small amount of unavoidable backlash.
> Code handling more significant backlash should be considered a temporary measure until the hardware can be fixed.

#### Thrashing/oscillation
This refers to high-speed "rattling" of the motor. This is commonly seen on systems that can rest in an "unloaded" state, when controlled by a [[PID]] or other [[Closed Loop Controller]]. It's most common on [[SuperStructure Rollers|Roller]] or Drivetrain systems, but can be present on others. 

This issue occurs because the PID will be tuned for a "loaded" condition: The controller is expecting a specific amount of resistance to motion (weight of bot, air resistance, inertia, etc). However, in the backlash region, there is effectively no resistance to motion. In this scenario, the PID is effectively "overtuned", resulting in sharp P oscillation. The greater the backlash, the more likely you are to end up with your system settling in a state that allows such oscillations.

The simplest resolution tends to be the application of a `ClosedLoopRampRate` on the Rev motor controller configuration. This applies a [[Slew Rate Limiting|Slew Rate Limiter]] on the output, which a very good job of preventing high-frequency output changes like this, without constraining typical operation.

It's also possible to observe this in [[SuperStructure Elevator|Elevator]] or [[SuperStructure Arm|Arm]] systems in some cases. Notably if the resting against a hard stop.

#### Bouncing/Slop
When an [[SuperStructure Arm|Arm]] or [[SuperStructure Elevator|Elevator]] visibly has significant backlash, it will often "bounce" without any effective motor control effort. This is especially visible when driving, ending a motion, or making small quick motions.

[[FeedForwards]] can play a big role in improving system stability. The stable output helps reduce PID output in general, and are stable regardless of the system's physical state.

[[PID]] systems can behave poorly when the system slams across the backlash gap, providing a sharp P and D output back toward the main system, which is an undesirable response. Output ramp rates can help make this gentler.


#### Deadzones/Precision Loss
This shows up as an inability to declare precise control near the setpoint. The backlash amounts to losing a small amount of motion any time you reverse direction, meaning you cannot correct for slight overshoots, or lag slightly behind the calibrated position targets as wear sets in.

In FRC systems, you'll generally see this in 
- [[Differential Drive]] systems, due to the heavy wear of those gearboxes
- [[SuperStructure Rollers|Roller]] systems being used for linear travel as a passthrough/indexer, particularly when driven by a belt or chain system. 
- Arm systems that travel over or under the pivot point. This changes the gravity loading from one side of the backlash to the other

It generally does not show up on systems that 
- Are gravity loaded in a consistent way, such as [[SuperStructure Elevator|Elevators]] or [[SuperStructure Arm|Arms]] that don't go over/under the pivot point

Using encoders measuring the output directly is the most straightforward solution; This allows a simple PID to handle any unexpected position quirks.

If an output encoder is not available, or extra precision is demanded, you can model the backlash as part of your system. This means measuring the magnitude of the backlash, and then applying an offset based on the direction of motion. That then compensates for the lost motion when changing directions. Depending on your reference and application, this might be a symmetric offset (+/- backlash/2) or an asymmetric (-0/+backlash or -backlash/+0) .
