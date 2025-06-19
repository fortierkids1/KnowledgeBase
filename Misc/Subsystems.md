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
- The subsystems are smaller, and more manageable
- More subsystems --> easier splitting of tasks and workloads 
- A couple simple [[Commands]] can easily link two subsystems that might seem useless when operated independently
- As a season progresses, unexpected developments might bring to light reasons to split a "double actuator subsystem". Having this already be done saves a lot of time and effort.


## Example Implementations

> [!Warning] Pseudocode warning
> This code will not compile or work out of the box, and just serves as a reference. 


### Bare minimum roller system

This is a functional, but barebones system. This is 

```java
SimpleRollerSystem extends SubsystemBase(){
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

This is representative of a much more feature complete subsystem, including positional control. This is in line with the expectation of a Stormbot's production subsystem.

This integrates [[FeedForwards]], [[PID|PIDs]], and a [[Motion Profiles|Motion Profile]]. 

```java
ExampleElevatorSubsystem extends SubsystemBase(){
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
		setDefaultCommand(setPower(kg));
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

Sensor subsystems can help manage access of a single sensor across multiple other subsystems. 

```java
ExampleSensorSubsystem extends SubsystemBase(){
	//Create a sensor object, whatever that looks like
	Sensor sensor = new Sensor(....);
	
	ExampleSensorSubsystem(){
		//Normal constructor tasks
		//Configure motor
		//There's likely no need for a defaultCommand
	}

	//Set Triggers 
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

Generally, sensor subsystems should be designed to avoid needing to `require` the subsystem, facilitating shared access. However, sometimes special cases are needed for very modal systems. The most notable and common example is Vision systems where pipeline changes are necessary for different game pieces, zoom, or detection methods.
