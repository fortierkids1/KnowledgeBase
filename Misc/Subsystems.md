---
aliases:
  - Subsystem
---
## Success Criteria
- [ ] None? Part of [[Robot Code Basics]] and reference

## Synopsis
A "Subsystem" class generally accomplishes 4 distinct things in a robot project
- Serve as a "resource" to constrain hardware for [[Commands]] requirement
- Hold tuning, configuration and code 
- House useful [[Commands]]
- House useful [[Triggers]]

Any time you're performing multiple tasks in a single code class, careful consideration of your structure is important. 
## Splitting up the robot
The first consideration of your robot should always be "What qualifies as a 'subsystem'" . Through experience or wisdom, you'll generally land on "A subsystem is a single actuator".

For more complex bots, you may be forced to consider a single subsystem as one or more [[Forward Kinematics||kinematically linked]] sets of mechanisms.

Chassis are something of an exception: Despite having multiple actuators/motors, they act as a single cohesive system, and have helper classes to manage some complexity.

This decision *will* create more subsystems than you think are necessary. However, it has a few advantages
- The subsystems are smaller, simpler, and more manageable
- More subsystems --> easier splitting of tasks and workloads 
- A couple simple [[Commands]] can easily link two subsystems that might seem useless when operated independently
- As a season progresses, unexpected developments might bring to light reasons to split a "double actuator subsystem". Having this already be done saves a lot of time and effort.


## Triggers as subsystem state 

When breaking down a robot, it's a good idea to model your subsystem's state using [[Triggers|Triggers]]. This might be directly measured state such as `isGamepieceLoaded` or `isAtPosition`. 
Triggers can also model other implicit states such as `isHomed` or complex conditions like `isJammed` .

Encapsulating your system state as a Trigger makes it simple to pass into Commands, sequences, or even directly launch conditions based on them.

There are of course states that *cannot* be represented this way, such as the current arm angle or elevator height. However, it is often useful to represent a meaningful angle/height as a function, such as `isAtGoal2Height`, `atScoringPosition`, or `isAtIdlePosition`

Modelling your system this way promotes loose coupling between subsystems: A given command can check for required conditions across a variety of subsystems without having to strictly control the system. This makes for flexible, easy to write autos and sequences, and makes for easy to read code.

## Example Implementations

> [!Warning] Pseudocode warning
> This code will not compile or work out of the box, and just serves as a reference. 


### Bare minimum roller system

This is a functional, but barebones system. This is often suitable for testing, and many simple [[SuperStructure Rollers|Roller]] mechanisms on some bots. 

```java
public SimpleRollerSystem extends SubsystemBase(){
	SparkMax motor = new SparkMax(42,kBrushless);

	ExampleSubsystem(){
		//Normal constructor tasks
		//Configure motor
		//Set the default command; In this case, just power down the roller
		setDefaultCommand(setPower(0))
	}	

	public Command setPower(double power){
		return run(()->motor.set(power)); 
	}
}
```

### Simplified elevator system

This is representative of a much more feature complete subsystem, including positional control. This is in line with the expectation of a Stormbot's subsystem suitable for competition.

This integrates [[FeedForwards]], [[PID|PIDs]], and a [[Motion Profiles|Motion Profile]]. 

```java
ExampleElevatorSubsystem extends SubsystemBase{
	SparkMax motor = new SparkMax(42,kBrushless);

	//See [[FeedForwards]] for additional information
	ElevatorFeedForward ff = new ElevatorFeedForward(...);

	//This is a relatively stramlined way to combine [[PID]] 
	// and [[Motion Profiles]]
	TrapezoidProfile.Constraints constraints = 
		new TrapezoidProfile.Constraints(maxv,maxa);
	ProfiledPIDController pid = 
		new ProfiledPIDController(kp,ki,kd,constraints);
	
	ExampleElevatorSubsystem(){
		//Normal constructor tasks, setup, and configuration
		//Configure motor
		
		//DefaultCommands help avoid leaving a system in an uncontrolled
		//state. Often, this simply means intentionally applying no motion.
		//No motion does *not* always mean a power of 0, as seen here!
		setDefaultCommand(setPower(ff.getKg()));
	}	

	//Triggers form the optimal way to transfer subsystem states/conditions
	//to external subsystems.
	public Trigger isAtPosition = new Trigger(pid::atGoal);
	
	//A manual command option is essential for tuning and calibration
	//Including the feed-forward here  
	public Command setPower(double power){
		return run(()->motor.set(power)); 
	}
	
	//Once tuned, a profiled PID is the gold standard for most control actions
	//The primary version _should not_ exit, as it complicates composition.
	public Command setPosition(double position){
		return startRun(()->{
			//Do initial reset for PID/profile
		},
		()->{}
			//Run the profiledPID
		});
	}
	
	//This version simply adds the exit condition to the setPosition
	//This helps clean up autos and sequences.
	public Command setPositionExit(double position){
		return setPosition(position).until(isAtPosition);
	}
}
```


