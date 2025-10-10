---
tags:
aliases:
  - Rollers
  - Roller
---

Requires:
[[Motor Control]]

Recommends:
[[FeedForwards]]
[[PID]]

## Success Criteria
- [ ] Create a Roller serving as a simple intake
- [ ] Create Commands for loading, ejecting, and stopping
- [ ] Create a default command that stops the subsystem
- [ ] Bind the load and eject operations to a joystick

Optional Bonus criteria: 
- [ ] Configure the Roller to use RPM instead of setPower
- [ ] Add a FeedForward and basic PID
- [ ] Confirm the roller roughly maintains the target RPM when intaking/ejecting

## Synopsis
A Roller system is a simple actuator type: A single motor output mechanically connected to a rotating shaft. Objects contacting the shaft forms the useful motion of a Roller system. This can also extended with additional shafts, belts, or gearing to adjust the contact range.

Despite the simplicity, Rollers are very flexible and useful systems in FRC. When paired with clever mechanical design, Rollers can accomplish a wide variety of game tasks, empower other system types, and serve as the foundation of more complex systems.

## Common use cases

On their own, rollers can be designed to serve a few functions 
- Fixed position [[SuperStructure Intake|Intakes]], responsible for pulling objects into a robot
- Simple scoring mechanisms for ejecting a game piece from the bot
- Simple launchers game pieces from the robot at higher speeds
- As motion systems to move game pieces through the bot
- As a simple feeder system for allowing or blocking game piece motion within the bot.

Rollers of this nature are very common on Kitbot designs, owing to the mechanical simplicity and robustness of these systems. 

For more complex bots, Roller systems are usually modified with extra mechanical features or actuated. These include
- A [[SuperStructure Flywheel|Flywheel]] system, which provides more accurate launching of game pieces through precision control and increased momentum
- As [[Superstructure Indexer|Indexers/Feeder/Passthrough]], providing precision control of game pieces through a bot.
- As actuated [[SuperStructure Intake|Intakes]], often with rollers attached to [[SuperStructure Arm|Arms]] or linkages
These documents discuss the special considerations for improving Rollers in those applications.


## Implementing a Roller

#### Analysis
In keeping with a Roller being a simple system, they're simple to implement: You've already done this with [[Motor Control]].

To integrate a Roller system into your bot code, there's simply a few additional considerations:
- The function of your roller: This determines the role and name of your system. Like all subsystems, you want the name to be reflective of it's task, and unique to help clarify discussion around the system. 
- Level of control needed: Rollers often start simple and grow in complexity, which is generally preferred. But, sometimes you can *tell* that your system will get complicated, and it's worth planning for. 
- The base tasks this system performs. 
- *sometimes* a Roller system will serve multiple roles: Usually, it's good to recognize this early to facilitate naming and configuration.

These are effectively what we see in [[Robot Design Analysis]] , but especially pertinent for Roller systems. Since your system will likely have multiple rollers, naming them "Rollers" is probably a bad idea. Assigning good names to the roller system and the actions it performs will make your code easier to follow.

Rollers generally will have a few simple actions, mostly setting a power and direction, with appropriate names depending on the intent of the action:
- Intake rollers usually have "intake", "eject", and "stop" which apply a fixed motor power. Larger game pieces might also have a "hold", which applies a lower power to keep things from shifting or falling out.
- Rollers in a Launcher role will usually have "shoot" and "stop", and rarely need to do much else.  
- Rollers serving as a "feeder" will usually alternate between the "launcher" and "intake" roles; So it'll need appropriate actions for both.

#### Code Structure

Appropriately, a useful, minimal roller system is very straightforward, and done previously in [[Motor Control]]. But this time let's clean up names.

```java
public Launcher extends SubsystemBase{
	SparkMax motor = new SparkMax(42,kBrushless);

	Launcher(){
		//Normal constructor tasks
		//Configure motor: See Motor Control for example code
		//Set the default command; In this case, just power down the roller
		setDefaultCommand(setPower(0)); //By default, stop
	}
	public Command setPower(double power){
		// Note use of a command that requires the subsystem by default
		// and does not exit
		return run(()->motor.set(power));
	}
	public Command launchNear(){
		return setPower(0.5);
	}
	public Command launchFar(){
		return setPower(1);
	}
}
```

For most Roller systems, you'll want to keep a similar style of code structure: By having a `setPower(...)` command [[Code Patterns|Factory]], you can quickly build out your other verbs with minimal hassle. This also allows easier testing, in case you have to do something like attach a [[Joysticks|Joystick]] to figure out the *exact* right power for a particular task. 

