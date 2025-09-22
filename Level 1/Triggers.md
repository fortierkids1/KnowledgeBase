---
tags:
aliases:
  - Trigger
  - Controllers
---
## Synopsis

Triggers are a simple boolean condition that robot code can use to interact with commands, including starting and stopping them.


## Success Criteria
- [ ] Start a command when the robot is enabled, and ends automatically
- [ ] Create a Trigger with multiple required conditions before running a command
- [ ] Read a sensor input in a Trigger, and run a command when the sensor enters a true state.

#### Learning objectives
- Support command+trigger subsystem interfaces
- Model system state into binary regions
- loose coupling of subsystems
- Tolerances on sensors
- Joystick buttons = trigger ; Hidden common use case
- Starting commands with triggers
- ending commands with triggers
- sequencing component

## Controllers

The first, easiest introduction to Triggers is the `CommandXboxController` class, used to interact with the driver's joystick! 

Each button is a trigger, and a great learning reference. Each new project has some triggers for you, set up and ready to go!
```java
public class RobotContainer{
	private final CommandXboxController m_driverController =
	new CommandXboxController(OperatorConstants.kDriverControllerPort);
	//That constant equals 0  btw
	
	private void configureBindings() {
		m_driverController.b()
		.whileTrue(m_exampleSubsystem.exampleMethodCommand());
}
```

In this case, `m_driverController.b()` generates a new Trigger interacting with the B button on the controller. 
Here, we see that the Trigger uses `.whileTrue(command)` . This Trigger option 1) Starts the command when the button is pressed, and 2) Cancels the command when it's released. 

`whileTrue` tends to be a very good option for Drivers and controller inputs : it enables good muscle memory, and predictable human->robot interactions. Similarly, with no buttons being pressed, no commands are running: This means your robot is predictably running only `defaultCommands` and becomes easy to reason about as a programmer.



## Subsystems + Sensors

Triggers are a great way to wrap sensor and subsystem "state", and expose it to be integrated with Commands. 

While it's less common to launch commands based on these types of Triggers, Triggers provide several functions that prove useful, and help clean up your subsystem code. 

Common triggers include

- Confirming you're at a target position, usually written isOnTarget / isAtGoal / isAtSetpoint(), or something similar. 
- Confirming the state of a game piece: Usually isGamepieceLoaded, replacing Gamepiece with the name of the current year's item.
- isWithinRange() ; Common for rangefinders like [[LaserCan]]

Usually, these are further customized to provide very clear, yes/no queries with true/false responses.



## Exploring Trigger methods

For a full reference of available options, see here: 
https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj2/command/button/Trigger.html

The most helpful ones will tend to be  

`whileTrue`: Extremely useful for driver interactions
`onTrue` : Very good for automated sensor interactions, such as automatically starting intake processes.

`and` and `or` : Allow joining two existing Triggers. This can clean up some code, most notably writing `command.until(...)` conditions. 

`and` is also helpful for attaching multiple conditions to existing triggers, to further filter conditions.

`getAsBoolean` can be useful, as it simply returns the true/false value of the checked condition. This allows Triggers to be used as simple Boolean functions, or as boolean variables.

`debounce(time)` prevents the trigger from changing  state until the condition is in the new state for `time` seconds. This  prevents instantaneous changes caused by environmental errors, which is common in sensors. A small debounce can significantly improve reliability and reduce "misfires".
## Where do Triggers go?

Triggers are a bit strange; When they're created, they're included in the robot's Command Scheduler, and it's not needed to make sure they stay in scope to continue working. 

However, you *do* need to keep them in an accessible scope if you want other parts of the system to continue working. This leads to a few variants on where they go in your codebase.

The important detail is that a Trigger should only be *created* once, and not created in `execute` blocks or using Factory methods.
#### As a named subsystem class member
This style is useful for "interface" functions; Ones that will be used inside or outside of your subsystem to tell you something about it.

```java
public class ExampleSubsystem{
	public ExampleSubsystem(){
	}
	public Trigger isEncoderAround20 = new Trigger(
		()->encoder.get()>15 && encoder.get < 25
	)
}
```

#### As an anonymous subsystem trigger

If a command is just running automated actions, but won't be referenced, you can simply put it in a constructor.  This will schedule the command, and work invisibly in the background.
```java
public class ExampleSubsystem{
	public ExampleSubsystem(){
		new Trigger(DriverStation::isEnabled)
		.and(()->isHomed==false)
		.onTrue(goHome())
	}
```