## Sensor Systems

Sensor subsystems can help manage access of a single sensor across multiple subsystems. 

```java
ExampleSensorSubsystem extends SubsystemBase(){
	//Create a sensor object, whatever that looks like
	Sensor sensor = new Sensor(....);
	
	ExampleSensorSubsystem(){
		//Normal constructor tasks
		//Configure sensor
		//There's likely no need for a defaultCommand
	}

	//Set Triggers if there's obvious reasons to do so
	public Trigger isThingInRange = new Trigger(()->
		if(sensor.read < 10) return true;
		return false;
	}
	
	// Repeat triggers as needed to have all useful states

	// Sometimes you just need analog data.
	public double getDistanceToThing(){
		return 0;//read sensor and return a sane value
	}
}
```

In most cases, sensors can simply be integrated with the subsystem they're a part of. However, some subsystems are unclear, or serve multiple roles, and being a dedicated system helps prevent quirky [[Code Patterns|Dependency Injection]] of one subsystem into another.

Generally, sensor subsystems should be designed to avoid needing to `require` the subsystem, facilitating shared access. However, sometimes special cases are needed for very modal sensors. The most notable and common example is Vision systems where pipeline changes are necessary for different game pieces, zoom, or detection methods.

Sensor Systems are one of the places where it might be sensible to wrap the system as a [[Singletons|Singleton]] , enabling easy shared access to read-only data without the scope management of dependency injection.


## Bad design: Multi-actuator subsystems

Whenever possible, it is recommended to minimize actuators within a single subsystem: Ideally, you want 1 actuator per subsystem. 

The exception is for systems that are kinematic-ally linked: EG, moving one *requires* controlling the other part, and moving them independently is impossible, unsafe, or impractical. 

The most common case of "too many actuators" applies to simple [[SuperStructure Rollers|Roller]] scoring systems added to height/position mechanisms, and the problem arises in a predictable way. Let's call it ElevatorScorer, consisting of an Elevator and a Roller:
- The driver loads a game piece, and wants to score
- The driver presses PositionElevator, requiring ElevatorScorer
- The driver gets into position,  and presses Score button (also requiring ElevatorScorer)
From a [[Commands|Command]] perspective, this is no issue: The first command is cancelled, and the second one runs. 

However, let's consider the two actuators: The first one is controlling the height, selecting one of several scoring positions. The second is controlling the rollers. These two actions are independent! 

When running the "Score" routine, you're having to remember the last target height, _or_ you have to have many additional functions to set target height *and* the scoring option. Your driver also cancelled the original go to height one; If they brushed it accidentally too early, they have to *re-hit* the PositionElevator button to set the height.

The real problem though, is if you're following good Command practice, you probably have commands that look like this:

```java
public class ElevatorScorer extends SubsystemBase{
	public Command setHeight(double height){
		return run(()->/*Set the height*/)
	}	
	public Command setRollers(double height){
		return run(()->/*Set the roller speed*/)
	}
	public Command scoreWhenAtHeight(){
		return parallel(
			setHeight(30),
			setRollers(0)
		).until(isAtTargetHeight()
		.andThen(setRollers(-1))
		;
	}
}
```

This will actually fail! The `parallel` command group cannot run both `setHeight` and `setRollers` at the same time, since they both require the same subsystem (`elevatorScorer`). It's clearly nonsensical to be unable to run both halves of the same subsystem at the same time, but by structuring things incorrectly, we've backed ourselves into a mess. 

Our solutions now are 
- Use some gross, poorly documented secret command wrappers to ignore the requirements (`.runAsProxy()`)
- Remove the requirements from the Rollers, violating the goal of Commands
- Duplicate a bunch of work to have "combo" commands to run both with a single requires.
- [[Refactoring|Refactor]] our code into two subsystems

While there's some cases to use the first few, the best solution long term is to refactor the code, making the systems properly independent. 

The easy way to catch this design early is ask "Am I having to provide two inputs for one action". If so, they should probably be two seperate subsystems. Currently, WPILib has no way to properly "nest" subsystems, but a simple wrapper class can be used to house commands interacting with two strongly coupled, but independent subsystems (such as an Arm attached to an Elevator).