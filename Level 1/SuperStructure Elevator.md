---
aliases:
  - Elevator
tags:
  - stub
---

Requires:
[[FeedForwards]]
[[PID]]
Reading Resources: 
[[Homing Sequences|Homing]]
## Success Criteria
- [ ] Create an Elevator subsystem
- [ ] Set Encoders
	- [ ] find the encoder range and conversion to have real-world values
	- [ ] Find the system range, apply soft limits
- [ ] Get control
	- [ ] Determine the system Gravity feed-forward value
	- [ ] Create a PID controller
	- [ ] Tune the PID to an acceptable level for control
- [ ] Create a default command that holds the system at the current height
- [ ] Create a setHeight function that takes a height, and returns a command that runs indefinitely to the target height
- [ ] Create a Trigger that indicates if the system is within a suitable tolerance of the commanded height.
- [ ] Bind several target positions to a controller
- [ ] Create a small auto sequence that moves to multiple positions in sequence.


## Synopsis

Elevators are a common linear motion system in FRC, generally used to raise game pieces from the floor to a high scoring objective. 

While mechanically straightforward, Elevators wind up generating a notable step up in code and control complexity compared to [[SuperStructure Rollers|Roller]] systems.

## Safety first! Elevator hazards

Elevators introduce several areas where you can inadvertently cause damage to the environment and robot. When prototyping and developing them, there's a few important considerations:

- Elevators have a limited range of motion: Carelessly commanding them beyond their physical constraints can damage the robot, the motors, and eject parts at nearby people!
-  Elevators and drive belts/ropes generate many pinch points that can snag fingers if the system is moved by hand, even when disabled. 
- Rapid motion can de-stabilize a robot that's sitting on a cart or on blocks

When making changes to elevator behaviors and tuning, make sure someone nearby is ready to disable the robot if anything goes wrong.
## PIDS + FeedForwards + Motion Profiles

Controlling an elevator requires the use of a [[PID]], and *greatly* benefits from aspects of both [[FeedForwards]] and [[Motion Profiles]].

A PID by itself *can* control an elevator, but it will be difficult to tune and require significant time and resources to "hack" into a usable state. Any time you change setpoints, the whole system is in for a bad time: The output power rapidly changes to move to the new position,  putting significant strain on the robot, causing excess wear, and potential damage. Moving a significant distance can also slam the elevator carriage into the limits of motion, causing damage.

FeedForwards, or at least the kS and kG terms, will greatly improve stability and accuracy, and reduce the time it takes for tuning a PID. However, it does *not* mitigate the issues cause changing setpoints.

[[Motion Profiles]] fix the setpoint problem, and lock the setpoint changes into a nicely defined ramp-up and ramp-down when changing positions. This means your PID never misbehaves, and provides additional feed-forward information to further simplify tuning. 

While the combination may seem daunting at first, each of these 3 solves a particular motion problem effectively, and with straightforward individual adjustments. Using all 3 together provides a better performing system, with much less effort overall. 

On occasion, you'll encounter linear systems with extremely high-torque gearing, such as screw-drive systems. These really *do* perform perfectly fine with just a PID controller, since the motor itself is never loaded, and a full motor output generates a slow motion with little inertia. In that case, you can simply ignore the FeedForward and Motion Profile.

## Preparing The Elevator

The first few steps can be done before spinning the motor
- temporarily define the [[Homing Sequences|Homing]] behavior as bootup zero. This streamlines the initial process.
- Calibrate the encoder on the elevator to Inches, and ensure the encoder increases as you gain height. These steps were encountered in [[Encoder Basics]]
- Verify the correct calibration and encoder values by moving the elevator by hand when disabled.
- Define soft limits to prevent driving past the frame limits. The important limit is the "reverse" limit heading toward the bottom, but the top limit can be set to an approximate or low value temporarily.
- Estimate and configure a low P value. This will be used for controlled preliminary motion, and just needs to move the system and not oscillate at setpoints. 
- Estimate and configure sane maxVelocity and maxAccelleration values for the motion profile.
- plot motor output voltage and height, which you'll need for tuning.
- You will need a `setPower` style command that allows arbitrary input.

## Tuning

