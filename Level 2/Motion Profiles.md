---
aliases:
  - Motion Profile
  - Trapezoidal Profile
---
## Success Criteria
- [ ] Configure a motion system with PID and FeedForward
- [ ] Add a trapezoidal motion profile command (runs indefinitely)
- [ ] Create a decorated version with exit conditions on reaching the target
- [ ] Create a small auto sequence to cycle multiple points
- [ ] Create a set of buttons for different setpoints

## Synopsis

Motion Profiling is the process of generating smooth, controlled motion. This is typically done using controlled, intermediate setpoints, consideration of the system's physical properties, and other imposed limitations. 

In FRC our tooling generally utilizes a Trapazoidal profile, allowing us precise control of system position, maximum system speed, and acceleration applied to the system.

## Benefits of Motion profiling

Motion Profiling resolves several problems that can come up with "simpler" control systems such as simple open loop or straight PID control. 

The first comes from acknowledgement of the basic physics equation $F=m*a$ : The faster your system is asked to accelerate, the more force is being applied through your entire geartrain. On a robot, this generates significant stress, and over time will damage your gears, stretch chains and ropes, and in some cases immediately break your robot. This comes up any time your output changes significantly: Such as 0->1 or 1->0. No good! A motion profile can control the acceleration, significantly reducing related issues. 

The next relates to "tuning": The process of adjusting robot parameters to generate consistent motion. If you've gone through the [[PID]] tuning process, you probably remember one struggle: Your tuning works *great* until you move the setpoint a larger distance, at which point it wildly misbehaves, and the system acts erratically. This case is caused by sharp changes in system error, resulting in the PID generating a large output. Motion profiles instead change the setpoint at a controlled rate, ensuring a small system error keeping the PID in a much more stable state. 

The extra complexity is worth it! Tuning a system using a Motion Profile is *significantly* less work than without it, since all the biggest pain points are eliminated. Your PID behaves better, with no overshoot or sharp outputs, and your system  


## How it works

> [!NOTE]
> For these purposes, we're going to discuss a "positional" control system, such as an [[SuperStructure Elevator|Elevator]] or [[SuperStructure Arm|Arm]] . 
> 
> Motion Profiles still apply to velocity control [[SuperStructure Rollers|Rollers]] and [[SuperStructure Flywheel|Flywheels]]; In those cases, we ignore the position, smoothly accelerating to our target velocity. This means we only see half the benefit on those systems.

The system we typically use is a "Trapezoidal" profile, named after the shape of the velocity graph it generates. 

This system has just two parameters: 
- A maximum velocity
- a maximum acceleration. 

By having these values set at values the physical system can achieve, our motion profile can split up one large motion into 3 segments. 
- An acceleration step, where the motor is approaching the max speed
- Running at our max speed 
- Decelerating on approach to the final position. 

Because our Motion Profile is *aware* of our system capabilities, it can then constrain our setpoint changes to ones that that our system can actually achieve, generating a smooth motion without overshooting our final target (or goal). 

Since we know how long it takes to accelerate and decelerate, and the max speed, we can also predict exactly how long the whole motion takes!

The entire motion looks just like this:
![[motion-profile-time-plots.png]]



#### Finding Max Velocity

A theoretical maximum can be found by multiplying your maximum motor RPM by your gear reduction: It's very similar to configuring your encoder conversion factor, then hitting it with your maximum velocity. 

In practice, you normally want to start by setting it slow, at something that you can visually track: Often ~1-2 seconds for a specific range of motion. This helps with testing, since you can see it work and mentally confirm whether it's what you expect. 

As you improve and tune your system, you can simply then increase the maximum until your system feels unsafe, and then go back down a bit.

In many practical FRC systems, you may not hit the actual maximum speed of your system: Instead, the system well simply accelerate to the halfway point and then decelerate. This is normal, and simply means the system is constrained by the acceleration.

#### Finding Max Acceleration

The maximum acceleration effectively represents the actual force we're telling the motor to apply. It's easiest to understand at the extremes:
- When set to zero, your system will never have force applied; It won't move at all. 
- When set to infinite, your system assumes it can reach to the max velocity instantly; This is effectively just applying as much force as your system is configured for.

 However, in actual systems you want them to move, so zero is useless. And infinite is useful, but impractical: Thinking back to our equation, $F=m*a$  and rearranging to $F/m=a$ , we only get "infinite" when we have infinite motor force and/or an object of zero weight.

This final form helps us clearly see we have a maximum: Our output force is is defined by the motor's max output and our system's mass, giving us a maximum constraint: $a <= F/m$ . 

Note this is highly simplified: Actually calculating max acceleration this way on real-world systems is often non-trivial, and involves *significantly* more variables and equations than listed here. However, it's the concept that's important.

In practice, the easiest way to find acceleration is to simply start small increase the acceleration until the system moves how you want. If it starts making concerning noises or over-shooting, then you've gone too far, and you should back it off.

