---
aliases:
  - Arm
  - Pivot
tags:
  - stub
---

Requires:
[[FeedForwards]]
[[PID]]

## Success Criteria
- [ ] Create an Arm subsystem
- [ ] Set Encoders
	- [ ] find the encoder range and conversion to have real-world angle values
	- [ ] Find the system range, apply soft limits
- [ ] Get control
	- [ ] Determine the system Gravity feed-forward value
	- [ ] Create a PID controller
	- [ ] Tune the PID to an acceptable level for control
- [ ] Create a default command that holds the system at the current angle
- [ ] Create a setAngle function that takes a angle, and returns a command that runs indefinitely to the target angle
- [ ] Create a Trigger that indicates if the system is within a suitable tolerance of the commanded height.
- [ ] Bind several target positions to a controller
- [ ] Create a small auto sequence that moves to multiple positions in sequence.


> [!TIP] WPILib web sim
> For a quick intro into Arm FeedForwards, WPILib's docs have a useful tool
> https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-vertical-arm.html


## Safety first! Arm hazards

Arms introduce several areas where you can inadvertently cause damage to the environment and robot. When prototyping and developing them, there's a few important considerations:

- Arms have a limited range of motion: Carelessly commanding them beyond their physical constraints can damage the robot, the motors, and eject parts at nearby people!
-  Arms and drive belts/ropes generate many pinch points that can snag fingers if the system is moved by hand or by gravity, even when disabled. 
- Rapid motion can de-stabilize a robot that's sitting on a cart or on blocks
- Arms that are "placed up" might seem stable, but if someone bumps a bot or turns it off, things might shift unexpectedly. Arms should be down when working on the bot, or physically blocked/held when needing to be up for access.
- Arms technically have *much* more torque than connected parts have mechanical robustness. Make sure to configure soft limits ASAP to prevent system damage.

When making changes to arm behaviors and tuning, make sure someone nearby is ready to disable the robot if anything goes wrong.


## Starter Template With Profiles

This uses the streamlined WPILib [ProfiledPIDController](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/profiled-pidcontroller.html) providing the simplest complete implementation.
There's many useful methods here : [ProfiledPIDController Method Reference](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/controller/ProfiledPIDController.html)

`[!!bug|UNITS|var(--color-red-rgb)]`  This is configured to use Volts for feedforward and PID gains, so be mindful! If you've calibrated your gains for "percent output" you'll need to convert them.

`[!!alert-triangle|Homing|var(--color-red-rgb)]` This template does not include [[Homing Sequences|Homing]], and assumes a "zeroed on boot" approach. It's recommended to determine and implement  a robust strategy for most bots. Since arms usually have absolute encoders, this typically provides the ideal workflow.

