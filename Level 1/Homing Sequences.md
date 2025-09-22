---
aliases:
  - Homing
---

Homing is the process of recovering physical system positions, typically using relative encoders.

#### Part of: 
[[SuperStructure Arm]]
[[SuperStructure Elevator]]
And will generally be done after most requirements for those systems
## Success Criteria
- [ ] Home a subsystem using a Command-oriented method
- [ ] Home a subsystem using a state-based method
- [ ] Make a non-homed system refuse non-homing command operations
- [ ] Document the "expected startup configuration" of your robot, and how the homing sequence resolves potential issues.
#### Lesson Plan
- Configure encoders and other system configurations
- Construct a Command that homes the system
- Create a Trigger to represent if the system is homed or not
- Determine the best way to integrate the homing operation. This can be 
	  - Initial one-off sequence on enable
	  - As a blocking operation when attempting to command the system
	  - As a default command with a Conditional Command
	  - Idle re-homing (eg, correcting for slipped belts when system is not in use)
  - 

## Success Criteria
- [ ] Home an elevator system using system current
- [ ] home an arm system using system current
- [ ] Home a system 

## What is Homing?

When a system is booted using [[Encoder Basics|Relative Encoders]], the encoder boots with a value of 0, like you'd expect. However, the real physical system can be anywhere in it's normal range of travel, and the bot has no way to know the difference.

Homing is the process of reconciling this difference, this allowing your code to assert a known physical position, regardless of what position it was in when the system booted. 

### To Home or not to home

Homing is *not* a hard requirement of [[SuperStructure Elevator|Elevator]] or [[SuperStructure Arm|Arm]] systems. As long as you boot your systems in known, consistent states, you operate without issue.

However, homing is generally recommended, as it provides benefits and safeguards

- You don't need strict power-on procedures. This is helpful at practices when the bot will be power cycled and get new uploaded code regularly. 
- Power loss protection: If the bot loses power during a match, you just lose time when re-homing; You don't lose full control of the bot, or worse, cause serious damage.
- Improved precision: Homing the system via code ensures that the system is always set to the same starting position. 


## Homing Methods

> [!INFO] Hard stops
> When looking at homing, the concept of a "Hard Stop" will come up a lot. A hard stop is simply a physical constraint at the end of a system's travel, that you can reliably anticipate the robot hitting without causing system damage. 
> In some bot designs, hard stops are free. In other designs, hard stops require some specific engineering design. 


> [!WARNING] Safety first!
> Any un-homed system has potential to perform in unexpected ways, potentially causing damage to itself or it's surroundings.
> We'll gloss over this for now, but make sure to set safe motor current constraints by default, and only enable full power when homing is complete.



### No homing + strict booting process.

With this method, the consistency comes from the physical reset of the robot when first powering on the robot. Humans must physically set all non-homing mechanisms, then power the robot. 

From here, you can do anything  you would normally do, and the robot knows where it is. 

This method is often "good enough", especially for testing or initial bringup. For some robots, gravity makes it difficult to boot the robot outside of the expected condition.

> [!BUG] Watch your resets!
> With this method, make sure your code does _not_ reset encoder positions when initializing. 
> If you do, code resets or power loss will cause a de-sync between the booted position and the operational one. You have to trust the motor controller + encoder to retain positional accuracy. 


### Current Detection

Current detection is a very common, and reliable method within FRC. With this method, you drive the system toward a hard stop, and monitor the system current.

When the system hits the hard stop, the load on your system increases, requiring more power. This can be detected by polling for the motor current. When your system exceeds a specific current for a long enough time, you can assert that your system is homed!

### Velocity Detection

Speed Detection works by watching the encoder's velocity. You expect that when you hit the hard stop, the velocity should be zero, and go from there. However, there's some surprises that make this more challenging than current detection. 

Velocity measurements can be very noisy, so using a [filter](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/filters/index.html) is generally required.

This method also suffers from the simple fact that the system velocity will be zero when homing starts. And zero is also the speed you're looking for as an end condition. You also cannot guarantee that the system speed ever increases above zero, as it can start against the hard stop. 
 As such, you can't do a simple check, but need to monitor the speed for long enough to assert that the system _should_ have moved if it was able to. 

### Limit Switches

Limit switches are a tried and true method in many systems. You simply place a physical switch at the end of travel; When the bot hits the end of travel, you know where it is.

> [!WARNING] Mechanical Robustness Required
> Limit switches require notable care on the design and wiring to ensure that the system reliably contacts the switch in the manner needed.

The apparent simplicity of a limit switch hides several design and mounting considerations. In an FRC environment, some of these are surprisingly tricky.
- A limit switch _must not_ act as an end stop. Simply put, they're not robust enough to sustain impacts and will fail, leaving your system in an uncontrolled, downward driving stage. 
- A limit switch _must_ be triggered at the end of travel; Otherwise, it's possible to start _below_ the switch. 
- A switch must have a consistent "throw" ; It should trip at the same location every time. Certain triggering mechanisms and arms can cause problems.
- If the hard stop moves or is adjusted, the switch will be exposed for damage, and/or result in other issues

