## Success Criteria
- [ ] Examine a robot design
- [ ] Generate a design plan indicating the breakdown

Recommended: 
- [[Subsystems]]
- [[Commands]]
- [[Triggers]]
- Cursory check of the available subsystem types

## General Analysis Process
This guide runs through how to examine a robot design, analyze the mechanics, game piece path, and form a plan to generate a code structure to control the system.

#### Track the Game Piece Flow
For an FRC bot, "move the game piece" is the fundamental design objective, and serves as a great way to step through the bot from a design process. 

If you start at "need to obtain a game piece" and then proceed through game play cycles, you'll naturally follow a good analysis process, and observe the various handoffs and subsystem interactions. 

The game piece flow often gives an intuitive sense for "forward" direction in many systems where it might be ambiguous. "intake -> score" as positive often provides a streamlined notation to make testing and bringup of systems a bit more consistent.

#### Quick breakdown of mechanism classes

Being able to identify basic mechanisms is key to being able to model a robot in code. This non-exhaustive list should help provide some vocabulary for the analysis on typical bots.

[[SuperStructure Rollers|Rollers]] The simplest mechanical system: A motor and a shaft that spins. 
[[SuperStructure Flywheel|Flywheel]] A specialized Roller system with extra weight, intended to maintain a speed when launching objects.
[[Superstructure Indexer|Indexer]] A mechanism to precisely align, prepare, or track game pieces internally in the robot. Often a Roller, but can be very complex.
[[Superstructure Shooter|Shooter]] A compound system, usually consisting of at least a Flywheel and an Indexer, and sometimes an Arm or other aiming structure.
[[SuperStructure Intake|Intake]] A specialized compound system intended for getting new game pieces into the robot. Generally consists of a Roller, often with another positioning mechanism like an Arm. 
[[SuperStructure Arm|Arm]] A system that rotates around a pivot point. Usually positions another subsystem. 
[[SuperStructure Elevator|Elevator]] A system that travels along a linear track of some sort. Typically up/down, hence the name.
[[Swerve Basics|Swerve Drive]] or [[Differential Drive]]: Makes robot go whee along the ground.

The systems in your robot will often take these basic archetypes and rename them to something else, and in some cases combine them into compound mechanisms.

Step 1 is break down the robot into component mechanisms, and follow up how they link to eachother. 

#### Identifying forward notations

As you're breaking down your system, it's necessary to define what values correlate to a "positive" motion. This is *not* as trivial as it sounds for some bots, but is necessary to ensure the team can refer to the motions and directions with the same language.
- Elevator style systems go up, so up -> Positive
- Arms should follow math convention of anti-clockwise-> Positive. It's easiest to pair 0 and "horizontal" 
- Drivetrains seem obvious: Positive means forwards. However, this assumes you know "what side is the front": If you have intakes and scoring on different sides, this usually requires discussing it with the designers to get everyone in agreement. 
- Intake,  Indexer, and scoring mechanisms can be trickier. Often, the simplest route is to consider "intaking direction positive", which often defines all systems at once. 

By doing all this, it becomes much easier for hop between subsystem bringups and diagnose motor and encoder configurations later.


#### Identify key states and transitions 

As you track game piece flow, you will see natural states where the robot has inventory change (acquiring/losing game pieces), or changes posture (extending/retracting intakes, raising/lowering elevators, etc).

As the robot moves between states (such as unloaded, loading, loaded, preparing to score, scoring), keep an eye on both the states and the transitions to ensure that things go smoothly.

#### Sensors

In a few places, you'll probably see certain state transitions that are a bit less clear, or rely on information the robot may not have. 

The classic example is the intake: If your robot transitions from unloaded->loaded , you may need to shut off the intake right away to prevent intaking excess game pieces, getting fouls and jams. However, if there's no good detection method, you may not be able to quickly trigger a shutoff.  This often requires a supplemental sensor, and additional handling logic.

Watch your state transitions for places where a sensor provides significant benefits in robustness, efficiency, or aiding driver control. If so, consider adding sensors, and working with mechanical teams to facilitate placement.

#### Constraints + Conflicts

Most FRC mechanisms have physical limits that must be coded. These are often trivial to document, but notable.

While often avoided in bot designs,in some cases multiple systems will attempt to occupy the same physical space. This is clearly a problem, and one that needs careful consideration. In some cases these can be trivial handled, but in other cases the entire robot code base must plan around it.