(see also [[#Automatically launching commands at startup]] for more info about this use case)

#### In RobotContainer's initialization functions
Less common, but some Trigger checks interfacing with multiple subsystems may need to be created in RobotContainer.  Generally, we want to minimize these for single-subsystem interactions. 

Usually, the constructor is fine, but for complex robots, you might want to create a `configureTriggers()` utility function, just as the constructor has a `configureBindings` for joystick buttons.

```java
public class RobotContainer{
	public RobotContainer(){ //constructor
		new Trigger(DriverStation::isEnabled)
		.and(()->elevator.isHomed==false)
		.onTrue(elevator.goHome())
	}
```

This one even has an example in the code!
```java
public class RobotContainer{
	private void configureBindings() {
		// Schedule `ExampleCommand` when `exampleCondition` changes to `true`
		new Trigger(m_exampleSubsystem::exampleCondition)
		.onTrue(new ExampleCommand(m_exampleSubsystem));
	}
}
```
#### Uninitialized class member + Initialization
This is an unusual edge case; Sometimes, your Trigger will rely on something in your class that is not yet initialized. Because of this, you can't put it as a class member, since that would need to evaluate immediately. But you can't put it in the constructor, because that'd hide the scope, and you can't access it!

The solution is do both; We reserve the location in memory, allowing Java to process the class, and then come back and evaluate our Trigger when we construct the class. 

```java
public class ExampleSubsystem{
	public Trigger isEncoderAround20; //uninitialized!
	
	public ExampleSubsystem(){
		// We now initialize it 
		isEncoderAround20 = new Trigger(
			()->encoder.get()>15 && encoder.get < 25
		);
	}
	
}
```

In general we try to avoid this pattern; It often leads to forgotten initialization steps, and produces more noise and redundancy. However, it does resolve this particular problem.

## Surprises

#### Automatically launching commands at startup

On initially reviewing this example code, you might expect it to work as written when the robot starts up
```java
public class ExampleSubsystem{
	public ExampleSubsystem(){
		new Trigger(()->isHomed)==false).onTrue(goHome());
	}
	public Command goHome(){
		return run(()->/*Do a homing process*/);
	}
}
```

However, it fails for a very surprising reason, relating to the robot's boot process,and the fact that commands don't run when in Disabled Mode. The timeline of events is as indicated:

- The robot boots
- The ExampleSubsystem is created, and the trigger is registered with the [[Scheduler]]
- Eventually, initialization is complete
- The robot enters Disabled mode
- isHomed is  checked and false. 
- goHome is started, and immediately stopped.
- Eventually, the driver enables the robot
- The robot enters Enabled mode.
- isHomed is *still* false, rather than *becoming* false so nothing happens. 

What the heck!

The issue is that the command state is not considered for onTrue; Only the condition is. In some cases "homing" might deserve `.onTrue`; But other triggers (like automatic gamepiece alignment or adjustment) might interfere with other robot processes!

The trick is to explicitly check for the robot to be enabled first, and then your other condition afterward.

```java
public class ExampleSubsystem{
	public ExampleSubsystem(){
		new Trigger(DriverStation::isEnabled)
		.and(()->isHomed==false)
		.onTrue(goHome());
	}
	public Command goHome(){
		return run(()->/*Do a homing process*/);
	}
}
```

In this case, `DriverStation::isEnabled` will always fail until the robot is in a state where it *could* run a command. Only then will it check  the `isHomed` status, see the condition is true, and attempt to schedule the command. Success!

#### Playing nice with other commands

Scheduling some actions based on sensors might wind up interfering with other ongoing robot tasks. Remember, they're *exactly* like a driver hitting a button!

For example, if you automatically "grab" a loaded game piece, like this: 

```java
public class IntakeSubsystem extends SubsystemBase{
	public IntakeSubsystem(){
		new Trigger(isGamePieceLoaded).onTrue(holdGamePiece());
	}
	public Command holdGamePiece(){
		return run(()->motor.set(0.1));
	}
	
}
```

and then try to run a sequence like this:
```java
public class RobotContainer(){
	public Command fancyGrabGamepiece(){
		return new SequentialCommand(){
			elevator.goToBottom(),
			intake.intake(), //<-- This causes your trigger to run! 
			//Command will get cancelled here
			elevator.goToScoringPosition() //won't run
		}
	}
}
```

You will be annoyed by the unexpected behavior. Worse, if there's a sensor glitch, you might cancel scoring operations too! Instead of getting points, you just suck the game piece back in. 

In cases where such conflicts might come up, you can detect if a Command requires the subystem, by adding some conditions to the trigger, preventing unexpected launches during other actions. 

```java
	//Check to see if *any* command is scheduled.
	new Trigger(isGamePieceLoaded)
	.and(this.getCurrentCommand()==null) //Null returned if no command running
	.onTrue(holdGamePiece());

	//Check to see if the current command is "default"
	// We probably don't mind interrupting the default.
	new Trigger(isGamePieceLoaded)
	.and(this.getCurrentCommand()==this.getDefaultCommand())
	.onTrue(holdGamePiece());
	//Note, if you have no defaultCommand getDefaultCommand() will 
	//return null!
```

These methods allow your trigger to intentionally set its own priority below other commands that might be interacting with the system. 

However, in some cases you might simply be better off using an intelligent DefaultCommand for such automated actions, but this is a useful tool to be aware of!


> [!BUG] Test this behavior
> While these specific Trigger techniques been used successfully in the past, these examples are not written from older, validated code and may contain errors. 
> Trigger/command behavior might also be changing in 2025, rendering these obsolete or working differently.