Because of these challenges, limit switches in FRC tend to be used in niche applications, where use of hard stops is restricted. One such case is screw-driven [[Linear Actuators]], which generate enormous amounts of force at very low currents, but are very slow and easy to mount things to.

Switches also come in multiple types, which can impact the ease of design. In many cases, a magnetic hall effect sensor is optimal, as it's non-contact, and easy to mount alongside a hard stop to prevent overshoot.

Most 3D printers use limit switches, allowing for very good demonstrations of the routines needed to make these work with high precision.

For designs where hard stops are not possible, consider a Roller Arm Limit Switch and run it against a CAM. This configuration allows the switch to be mounted out of the line of motion, but with an extended throw. 

![[limit-switch-cam.svg]]


### Index Switches

Index switches work similarly to Limit Switches, but the expectation is that they're in the middle of the travel, rather than at the end of travel. This makes them unsuitable as a solo homing method, but useful as an auxiliary one. 

Index switches are best used in situations where other homing routines would simply take too long, but you have sufficient knowledge to know that it _should_ hit the switch in most cases. 
This can often come up in [[SuperStructure Elevator|Elevator]] systems where the robot starting configuration puts the carriage far away from the nearest limit. 

In this configuration, use of a non-contact switch is generally preferred, although a roller-arm switch and a cam can work well.

### Absolute Position Sensors

In some cases we can use absolute sensors such as [[Absolute Encoders]], [[Gyro Sensing|Gyros]], or [[LaserCan|Range Finders]] to directly detect information about the robot state, and feed that information into our other sensors. 

This method works very effectively on [[SuperStructure Arm|Arm]] based systems; [[Absolute Encoders]] on an output shaft provide a 1:1 system state for almost all mechanical designs. 

Elevator systems can also use these routines using [[LaserCan||Range Finders]] , detecting the distance between the carriage and end of travel. 

Clever designers can also use [[Absolute Encoders]] for elevators in a few ways 
- You can simply assert a position within a narrow range of travel
- You can gear the encoder to have a lower resolution across the full range of travel. Many encoders have enough precision that this is perfectly fine.
- You can use multiple absolute encoders to combine the above global + local states


For a typical system using Spark motors and Through Bore Encoders, it looks like this:

```java
public class ExampleSubsystem{
	SparkMax motor = new Sparkmax(/*......*/);
	ExampleSubsystem(){
		SparkBaseConfig config = new SparkMaxConfig();
		//Configure the motor's encoders to use the same real-world unit
		armMotor.configure(config,/***/);
		
		//We can now compare the values directly, and initialize the 
		//Relative encoder state from the absolute sensor.
		var angle = motor.getAbsoluteEncoder.getPosition();
		motor.getEncoder.setPosition(angle);
	}
}
```


### Time based homing

A relatively simple routine, but just running your system with a known minimum power for a set length of time can ensure the system gets into a known position. After the time, you can reset the encoder. 

This method is very situational. It should only be used in situations where you have a solid understanding of the system mechanics, and know that the system will not encounter damage when ran for a set length of time. 

### Backlash-compensated homing

In some cases you might be able to find the system home state (using gravity or another method), but find [[Mechanical Backlash|backlash]] is preventing you from hitting desired consistency and reliability.

This is most likely to be needed on [[SuperStructure Arm|Arm]] systems, particularly actuated [[Superstructure Shooter|Shooter]] systems. This is akin to a "calibration" as much as it is homing.

In these cases, homing routines will tend to find the absolute position by driving downward toward a hard stop. In doing so, this applies drive train tension toward the down direction. However, during normal operation, the drive train tension will be upward, against gravity. 

This gives a small, but potentially significant difference between the "zero" detected by the sensor, and the "zero" you actually want. Notably, this value is not a consistent value, and wear over the life of the robot can impact it.

Similarly, in "no-homing" scenarios where you have gravity assertion, the backlash tension is effectively randomized. 

To resolve this, backlash compensation then needs to run to apply tension "upward" before fully asserting a fully defined system state. This is a scenario where a time-based operation is suitable, as it's a fast operation, from a known state. The power applied should also be small, ideally a large value that won't cause actual motion away from your hard stop (meaning, at/below [[FeedForwards|kS+kG]] ).