A simple collision fix is a lockout: Say a grabber is mounted on the elevator, and when extended, it collides with the intake. Therefore, we define 2 lockouts: If elevator is below X, the grabber cannot extend. If the grabber is extended, then it cannot go below X. The two systems are intertwined, but have a lockout system that helps manage their motions.

For more complex cases, simple lockouts don't work, and systems must follow a more complex series of state transitions, forming a [[State Machines|State Machine]]. We often avoid programming our robots with explicit super-structure state machines, but it's a common [[Code Patterns|pattern]] in FRC for designs with significant collision potential or complex motion patterns.

Identifying these issues early helps you plan your overall code structure.

#### Critical Code tasks

Based on the above, you can identify certain code features that the robot *must* have to accomplish game tasks, do self-management, and otherwise hit your goals. These are general things like "Aim at the goal", "shoot a game piece", "extend up to scoring position", etc. High level overviews of the primary things you're doing.

For an example of this stage, see the case study below.

There's also few standard house-keeping tasks due to system constraints or evergreen rules:
- The robot must have suitable methods to home or acquire correct internal system states
- Robots typically have an initial starting in-frame configuration, which may require special handling
- Manage the disable/re-enable transition (which may or may not be trivial depending on bot) 


#### Define a viable driver control scheme

With the mechanics, code, and game piece flow understood, it's worth considering what the driver interface will resemble. Once again iterate through the full game piece flow from floor to score, as well as any endgame or alignment tasks.

As you do this, look for button sequences that seem problematic: Notably button sequences that always have to go in a fixed sequence, buttons that might misbehave when loaded/unloaded or in certain bot states, or "combo" multi-button presses that put undue stress on the driver. Your goal is to have each input be unambiguous and safe.

In practice, each button will likely control a small sequence of robot actions and safety checks; This particular analysis step is intended to facilitate identifying any missing features, sensors, or processes that would make robust control challenging. 

In some cases, we also need to consider the amount of available buttons: It may need to be the cases that buttons are "modal", and do two different things depending on what "mode" the robot is in. Common modal states are "which game piece is loaded", or "if X is deployed we're doing Y now". Be careful here: Any mode switches need to be very obvious to drivers, and we want to facilitate muscle memory as much as possible.

Remember to leave buttons for "error correction actions"! You'll always need at least one button to "reset" the robot, clear jams, or eject badly loaded game pieces. 

The gold standard of inputs is a "win button"; The driver starts the match, hits a button, then wins. While this is not attainable in practice, we can split a complex game into smaller tasks that can be accomplished and managed by a single button; This lets the drivers focus on the higher level match strategy rather than manage the robot's interactions.

Note that your initial input analysis will not be final! Drivers will have their own preferences, and later considerations will pop up. This step is a sanity check.

#### Plan a Tier 2 Auto

Next is to work toward a relatively complex auto. This is typically one that at scores one game piece, and acquires+scores a second. 

Unlike a Tier 1 "get the robot on the field" auto which is "score a piece and stop", a Tier 2 auto requires consideration for re-alignment, odometry, and re-acquisition.  Even if we never build this auto, putting these details in our roadmap early on can help guide our code toward a solution that *will* work. If we do not do this, it's easy to lock ourselves into a Tier 1 auto that we cannot extend easily. When given a choice on how to code our autos, we want to ensure that a useful auto can be the foundation for a more complex one.

Note that implementing the auto indicated here is not a requirement: Considering *how* we can make it happen and what supporting infrastructure is needed is the valuable part at this stage.
#### CODE!

We're now good to code! At this time you can 
- Plan out your subsystems, knowing how they interact and should be split.
- Plan out your subsystem APIs and interfaces
- Plan any lockout management
- rough out what your button sequences should be
- Get a rudimentary auto to serve as a starting point for more complex ones

The final step of analysis and prep is to "stub" your subsystems: EG, writing all the functions and signatures (parameters+return values) , but not actually putting in the code that makes them work. 

The goal of this step is to ensure that higher level work (like buttons and autos) can be scripted out in advance. This means  code like `intake.intake()` might not work, but it can still be put into code and left in place until it does.




# Case Study: 2024 Crescendo Bot


![[2024-Crescendo-Reveal.mkv]]