```java
public class ExampleArm extends SubsystemBase {
	SparkMax motor = new SparkMax(42,MotorType.kBrushless);

	//Define our system's maximum motion constraints
	//When in doubt, 
	//- set maxVelocity at a low value. 30 deg/s is a great starter value 
	//- set kMaxAccelleration somewhat low, at roughly 2*kmaxVelocity
	private final TrapezoidProfile.Constraints constraints =
		new TrapezoidProfile.Constraints(0, 0);
	//Create a PID that obeys those constraints in it's motion
	//Likely, you will have kI=0 and kD=0
	private final ProfiledPIDController controller =
		new ProfiledPIDController(0, 0, 0, constraints, 0.02);
	//Our feedforward object
	//When in doubt, set these to zero (equivilent to no feedforward)
	//Will be calculated as part of our tuning.
	private final ArmFeedforward feedforward = 
		new ArmFeedforward(0, 0, 0);
	
	ExampleArm(){
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
		var angle=motor.getEncoder().getPosition();
		var velocity=motor.getEncoder().getVelocity();
		var setpoint=controller.getSetpoint();
		var goal=controller.getGoal();
		
		//Generally useful values
		SmartDashboard.putNumber("arm/output",output);
		SmartDashboard.putNumber("arm/voltage",voltage);
		SmartDashboard.putBoolean("arm/atSetpoint",controller.atSetpoint());
		SmartDashboard.putBoolean("arm/atGoal",controller.atGoal());
		//Our relevant Positional values
		SmartDashboard.putNumber("arm/angle/measured",angle);
		SmartDashboard.putNumber("arm/angle/setpoint",setpoint.position);
		SmartDashboard.putNumber("arm/angle/goal",goal.position);
		SmartDashboard.putNumber("arm/angle/error",
			controller.getPositionError());
		//Our relevant Velocity values
		SmartDashboard.putNumber("arm/velocity/measured",velocity);
		SmartDashboard.putNumber("arm/velocity/setpoint",setpoint.velocity);
		SmartDashboard.putNumber("arm/velocity/goal",goal.velocity);
		SmartDashboard.putNumber("arm/velocity/error",
			controller.getVelocityError());
	}

	//Triggers form the optimal way to transfer subsystem states/conditions
	//to external subsystems.
	//The PID contains this method, so we can just check it
	//ProfiledPID objects's other useful indicators of completion as well,
	//such as .timeRemaining for the intended motion.
	public Trigger isAtPosition = new Trigger(controller::atGoal);
	
	/** Return the height of our elevator, using Unit measures */
	public Angle getAngle(){
		return Degrees.of(motor.getEncoder().getPosition());
	}
	
	/** Provide manual output. Should generally only be used internally
	 * for debugging, other commands, or occasional special cases.
	 */
	private Command setPower(DoubleSupplier percentOutput){
		// Due to the non-linear effect of a fixed power on an arm, 
		// the feedforward with kg is critical for sensible control.
		return run(()->{
			var vpercent=
				percentOutput.getAsDouble()*RobotController.getBatteryVoltage();
			var vff = feedforward.calculate(getAngle().in(Radian), 0);	
			motor.setVoltage(vpercent+vff);
		});
	}
	
	/** Disable the elevator's motion entirely */
	public Command stop(){
		//Since we handle kG in setPower, a value of 0 represents "stop" effectively
		return setPower(()->0);
	}
	
	/** Command the Elevator to go to the target position */
	public Command setAngle(Supplier<Angle> position){
		return run(()->{
			//Update our goal with any new targets
			controller.setGoal(position.get().in(Degrees));
			//Calculate the voltage contributing to the output
			var vpid = controller.calculate(motor.getEncoder().getPosition());
			var vff = feedforward.calculateWithVelocities(
				motor.getEncoder().getPosition(),
				motor.getEncoder().getVelocity(),
				controller.getSetpoint().velocity);
			//Apply to the motor
			motor.setVoltage(vff+vpid);
		});
	}
	
	/** Set the angle to a fixed value */
	public Command setAngle(Angle position){
		return setAngle(()->position);
	};


	/** Actively maintain the position command was started at */
	public Command holdPosition(){
		//Deferred commands are needed here to capture the angle
		//at the time the command is initiated
		return defer(()->setAngle(getAngle()));
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
	.forwardSoftLimit(18) //Whatever max angle is
	.forwardSoftLimitEnabled(true)
	.reverseSoftLimit(0) //Sometimes zero, or the lowest attainable angle
	.reverseSoftLimitEnabled(true);
	;
	//The conversion/gearing needed to put your system in a sensible
	// unit; Degrees is generaly preferable.
	// It's recommended to set this, even if you use the absolute encoder
	// as your primary input
	var conversionfactor = 1;
	config.encoder
	.positionConversionFactor(conversionfactor)
	.velocityConversionFactor(conversionfactor / 60.0)
	;
	//The absolute encoder will *also* need configuration. 
	//However, since it avoids gearing, it's generally easy
	config.absoluteEncoder
	.zeroCentered(false)// false ->0...360 range. True ->-180...180 range
	.positionConversionFactor(360) //Configure for Degrees
	.velocityConversionFactor(360/60)
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



## PIDS + FeedForwards + Motion Profiles

Unlike other systems, Arms are almost impossible to properly control using *just* PID controls! PIDs are a "linear" controller, and expect the system to respond the to a change in output in a consistent way. Arms do not do this, and respond differently according to it's current position.

As a result, the kG is the most valuable detail of tuning an Arm: This removes the effect of gravity, bringing the system back into a linear state and making a PID function as expected.

The inertia of arms is proportional to mass and _square_ of the arm length: Meaning, a fast moving arm will take a *significant* amount of force to stop. Having a [[Motion Profiles|Motion Profile]] with a sensible max velocity greatly improves the safety and consistency of an arm.

Similarly, the high inertia combined with very small teeth on our gearboxes, you get very small, critical points of contact with enormous physical stresses. Because of this, a motion profile's acceleration constraint is extremely helpful in preventing damage to the arm.

In general, when examining arm tunings:
-  finding the arm kG, kV, and kS will ensure that your arm will quickly get to the goal positions.
- Your MaxAccel constraint is typically much more important than your maxVelocity: MaxAccel constraints the force applied by the motor, and for most Arm systems you will spend the majority of the motion accelerating or decelerating rather than coasting at max speed. 
- The MaxVelocity tends to help constrain the tangential force: Eg, your robot losing objects midway through a fast motion and flinging them unexpectedly.
- Depending on your arm's role, you may need a kI or kD on your PID: Because forces are magnified by the arm length, even a relatively small forces can have a notable impact on your system's precision, possibly preventing it from reaching your goal state. However, in such cases you might consider amount of available motor power you're using at such goal states, and see if you can have the mechanism geared lower to provide more torque. Since the max speed is often unattainable in practice, more torque helps provide snappier, more controlled motion. 

## Absolute Encoders

[[Absolute Encoders]] are commonly used to fully capture the rotation of an Arm system. This greatly simplifies (or removes) the [[Homing Sequences|Homing]] process. 

However, it's critical that you ensure the encoder reports continuous angles through the entire motion plus some on each end, avoiding any [[Absolute Encoders#Discontinuities|discontinuities]]. Rev Through-bore encoders have an option to report -180..180 to assist many mechanisms. For systems that need a -90..+270 option, there's no API to move the discontinuity: You have to handle it in software before feeding it to your PID.

Most practical systems can be driven equally effectively by using the absolute encoder directly, or by using relative encoder and simply using the absolute encoder's position for initial setup. The choice comes down to handling several factors in the system, such as 
- Discontinuity location
- Trust in the wiring to the absolute encoder during operation
- System backlash (and impact on each encoder)
- 

## Backlash
[[Mechanical Backlash|Backlash]] has a significant impact on Arm mechanisms. 
- The mechanism length means that as backlash worsens, even a minor angular difference can result in notable end-effector position errors. 
- If the backlash occurs on the mounting arm, this desyncs both encoders from the arm position. If it occurs inside the gear train, then it reduces the alignment between absolute encoder and relative encoders.
- The effect of backlash causes position stability errors as the arm sweeps through a motion: at the lower hard limit (random backlash alignment), as it goes up/down (aligned to bottom of teeth), as it approaches vertical (random again), and flipped (aligned to opposite side of teeth). This means your position tolerance is +/-(backlash/2) or -0/+backlash
Like all backlash, a mechanical fix should be prioritized, but should also be considered in software.  In the case where the system operates in a single mode (indicated above), this is easy to resolve. For systems that operate within multiple quadrants, careful choices of the 

Care should be taken when using Absolute encoders directly when backlash is involved; In this configuration, the arm bouncing actually generates error, causing a system response *opposite* the arm motion. This can easily generates thrashing, quickly worsening the backlash and putting strain on the arm. However, the actual bounce will generally be more constrained, improving precision.
In contrast, when using the motor's relative encoder, the bounce is not captured, resulting in no response. You'll have a bouncy arm, but will not have accelerated forces worsening backlash.

## Tuning Tips

#### Finding kG+kS
kG is the first step toward an arm being remotely controllable, but not easily gotten until you get an approximate value first. This is easily gotten in a couple simple steps:
- Set the P value of your system to be relatively low, guessable value
- Set the arm position to horizontal, and let it settle (it will not likely be at horizontal)
- Read the output (in volts), and set as your kG
- Repeat once or twice until the arm is approximately horizontal

Now, you can reliably use the standard method for finding kG+kS: Move the system up/down, recording the voltages to do it. The difference is kS, and the average is kG.

#### Finding kV+kA
Standard [[Motion Profiles|Motion Profile]] practices apply: Increase kV until your system is accurate in tracking the setpoints over time. 
Increase kA as needed to better handle the velocity transitions. This is more likely to be needed on arms than most other systems.

#### Finding kP
kP is relatively simple: Just increase the value until it starts oscillating, and then decrease them. Be careful with high PID gains: If the system starts bouncing due to backlash, a high kP can incite thrashing.

#### Finding kI +kD
These are often unnecessary, but can help with certain cases where the system has inconsistent loading caused by variances in game piece weights, or if the system itself is not reliably constructed. 

kD can reduce (and/or cause) PID thrashing, and kI can help cover for minor discrepencies that might prevent you from hitting goal states. However, it's important to constrain kI using the `izone` tools. Remember: the PID is a linear response, but system disturbances such as game piece weight are *nonlinear* (sinusoidal, specifically). As such, kI is mostly ineffective in responding to major changes in system dynamics without inducing other errors.

In cases where your system dynamics change significantly based due to the game piece, consider [[FeedForwards#Modelling changing systems|modelling game pieces]]  as part of your feedforward. This will be faster and more reliable than kI and kD.


