---
tags:
  - stub
aliases:
  - Intake
---


Requires:
[[SuperStructure Rollers]]
[[Sensing Basics]]

Recommends:
[[State Machines]]

Requires as needed:
[[SuperStructure Rollers]]
[[SuperStructure Elevator]]
[[SuperStructure Arm]]

## Success Criteria
- [ ] Create an "over the bumper" intake system
- [ ] Add a controller button to engage the intake process. It must retract when released
- [ ] The button must automatically stop and retract the intake when a game piece is retracted

## Synopsis
Intake complexity can range from very simple rollers that capture a game piece, to complex actuated systems intertwined with other scoring mechanisms. 

A common "over the bumper" intake archetype is a deployed system that
- Actuates outward past the frame perimeter
- Engages rollers to intake game piece
- Retracts with the game piece upon completion of a game piece

The speed of deployment and retraction both impact cycle times, forming a critical competitive aspect of the bot. 

The automatic detection and retraction provide cycle advantages (streamlining the driver experience), but also prevent fouls and damage due to the collisions on the deployed mechanism.

Intakes often are a [[Compound Subsystem|Compound Subsystem]] , and come with several quirks for structuring and control

## Intakes: Taming the unknown

The major practical difference between intakes and other "subsystems" is their routine interaction with unknowns. Intake arms might extend into walls, intake rollers might get pressed into a loading station, and everything is approaching game pieces in a variety of speeds and states. Every FRC intake is different, but this one aspect is consistent.

Good mechanical design goes a long way to handling unknowns, but programming is affected too.  Programming a robust intake demands identifying and resolving ways that these interactions can go wrong, and resolving them in a way that leaves the robot in an operational state (and preferably, with a game piece). Sometimes this is resolved in code, and sometimes requires hardware or design tweaks. Intakes tend to have more physical iterations than many other parts of the robot.

## Detection + Sensing

For most intakes, you want a clear confirmation that the intake process is Done and a game piece is loaded. This typically means reviewing the mechanical design, and identifying what, if any, sensing methods will work reliably to identify successful intake.

Common approaches are
- Break beams / Range finders like [[LaserCan]] : Since these sensors are non-contact, they provide a easy to use, and relatively safe way to interact with game pieces with little design overhead. 
- A backplate with a switch/button: This requires special mechanical design, but gives a simple boolean state to indicate successful loading. This typically only works with intakes that interact with a back-stop, but can be made to work with other mechanisms that can actuate a switch when objects are in a known position
- Current Detection: This is a common option for intakes that pull in directly into a backstop, particularly for rigid game pieces. Like other places where current detection is used, it's either trivial + very effective, or ineffective + extremely challenging, depending on the design and interactions. 
- Speed/Stall Detection: Like above but measuring roller speed rather than motor current. 

A good understanding of [[Sensing Basics]] , and familiarity with our other sensors available will go a long way to making a robust intake.

## Robot + Game Piece Safety

When intaking objects, *many* things can go wrong with. Here's a quick list of considerations:
- A game piece can be oriented wrong, jamming the intake.
- A game piece can be loaded from weird angles or during unexpected motions, jamming the intake
- A game piece can be loaded at the edge of the intake, binding against mechanical supports
- A game piece load can be interrupted by drivers, leaving the intake in a "default command" state  while half-loaded
- A game piece can be damaged, loading in incorrectly or binding
- A game piece can be stretched/deformed inside the bot or intake
- A game piece can be smaller/larger than originally expected, causing various effects
- The game piece can be damaged due to excess force when stalled/jammed
- The game piece can be damaged due to initial contact with rollers at a high speed
- The intake can be extended past frame perimeter, and then slam into a wall
- The intake can be extend past the frame perimeter *into* a wall. 
- The rollers can jam against an intake pressed against a wall
- The rollers+game piece can force other systems into unexpected angles due to wall/floor/loading station interactions
- Intakes can fling past the robot perimeter during collisions, colliding with other robots (collecting fouls or snagging wires)

Murphy's law applies! If anything *can* go wrong, it *will*. When testing, you might be tempted to assume errors are a one-off, but instead record them and try to replicate errors as part of your testing routine. It's easier to fix at home than it is in the middle of a competition. 