[Crescendo Bot Code](https://github.com/stormbots/Crescendo/) 
`[!!info|Note|var(--color-blue-rgb)]` The actual code for this bot may differ from this breakdown; This is based on the initial design provided, not the final version after testing. 

> [!NOTE] Game info
> The game piece for this game is an 2" tall, 14" diameter orange donut called a "note", and will be  referenced throughout this breakdown.
> There are two note scoring objectives: Shooting into an open "speaker" slot about 8' high, or placing into an "amp", which is a 2" wide slot about 24" off the ground.
> Lastly, climbing  is an end game challenge, with an additional challenge of the "trap", which is effectively score an amp while performing a climb. 
#### Mechanism Breakdown 
For this, we'll start with the game piece (note) path, and just take note of what control opportunities we have along this path. 

The note starts on the floor, and hits the under-bumper intake. This is a winding set of linked rollers driven by a single motor. This system has rigid control of the note, ensuring a "touch it own it" control path.

Indexer: The  game piece is then handed off to an indexer (or "passthrough"). This system is two rollers + motors above and below the note path, and have a light hold on the note; Just enough to move it, but not enough to fight other systems for physical control.

Flywheel: The next in line is a Flywheel system responsible for shooting. This consists of two motors (above and below the note's travel path), and the rollers that make physical contact. When shooting, this is the end of game piece path. This has a firm grip to impart significant momentum quickly.

Dunkarm + DunkarmRollers: When amp scoring/placing notes, we instead hand off to the rollers in front of the shooter. These rollers are mounted on an arm, which move the rollers out of the way of the shooter, or can move it upward. 

Shooter: The Indexer and Shooter are mounted on a pivoting arm, which we denote as the shooter. This allows us to set the note angle.

Climber: The final mechanism is the climber. There's two climber arms, each with their own motor.

#### Sensing

The indexer has a single [[LaserCan]] rangefinder, located a just before the shooter. This will allow detection of a game piece being loaded or not, and with some math, the precise positioning of the game piece.
#### Constraints + Conflicts
Again let's follow the standard game piece and flow path. 

- Intake+Indexer interaction: A note can be held by both Intake and Indexer simultaneously. In this case, the intake exerts control.
- Intake + Shooter : If the shooter angle is too high, note transfer to the indexer will fail. 
- Indexer + Flywheel: A note can be held by both intake and indexer simultaneously. Again, the shooter wins, but fighting the indexer would impact accuracy/momentum.
- Flywheel + Dunkarms: A note getting passed into the dunkarm rollers is held more strongly by the shooter than the dunkarm rollers. This can only be done at a fixed angle combination.
- Climber + Dunkarms. When climbing without trap scoring, the chain will come down on the dunkarms. The climber will win with catastrophic damage. 
- Climber + Shooter: When trap scoring, the dunk arms are out  of the way, but the chains will come down on the shooter.
- Dunkarm and shooter: The rollers can potentially be in the way of the shot path. This can occur at low shot angles, or if the dunkarm is moving down after other scoring.

This seems to be about it for conflicts between control systems

We should  also do a quick check of the hard stops; These serve as reference points and physical constraints.
- Dunkarms have a lower hard stop. It has no upper hard stop, but eventually rotates too far and breaks wiring.
- Shooter has a bottom hard stop. It has a upper end of travel, but no physical stop.
- Climber has a bottom hard stop, and a upper end of travel. In both cases, high torque movement will cause damage when ran into.
- All other systems are rollers, with no hard or soft stops. 

#### Critical code tasks
Before getting into how the code is structured, let's decide what the code should be doing during normal gameplay cycles

- Intake note: This will be running the intake, feeding the note into the indexer. Since running too far will feed it into the shooter, we can use our indexer sensor to end the process. 
- Shot preparation: Before shooting a note, we need to make sure the note is not pushed into the flywheels. Once that's done, we need to get to the target angle and speed. 
- Shooting: The indexer needs to feed the note into the flywheel.
- Score amp: This takes a note held in the dunk arm rollers, rotate up, and then rotate the rollers to score it. 
- Load dunkarm rollers: This requires the dunkarm + shooter to be in the desired lineup, then the indexer feeds the note into the shooter, which in turn feeds it into the dunk arm rollers. The rollers must be capable of stopping/managing the note to prevent it from falling out during this process.
- Climbing,  no trap: Climber goes up, climber comes down, and doesn't crush the dunkarms.
- Scoring Trap: This requires maneuvering _around_ the chain to get the dunkarms in position. From there, it's just climbing, then amp scoring. 


#### Code Breakdown
We can now start looking at how to structure the code to make this robot happen. Having a good understanding of [[Commands|Command]] flow helps here, but is not required.

We'll start with subsystems breakdowns. Based on the prior work, we know there's lots of loose coupling: Several subsystems are needed for multiple different actions, but nothing is strongly linked.  The easy ones are:
- Intake (1 motor)
- Indexer (2 motors, the top and bottom)
- Shooter (the pivot motor)
- Flywheels (the two motors, top and bottom)
- Climber (two motors, left and right)
This allows commands to  pair/link actions, or allow independent responses. 

The Dunkarm + Dunkarm rollers is less clear. From an automation perspective, we could probably combine these. But the humans will want to have separate buttons for "put arm in position" and "score the note". To avoid command sequence conflicts, we'd want these separate.
- Dunkarm (1 motor, the pivot)
- Dunkarm Rollers (1 motor for the roller pair)

Next we define what the external Command API for each subsystem should look like so we can manipulate them.
- Intake:
	- Intake command to pull in a note. 
	- Eject: In case we have to get rid of a note
- Flywheel:
	- Shoot note. Would need the appropriate RPM, which may be constant, or vary based on vision/sensor data. 
	- Pass to dunk arm. This is likely just running at a target RPM, but may use other logic/control routines.
	- Retract note: In case something goes wrong, perhaps we want to pull the note in and clear the shooter for a reset.
	- isAtTargetRPM check, which is critical for sequencing
- Shooter Pivot
	- SetAngle 
	- isAtTargetPosition for sequencing
- Dunkarm:
	- Set Angle. Would just need an angle reference.
	- isAtTargetAngle check for sequencing
	- Manual positioning: The human may  control this directly for trap score line up
- Dunkarm Rollers:
	- Load, which is likely just a speed/power suitable for controlled intaking from the robot
	- Score Amp, another speed/power appropriate for this task
	- Score Trap, Another speed/power for the task
	- drop/eject, just get rid of a note if something went wrong.
	- getPosition, since certain tasks might require specific positions
- Climber: 
	- set height: Basically the only job of this system is go up/go down
	- Is At Target Height check. Maybe useful for sequencing
- Indexer
	- load/intake: For working with the intake to load notes
	- hasNote check; This is required for "end of intake"
	- feedShooter: Feed a held note into the flywheels as part of the shooting process


With this, we now have the structures in place to start asserting how we'd define more complex actions and sequencing.

#### Game Piece Cycle

Before we *really* dive into writing all our robot code it's now, it's helpful to walk through a basic game piece structure, and make a couple "high level code" sequences to verify that you *can* in fact do the things you want to do. Often you'll catch missing details this way. 

 This will use the [[Commands|Command]] and [[Triggers|Trigger]] syntax, but it should be fairly readable without specific knowledge.

We know from prior analysis what our intake process looks like: Run the intake, feed it into the indexer, then stop when it hits the indexer's sensor. This should look something like this:

```java
Commands.parallel(
	intake.intake(),
	indexer.intake()
).until(indexer.hasNote());
```

Once we have a note, we then have to score it. For now, we'll simply assume a fixed position on the field (which is what we did at this bot's first competition!)

```java
Commands.sequence(
	Commands.parallel(
		flywheel.setRPM(3000),
		pivot.setAngle(60)
	).until(pivot.isAtTarget().and(flywheel.isAtTargetRPM())),
	
	Commands.parallel(
		flywheel.setRPM(3000),
		pivot.setAngle(60),
		indexer.feedShooter(),
	).withTimeout(500)
)
```

And, now we can actually score. This code isn't ideal (we have duplicated sections and magic numbers), but that comes later. We're in proof of concept stage.

If we followed through with this exercise for the other [[#Critical code tasks]] noted above, we could fully validate all parts of our system to make sure we've covered all necessary command and trigger interfaces.

#### Further refinements
With this, we've somewhat defined have confirmed our game plan for the bot code breakdown works, and provides us with the work Subsystem program

Be aware that "simple" routines like this might sometimes have surprising interactions mechanically. Indexers (or Passthroughs) are full of these. In this bot, the actual code's shoot sequence actually uses the intake too, since the note would often snag there, causing shot failures. 
Similarly, intaking initially required running the flywheels; We had to stop them or the note would catch, going from an intake process to an intake-and-immediately-shoot process!