In general, representing actions using named command factories with no arguments is preferable, and will provide the cleanest code base.  The alternatives such as making programmers remember the right number, or feeding constants into setPower will result in much more syntax and likelyhood of errors.

Having dedicated command factories also provides a cleaner step into modifying logic for certain actions. Sometimes later you'll need to convert a simple task into a short sequence, and this convention allows that to be done easily.


## Boosting Roller capability

Without straying too far from a "simple" subsystem, there's still a bit we can do to resolve problems, prevent damage, or streamline our code.

#### FeedForwards and PIDs for consistent, error free motion

Sometimes with Roller systems, you'll notice that the power needed to *move* a game piece often provides undesirable effects when initially *contacting* a game piece. Or, that sometimes a game piece loads wrong, jams, and the normal power setting won't move it.

This is a classic case where error correcting methods like [[PID]]s shine! By switching your roller from a "set power" configuration to a "set rotational speed" one, you can have your rollers run at a consistent speed, adjusting the power necessary to keep things moving. 

Notably though, PIDs for rollers are very annoying to dial in, owing to the fact that they behave *very* differently when loaded and unloaded, and even more so when they need to actually resolve an error condition like a jam! 
The use of a [[FeedForwards|FeedForward]] aids this significantly: Feedforward values for rollers are extremely easy to calculate, and can model the roller in an unloaded, normal state. This allows you to operate "unloaded", with nearly zero error. When operating with very low error, your PID will be much easier to deal with, and much more forgiving to larger values.

You can then tune the P gain of your PID such that your system behaves as expected when loaded with your game piece. If the FF+P alone won't resolve a jamming issue, but more power will, you can add an I gain until that helps push things through.

#### Sensors + Automated actions

Some Roller actions can be improved through the use of [[Sensing Basics|Sensors]], which generally detect the game piece directly. This helps rollers know when they can stop and end sequences, or select one of two actions to perform. 

However, it is possible (although sometimes a bit tricky) to read the motor controller's built in sensors: Often the Current draw and encoder velocity. When attempting this, it's recommended to use [[Triggers|Trigger]] with a Debounce operation, which provides a much cleaner signal than directly reading these 

You can also read the controller _position_ too! Sometimes this requires a physical reference (effectively [[Homing Sequences|Homing]] a game piece), which allows you to assert the precise game piece location in the bot. 
In other cases you can make assertions solely from relative motion: Such as asserting if the roller rotated 5 times, it's no longer physically possible to still be holding a game piece, so it's successfully ejected.
#### Default Commands handling states

Many Roller systems, particularly intakes will wind up with in one of two states: Loaded or Unloaded, with each requiring a separate conditional action.

The defaultCommand of a Roller system is a great place to use this, using the [[Commands|Command]] utility `ConditionalCommand` (aka`either` ). A common case is to apply a "hold" operation when a game piece is loaded, but stop rollers if not.  

Implemented this way, you can usually avoid more complex [[State Machines]], and streamline a great deal of code within other sequences interacting with your roller.

#### Power Constraints + Current Limiting

Some Roller systems will pull objects in, where the object hits a hard stop. This is most common on intakes. In all cases, you want to constrain the power such that nothing gets *damaged* when the roller pulls a game piece in and stalls.

Beyond that, in some cases you can set the current *very* low, and replace explicit `hold()` actions and sensors with just this lower output current. You simply run the intake normally, letting the motor controller apply an appropriate output current.

This is not common, but can be useful to streamline some bots, especially with drivers that simply want to hold the intake button to hold a game piece.

#### Fault Detection
Should jams be possible in your roller system, encoder velocity and output current can be useful references, when combined with the consideration that 

When a "jam" occurs, you can typicaly note 
- A high commanded power
- A high output current
- A low velocity

```java
//Current detection
new Trigger(()->motor.getAppliedOutput()>=.7 && motor.getOutputCurrent()>4).debounce(0.2);
//Speed Detection
new Trigger(()->motor.getAppliedOutput()>=.7 && motor.getEncoder().getVelocity()<300).debounce(0.2);
```

However, care should be taken to ensure that these do not also catch the _spin up time_ for motors! When a motor transitions from rest to  high speed, it *also* generates significant current, a low speed, and high commanded output.

Debouncing the trigger not only helps clean up the output signal, but for many simple Roller systems, they spin up quickly enough that the debounce time can simply be set higher than the spin up duration.