## Revisiting the graph

With some background, there's a couple ways to visually interpret this graph:  

- This graph always represents the generated profile, but *also* should reflect your *actual* position too! When configured correctly, the generated graph is always within our system's capability. 
- The calculated  "position" setpoint is generally what we feed to our system's PID output.
- The "acceleration" impacts the angle on our velocity trapezoid! At infinite, it's vertical, and at zero, it's horizontal. 


![[motion-profile-time-plots.png]]



## Interactions with FeedForwards

Having a motion profile also enables inputs for [[FeedForwards]], allowing even higher levels of precision on your expected outputs.

At the basic level, positional PIDs can be trivially configured with kG and kS, which only depend on values known on all systems. kG is constant or depends on position, and kS depends only on direction of motion.

However, the kV and kA gains both depend on not just on position, but also a known system acceleration and velocity targets.... which we haven't had with arbitrary setpoint jumps.  

But now, we can plan our motion: Giving us velocity and acceleration targets. With the right feedforwards, we can now compute the moment-to-moment output for the entire motion in advance! 


## Interaction with PIDs

The most important part is that our error changes from one big setpoint jump to a lot of small, properly calculated ones. 

Without a feed-forward, simply having a motion profile completely removes the big transitions and associated output spikes. This makes tuning simpler, less critical, and often allows for more aggressive PID gains without problems. However, many positional systems will usually still need an I term to accurately hit setpoints accurately without rapid oscillations from a high P term. 

With a partial FeedForward (kG + kS) the Feed-Forward handles the base lifting, reducing the need of a PID to handle gravity. This leaves the PID left to handle the motion itself, and it can easily hit setpoint targets without an I term, barring weight changes (such as loaded game pieces)

With a full FeedForward, the error between commanded motion and actual motion will be extremely small, making the PID's impact almost completely irreverent. This makes the PID tuning extremely easy, and the gains can be tuned for precisely the expected disturbances you'll encounter, such as impacts or weight variation.
Note, that despite having theoretically minimal impact, you would *still* always want a PID to ensure the system gets back to position in cause of error.

## Tuning Errors and Adjustments

Fortunately, with motion profiles you get a lot, with very little in the way of potential problems.

Most errors can be easily captured by looking at the position and velocity graph of a real system, and applying a bit of reasoning on what line isn't matching up.

- Overshooting the position setpoint at the end: This likely means your acceleration is too high, or your I gain is too large. 
- The system is lagging behind the setpoints and the lines are diverting entirely. It then overshoots the setpoint: Your system cannot output enough power to meet your acceleration constraint. Make sure you have appropriate current limits, and/or lower the acceleration within the system capability.
- The system is lagging behind the setpoints so the lines are parallel but offset: This is typical when not using kA+kV, or when they're set too low.  The difference is how long it takes the PID control to kick in and make up for what kA+kV should be generating for that motion.
- The motion is very jerky when travelling at the max speed: This usually means kV is too high, but can be kS being too high. Lowering kP may help as well. 
- It starts really awful and smooths out: Likely kA is too high, although again kS and kP can influence this.

## Implementing \<system type here> 

The full system and example code is part of the system descriptions: 
- [[SuperStructure Elevator|Elevator]]
- [[SuperStructure Arm|Arm]]
- [[SuperStructure Rollers|Roller]]
- [[SuperStructure Flywheel|Flywheel]]

The most effective way in general is using the WPILib [ProfiledPIDController](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/profiled-pidcontroller.html) providing the simplest complete implementation.

This works as a straight upgrade to the standard PID configuration, but takes 2 additional parameters that grant huge performance gains and easier tuning. 

```java
ExampleElevator extends SubsystemBase{
	SparkMax motor = new SparkMax(42,kBrushless);
	//Configure reasonable profile constraints
	private final TrapezoidProfile.Constraints constraints =
		new TrapezoidProfile.Constraints(kMaxVelocity, kMaxAcceleration);
	//Create a PID that obeys those constraints in it's motion
	//Likely, you will have kI=0 and kD=0
	//Note, the unit of our PID will need to be in Volts now.
	private final ProfiledPIDController controller =
		new ProfiledPIDController(kP, kI, kD, constraints, 0.02);
	//Our feedforward object appropriate for our subsystem of choice
	//When in doubt, set these to zero (equivilent to no feedforward)
	//Will be calculated as part of our tuning.
	private final ElevatorFeedforward feedforward = 
		new ElevatorFeedforward(kS, kG, kV);
		
	//Lastly, we actually use our new 
	public Command setHeight(Supplier<Distance> position){
		return run(
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
```

The big difference compared to our other, simpler [[PID]] is that we're using `motor.setVoltage` for this; This relates to the inclusion of [[FeedForwards]] which prefers volts for improved consistency as the battery drains. 
While the PID itself doesn't care, since we're adding them together they *do* need to be the same unit. 

If you already calculated your PID gains using `motor.set()` or 