For an implementation of this, see [CalibrateShooter](https://github.com/stormbots/Crescendo/blob/main/src/main/java/frc/robot/commands/CalibrateShooter.java) from Crescendo.


### Online position recovery
Nominally, homing a robot is done once at first run, and from there you know the position. However, sometimes the robot has known mechanical faults that cause routine loss of positioning from the encoder's perspective. However, other sensors may be able to provide insight, and help correct the error. 
This kind of error most typically shows up in belt or chain skipping. 

To overcome these issues, what you can do is run some condition checking alongside your normal runtime code, trying to identify signs that the system is in a potentially incorrect state, and correcting sensor information.

This is best demonstrated with examples: 
- If you home a elevator to the bottom of a drive at position 0, you should never see encoder values be negative. As such, seeing a "negative" encoder value tells you that the mechanism has hit end of travel.
- If you have a switch at the limit of travel, you can just re-assert zero every time you hit it. If there's a belt slip, you still end up at zero.
- If an arm should rest in an "up" position, but the slip trends to push it down, retraction failures might have no good detection modes. So, simply apply a re-homing technique whenever the arm is in idle state.

> [!BUG] Band-Aid Fix
> Online Position Recovery is a useful technique in a pinch. But, as with all other hardware faults, it's best to fix it in hardware. Use only when needed.

If the system is running nominally, these techniques don't provide much value, and can cause other runtime surprises and complexity, so it's discouraged.
In cases where such loss of control is hypothetical or infrequent, simply giving drivers a homing/button tends to be a better approach. 

## Modelling Un-homed systems in code 

When doing homing, you typically have 4 system states, each with their own behavior. Referring it to it as a [[State Machines|State Machine]] is generally simpler 

```mermaid
flowchart LR
Unhomed --> Homing --> Homed --> NormalOperation
```

#### Unhomed
The UnHomed state should be the default bootup state. This state should prepare your system to 
- A boolean flag or state variable your system can utilize
- Safe operational current limits; Typically this means a low output current or speed control.

It's often a good plan to have some way to manually trigger a system to go into the Unhomed state and begin homing again. This allows your robot drivers to recover from unexpected conditions when they come up. There's a number of ways your robot can lose position during operation, most of which have nothing to do with software.

#### Homing
The Homing state should simply run the desired homing strategy. 

Modeling this sequence tends to be the tricky part, and a careless approach will typically reveal a few issues
- Modelling the system with driving logic in the subsystem and Periodic routine typically clashes with the general flow of the Command structure. 
- Modelling the Homing as a command can result in drivers cancelling the command, leaving the system in an unknown state
- And, trying to continuously re-apply homing and cancellation processes can make the drivers frustrated as the system never gets to the known state.
- Trying to make many other commands check homing conditions can result in bugs by omission.

The obvious takeaway is that however you home, you want it to be _fast_ and ideally run in the Auto sequence. Working with your designers can streamline this process.

Use of the [[Commands|Command]] decorator `withInterruptBehavior(...)` allows an easy escape hatch. This flag allows an inversion of how Command are scheduled; Instead of new commands cancelling running ones, this allows your homing command to forcibly block others from getting scheduled.

If your system is already operating on an internal state machine, homing can simply be a state within that state machine.

#### Homed
This state is easy: Your system can now assert the known position, clear your Homed state, apply updated power/speed constraints, resume normal operation.

## Example Implementations

### Command Based
Conveniently, the whole homing process actually fits very neatly into the [[Commands]] model, making for a very simple implementation
- `init()` represents the unhomed state and reset
- `execute()` represents the homing state
- `isFinished()` checks the system state and indicates completion
- `end(cancelled)` can handle the homed procedure

```java
class ExampleSubsystem extends SubsystemBase(){
	SparkMax motor = ....;
	private boolean homed=false;
	ExampleSubsystem(){
		motor.setMaxOutputCurrent(4); // Will vary by system
	}

	public Command goHome(){
		return new FunctionalCommand(
			()->{
				homed=false;
				motor.getAppliedCurrent()
			};
			()->{motor.set(-0.5);};
			()->{return motor.getAppliedCurrent()>3}; //isFinished
			(cancelled)->{
				if(cancelled==false){
					homed = true;
					motor.setMaxOutputCurrent(30);
				}
			};
		)
		//Optionally: prevent other commands from stopping this one
		//This is a *very* powerful option, and one that
		//Should only be used when you know it's what you want.
		.withInterruptBehavior(kCancelIncoming)
		// Failsafe in case something goes wrong,since otherwise you 
		// can't exit this command by button mashing
		.withTimeout(5);
		}
} 
```

This command can then be inserted at the start of autonomous, ensuring that your bot is always homed during a match. It also can be easily mapped to a button, allowing for mid-match recovery.


For situations where you won't be running an auto (typical testing and practice field scenarios), the use of [[Triggers]] can facilitate automatic checking and scheduling
```java
class ExampleSubsystem extends SubsystemBase(){
	ExampleSubsystem(){
		Trigger.new(Driverstation::isEnabled)
		.and(()->isHomed==false)
		.onTrue(goHome())
	}
}
```

Alternatively, if you don't want to use the `withInterruptBehavior(...)` option, you can hijack other command calls with `Commands.either(...)` or `new ConditionalCommand(...)`
```java
class ExampleSubsystem extends SubsystemBase(){
/* ... */
	//Intercept commands directly to prevent unhomed operation
	public Command goUp(){
		return Commands.either(
		Commands.run(()->motor.0.5)
		goHome(),
		()->isHomed
	}
/* ... */
```