With the system and code prepared, you can now work with the system a bit.
- Determine the approximate system kG: For this, you can use the manual command to drive the motor upward away from the hard stop, then and hold it still. Record the voltage, and add it to your feed-forward.
- Now you can use easily use your manual adjustment command and approximate kG calculate a precise kS and kG, as indicated in [[FeedForwards#Finding kS and kG]]. It helps to divide the joystick value by 4 to 10 to give you more range on the physical input side.
- With the FeedForward configured, almost any kP will work to get the system to setpoints, but slowly. If your system is oscillating, you can lower kP. 
- Optionally, you can attempt to calculate kV and kA, or use [[System Identification]] to assist.
- At this point, your system should be travelling between setpoints fairly cleanly. The plot of your position during motion should be smooth, and your velocity a clean trapezoidal shape.
- Now that you have optimized motion, you can set the upper soft limit properly.
- Check your current limits! Now is a reasonable time to increase them, and you will want to plot them to see what currents are needed so you can limit them properly. 

## Troubleshooting:

- System oscillates or rattles around setpoint: This is likely due to a high kS value, but it may be caused by a high kP value.
- There's odd hitches/jolts during a motion: Your kV or kA gain is probably too high. Set kA to zero, and then lower kV until it moves smoothly. Then try to add kA back in, lowering if needed. This is possible with a high P gain, but less probable.
- The system drifts up/down: Your kG is too high or too low.
- System not getting to setpoint: If it's not going up enough, kG+kS is too small. If it's not going down enough, kG-kS is too small. Recalculate those two.
- Any of the above, but only when loaded/unloaded with game pieces: This can be caused due to the weight difference causing your gains to no longer properly approximate your system output. One option is to tune for the "lightest" elevator, which should result in stable motion when weight is added, and will lean on the PID to handle the error. The ideal option is to configure two or more feed-forwards, and swap the feed-forward values when each condition occurs.
- Any of the above, but only in some parts of the travel. This can be caused by design quirks, as some elevator configurations have physical differences at different points in travel. Similar to above, you can tune to the "lowest output" variant and let the PID handle problems, or configure several feed-forwards and select them as your elevator enters specific regions of travel.



## Optimizing the Motion
![[motion-profile-time-plots.png]]
If your FF+PID tuning looks good, it's time to play with the Motion Profile. The goal is to make your max Velocity and max Accel as high as possible, representing the fastest possible motion. However, you do not want to set these higher than the system can physically manage, and it should never move so fast as to be unsafe or self-damaging. So, it's something of an iterative, incremental increase until you get the motions you want.

The best two plots for this are 
- Current height and setpoint height
- Current velocity and setpoint velocity
These both represent what you're *telling* the system to do and what it's actually doing. 

Once you have plots, you just need two buttons on a controller, moving the elevator to two different heights. You should choose heights far apart to have a relatively long travel, but stays a couple inches away from any hard stops or soft limits. The extra space at the end provides a safety net in case of an error, and allows the system to avoid crashing into them at high speeds. The long travel ensures you can reach high speeds during the motion, and test the max velocity.

Now, you should be able to see both the height and velocity closely track the setpoint versions. There's a few ways that they can deviate

- The velocity setpoint graph is always a triangle, never a trapezoid: This is ok! This just means you're constrained by range and the acceleration. As you increase acceleration, you 
- The real velocity/position "lags" behind the setpoint. This is expected if you do not have a kV or kA on your feedforward. Adding a kV will bring them closer together and reduce this lag. A kA can also help, but is less critical.
- The real position/velocity "bounces": See troubleshooting above. It's likely that your P gain is too high.
- Instead of a trapezoidal shape, the velocity start and end have curved lines, and/or overshoots setpoint: This means your max acceleration is too high, and the motor cannot output enough power to hit the target velocity. Check your current limit, increase if needed. If you cannot provide more current, or do not wish to do so, decrease the acceleration until the system is on track.
- The robot emits concerning creaky/crunchy noises: Your acceleration is higher than the bot is capable of surviving. The motor can keep up, but the geartrain will explode in short order. Lower the acceleration until it goes away. Remember, F=M\*A : The acceleration is what imparts the force to the elevator. 


## Homing

Until now, we've assumed an initial physical position against a hard stop. This is perfectly suitable for setup, testing, and diagnostics, but can be hazardous over the full life cycle of a robot.

Every time the battery is changed or the power is cycled, there's a chance that the robot wasn't where you expected, and if caught off guard you can damage the robot pretty severely, on top of failing to play the game as intended. 

Elevators tend to require [[Homing Sequences|Homing]] more than other systems, as there's no robust, readily available sensor to capture the height directly. 

There's several valid homing strategies, but often elevators start near the lowest point of travel, making it simple to use a [[Homing Sequences#Current Detection|Current Detection]] process. Helpfully, our feed forward analysis also tell you what the needed output power is: something just below $kG-kS$ .

Homing should start automatically (or be run at start of auto), but it's also wise to give Drivers a "home" button just in case. They can't touch the robot on the field, making a home operation a necessary option for recovery from reboots or unexpected field events.

## Starter Template With Profiles

This uses the streamlined WPILib [ProfiledPIDController](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/profiled-pidcontroller.html) providing the simplest complete implementation.
There's many useful methods here : [ProfiledPIDController Method Reference](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/controller/ProfiledPIDController.html)

`[!!bug|UNITS|var(--color-red-rgb)]`  This is configured to use Volts for feedforward and PID gains, so be mindful!

`[!!alert-triangle|Homing|var(--color-red-rgb)]` This template does not include [[Homing Sequences|Homing]], and assumes a "zeroed on boot" approach. It's recommended to determine and implement  a robust strategy for most bots.

```java
ExampleElevator extends SubsystemBase{
	SparkMax motor = new SparkMax(42,kBrushless);

	//Define our system's maximum motion constraints
	//When in doubt, 
	//- set maxVelocity at a low value. 12 in/s is a great starter value 
	//- set kMaxAccelleration high, at roughly 4*kMaxVelocity
	private final TrapezoidProfile.Constraints constraints =
		new TrapezoidProfile.Constraints(kMaxVelocity, kMaxAcceleration);
	//Create a PID that obeys those constraints in it's motion
	//Likely, you will have kI=0 and kD=0
	private final ProfiledPIDController controller =
		new ProfiledPIDController(kP, kI, kD, constraints, 0.02);
	//Our feedforward object
	//When in doubt, set these to zero (equivilent to no feedforward)
	//Will be calculated as part of our tuning.
	private final ElevatorFeedforward feedforward = 
		new ElevatorFeedforward(kS, kG, kV);
	
	ExampleElevator(){
		//These documented below to better demonstrate the general structure
		configMotor();
		configProfiledPID();
		//DefaultCommands help avoid leaving a system in an uncontrolled
		//state. For elevators, we often want no motion
		setDefaultCommand(stop());
		//For systems using an I gain, you likely want to 
		//command the PID controller at all times rather than use stop(), 
		//which behaves better with the I term's summing behavior.
		//holdPosition() is better than stop() for this use case.
	}
	
	///Prepare Telemetry for tuning
	public void periodic(){
		var output=motor.getAppliedOutput();
		// We cannot get the applied voltage directly, but since
		// it's a percentage we can multiply by the provided battery voltage
		var voltage=motor.getAppliedOutput()*RobotController.getBatteryVoltage();
		var height=motor.getEncoder().getPosition();
		var velocity=motor.getEncoder().getVelocity();
		var setpoint=controller.getSetpoint();
		
		Smartdashboard.putNumber("elevator/voltage",voltage);
		Smartdashboard.putNumber("elevator/height",height);
		Smartdashboard.putNumber("elevator/velocity",velocity);
		Smartdashboard.putNumber("elevator/set position",setpoint.position);
		Smartdashboard.putNumber("elevator/set velocity",setpoint.velocity);
	}

	//Triggers form the optimal way to transfer subsystem states/conditions
	//to external subsystems.
	//The PID contains this method, so we can just check it
	//ProfiledPID objects's other useful indicators of completion as well,
	//such as .timeRemaining for the intended motion.
	public Trigger isAtPosition = new Trigger(controller::atGoal);
	
	/// Return the height of our elevator, using Unit measures
	public Distance getHeight(){
		return Inches.of(motor.getEncoder().getPosition)
	}
	
	/// Provide manual output. Should only be used for
	/// debugging, tuning, and homing
	public Command setPower(DoubleSupplier percentOutput){
		//Including the feed-forward here, but can be removed
		//if your testing is easier without it.
		return run(()->motor.setVoltage(
			percentOutput.getAsDouble()*RobotController.getBatteryVoltage()
			+ ff.getKg()
			)
		); 
	}
	
	/// Disable the elevator's motion entirely
	public Command stop(){
		//Note, an output of 0 usually falls down, which is *not* the 
		//same as stopping! So, we instead use kg to resist gravity.
		//A "stopped" subsystem can still be moved by hand or 
		//external forces though!
		return setPower(feedforward::getKg)
	}
	
	/// Actively maintain the position command was started at
	public Command holdPosition(){
		var currentHeight=getHeight();
		return setHeight(()->currentHeight);
	}

	///Command the Elevator to go to the target position
	public Command setHeight(Supplier<Distance> position){
		return startRun(
		()->{
			//Set our initial goal prior to starting the command
			// Optional? Used previously to prevent trigger glitches
			controller.setGoal(position.get().in(Inches));
		},
		()->{
			//Update our goal with any new targets
			controller.setGoal(position.get().in(Inches));
			//Calculate the voltage
			voltage=
			motor.setVoltage(
				controller.calculate(motor.getEncoder().getPosition()) 
				+ feedforward.calculate(controller.getSetpoint().velocity)
			);
		});
	}
}
```

```java
// Important configuration parameters for an Elevator subsystem
void configMotor(){
	//These are the important ones for Elevators specifically
	SparkBaseConfig config = new SparkMaxConfig();
	//Default current limit should be low until the system is homed.
	//.. but this requires a homing process.
	config.smartCurrentLimit(40);
	//You want "forward" to be positive, with increasing encoder values
	config.inverted(false); 
	//Soft 
	config.softLimit
	.forwardSoftLimit(18) //Whatever max height is
	.forwardSoftLimitEnabled(true)
	.reverseSoftLimit(0) //Sometimes zero, or the lost attainable height
	.reverseSoftLimitEnabled(true);
	;
	//The conversion/gearing needed to put your system in a sensible
	// unit; Often Inches.
	var elevatorConversionfactor = 1;
	elevatorConfig.encoder
	.positionConversionFactor(elevatorConversionfactor)
	.velocityConversionFactor(elevatorConversionfactor / 60.0)
	;
	//Apply the configs
	elevatorMotor.configure(
		config,
		ResetMode.kResetSafeParameters,
		PersistMode.kPersistParameters
	);
}

void configProfiledPID(){
	//Configure the ProfiledPIDController
	
	//This value constraints the output added by the I term 
	//in the PID controller. This is very helpful to prevent damage in cases
	//where the elevator system jams or collides with the environment and 
	//cannot move.
	// This will be system dependent, and should (needs testing) be 
	// in PID ouptut units (normally Volts)
	controller.setIntegratorRange(-1, 1); //Configure to +/-1 volt
	
	//When the robot is disabled, the elevator might move, invalidating
	// the controller's internal state, and invalidating the I term's sum.
	//This needs to be reset whenever the robot is enabled, and a Trigger
	// provides a clean way of handling this
	new Trigger(DriverStation::isEnabled).onTrue(
		()->controller.reset(motor.getEncoder.getPosition())
	);
	
	//There's other methods for interacting with the PID values 
	// and constraints, should the need arise
}
```

Advanced usage can easily extend this to handle position dependent constraints, or provide alternate constraints for certain motions. 

## Using Spark PID control + Elevators


> [!TIP] New API incoming
> Rev is developing a new Spark API that can run Feedforwards + Motion profiles entirely on the Spark Motor controller. 
> TrapezoidalProfiles + external PID is messy, and thus documented to a minimal extent below
> 

Implementing mixed Spark PID + Trapezoidal  is not strongly recommended, due to the high levels of code jank and surprise complexity, combined with limited benefit in this context. However, a full example can be found in [2025 Stormbots Reefscape](https://github.com/stormbots/Reefscape/tree/main/src/main/java/frc/robot/subsystems/Elevator#L264)  

The primary difference from above
- The need to create a Trapazoidal Profile object directly, rather than a ProfiledPID
- You need to set the PID in the trapezoidal Profile to determine the expected state 
- Need to query the calculated state and pass the value to the external PID.
- Need to track the "Goal state" for completion testing

Note that a "Goal State" represents the final objective for the trap profile, which is used to calculate setpoints, serving as intermediate targets for motion.
  
```java

private TrapezpoidProfile.Constraints constraints = 
new TrapezpoidProfile.Constraints(
	kElevatorMaxVelocity,
	kElevatorMaxAcceleration
);
private final TrapezoidProfile elevatorTrapezoidProfile = 
	new TrapezoidProfile(constraints);
	
//Needed to store the Final state of the motion so triggers can 
//calculate finished motion
TrapezoidProfile.State elevatorGoal=new TrapezoidProfile.State(0,0);
//We also need to retain the intermediate steps; When these match, 
//our planned motion has been completed.
TrapezoidProfile.State elevatorSetpoint=new TrapezoidProfile.State(0,0);

private Command setHeight(DoubleSupplier position){
	return new SequentialCommandGroup(
		//Seed the initial state/setpoint with the current state
		//Maybe optional?
		new InstantCommand( ()->{
			elevatorSetpoint = new TrapezoidProfile.State(
				getAngle().in(Degrees),
				elevatorMotor.getEncoder().getVelocity()
			);
		}),
		  
		new RunCommand(()->{
			//Make sure the goal is dynamically updated
			elevatorGoal = new TrapezoidProfile.State(
				position.getAsDouble(),
				0
			);
			//update our existing setpoint to the next achievable state
			elevatorSetpoint = elevatorTrapezoidProfile
				.calculate(0.02, elevatorSetpoint, elevatorGoal);
			//Query our feedforward for projected output
			var ff = elevatorFF.calculate(
				elevatorSetpoint.position,
				elevatorSetpoint.velocity
			);
			//Pass all our info out to the motor for output and PID control
			elevatorMotor.getClosedLoopController()
			.setReference(
				elevatorSetpoint.position,
				ControlType.kPosition, ClosedLoopSlot.kSlot0,
				ff, ArbFFUnits.kVoltage
			);
		})
	);
	}

}
```