When coding, pay close attention to 
- Expected mechanism currents: Make sure that your intake is operating with reasonable current limits, and set high enough to work quickly, but low enough to prevent harm when something goes wrong.
- Intake Default Commands: Ideally, the code for your intake should ensure the system ends up in a stable, safe resting location.
- The expected and actual positions: In some cases, if there's a large mismatch between expected positions and the current one, you might infer there's a wall/object in the way and halt loading, or apply a different routine.
- Command conditions: Make sure that your start/stop conditions work reliably under a variety of conditions, and not just the "values when testing". You might need to increase them or add additional states in which sensor values are handled differently or ignored.


## Fault Management

A good intake doesn't need to perfectly load a game piece in every scenario, but it does need to have a way to recover so you can resume the match. Let's look at a couple approaches

#### Load it anyway
The optimal result, of course, is successfully resolving the fault and loading the game piece. Several fault conditions can be resolved through a more complex routine. A couple common ones are 
- Stop + restart the intake (with motor coast mode). This works surprisingly well, as it lets the game piece settle, slip away from obstructions, and then another intake attempt might successfully complete the load operation.
- Reverse + restart the intake. A more complex (and finicky) option, but with the same effect. This an help alleviate many types of jamming, stretching, squishing, and errant game piece modes, as well as re-seat intake wheels.
- Re-orient + restart: This can come up if there's mechanical quirks, such as known collision or positional errors that can result in binding (like snagging on a bumper). Moving your system a bit might fix this, allowing a successful intake

A good load routine might need support from your mechanical teams to fix some edge cases. Get them involved!
#### Stall/Incomplete load 

If we can't fix it, let's not break it: This is a fault mitigation tactic, intended to preserve game piece and robot safety, and allow the robot to continue a match.

The *most* important part of this is to facilitate the drivers: They need an Eject button, that when held tries to clear a game piece from the system, putting it outside the frame perimeter. A good eject button might be a simple "roller goes backwards", but also might have more complex logic (positioning a system a specific way first) or even controlling other subsystems (such as feeders, indexers, or shooters). 

Historically, a good Eject button solves a *huge* amount of problems, with drivers quickly identifying various error cases, and resolving with a quick tap. Often drivers can tap eject to implement "load it anyway" solution, helping prove it on the field before it's programmed as a real solution.
#### Irrecoverable jam

The big oof 💀. When this happens, your robot is basically out of commission on the field, or your drivers are  slamming it against a wall to knock things loose.

In this case, you should be aiming to identify, replicate, and resolve the root cause. It's *very* likely that this requires mechanical teams to assist. 

If the jam is not able to be mechanically prevented, then Programming's job is to resolve the intake process to make it impossible, or at least convert it to a temporary stall. 


## Making A Solid Intake

Within the vast possibility space of the intake you'll handle, there's a few good practices

- Test early, test often, capture a lot of errors.
- Revise the hardware, then revise the software: Fix the software only as needed to keep things working. Don't spend the time keeping junked intake designs limping along, unless needed for further testing.
- Closed loop: Several fault conditions can be avoided by using a velocity PID and feed-forwards to generate a slower, more consistent initial interaction with game pieces, and automatically apply more power in fault condition.
- Operate at the edge cases: Do *not* baby the intake, and do your best to generate repeatable fault conditions to inform the design. 
- Operate in motion: Feeding the intake on a stationary chassis tends to feed differently than a mobile chassis and stationary game piece, or a mobile chassis + Mobile Game Piece. 

## Intakes + Other Subsystems

#### Interactions
Generally,  intakes are not one "system", but often actuated to deploy it beyond the frame perimeter, or line it up with intake positions. These are often done using [[SuperStructure Arm|Arms]] or [[SuperStructure Elevator|Elevators]]. In some cases, it's a deployable linkage using motors or [[Level 1/Pnuematics|Pnuematic Solenoids]] .

Intakes often also interact with gamepiece management systems, usually an [[Superstructure Indexer|Indexer/Passthrough]] that helps move the game piece through a scoring mechanism. In some systems, the intake *is* the scoring mechanism.

#### Naming
Regardless of the system setup, good [[Subsystems|Subsystem]] design remains, and the "Intake" name tends to be given to the end effector (usually [[SuperStructure Rollers|Rollers]]) contacting the game piece, with intake actuators being named appropriately (like IntakeArm or IntakeElevator)

#### Code structure and control

#todo