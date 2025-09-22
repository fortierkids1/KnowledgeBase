Requires:
[[Motor Control]]

## Success Criteria
- [ ] Create a velocity FF for a roller system that enables you to set the output in RPM
- [ ] Create a gravity FF for a elevator system that holds the system in place without resisting external movement
- [ ] Create a gravity FF for an arm system that holds the system in place without resisting external movement

## Synopsis
Feedforwards model an expected motor output for a  system to hit specific target values. 
The easiest example is a motor roller. Let's say you want to run at ~3000 RPM. You know your motor has a top speed of ~6000 RPM at 100% output, so you'd correctly expect that driving the motor at 50% would get about 3000 RPM. This simple correlation is the essence of a feed-forward. The details are specific to the system at play. 

## Explanation
The WPILib docs have good fundamentals on feedforwards that is worth reading.
https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/feedforward.html

### Tuning  Parameters
Feed-forwards are specifically tuned to the system you're trying to operate, but helpfully fall into a few simple terms, and straightforward calculations. In many cases, the addition of one or two terms can be sufficient to improve and simplify control. 

#### kS  : Static constant
The simplest feedforward you'll encounter is the "static feed-forward". This term represents initial momentum, friction, and certain motor dynamics. 

You can see this in systems by simply trying to move _very_ slow. You'll often notice that the output doesn't move it until you hit a certain threshhold. That threshhold is approximately equal to kS.

The static feed-forward affects output according to the simple equation of  $output = kS*sign(directionOfMotion)$

#### kG : Gravity constant
a kG value effectively represents the value needed for a system to negate gravity. 

Elevators are the simpler case: You can generally imagine that since an elevator has a constant weight, it should take a constant amount of force to hold it up. This means the elevator Gravity gain is simply a constant value, affecting the output as $output=kG$ ; You don't need any other considerations regarding the system motion, because gravity is always constant. 

A more complex kG calculation is needed for pivot or arm system. You can get a good sense of this by grabbing a heavy book, and holding it at your side with your arm down. Then, rotate your arm outward, fully horizontal. Then, rotate your arm all the way upward. You'll probably notice that the book is much harder to hold steady when it's horizontal than up or down.

The same is true for these systems, where the force needed to counter gravity changes based on the angle of the system. To be precise, it's maximum at horizontal, zero when directly above or below the pivot. Mathematically, it follows the function $cos(angle)$ ratio, lending this version of the feed-forward the nickname kCos.

This form of the gravity constant affects the output according to 
$output = kG*cos(currentAngle)$, where $kG$ is is the maximum output, at horizontal. [^kcos] 

#### kV : Velocity constant
The velocity feed-forward represents the expected output to maintain a target velocity. This term accounts for physical effects like dynamic friction and air resistance, and a handful of  

This is most easily visualized on systems with a velocity goal state. In that case, $kV$ is easily known, and contributes to the output as $output = kV*targetVelocity$ .

In contrast, for positional control systems, knowing the desired system velocity is quite a challenge. In general, you won't know the target velocity unless you're using a [[Motion Profiles]] to to generate the instantaneous velocity target. 

#### kA : Acceleration constant
The acceleration feed-forward largely negates a few inertial effects. It simply provides a boost to output to achieve the target velocity quicker.

like $kV$, $kA$ is typically only known when you're working with [[Motion Profiles]]. 


#### The equations of FeedForward

Putting this all together, it's helpful to de-mystify the math happening behind the scenes.

The short form is just a re-clarification of the terms and their units
$kG$: Output to overcome gravity ($output$)
$kS$: Output to overcome static friction ($output$)
$kV$: Output per unit of target velocity ($output/velocity$)
$kA$: Output per unit of target acceleration ($output/(velocity^2)$)

A roller system will often simply be 
$$output = kS(signOf(velocity)) + kV*velocity + kA*\Delta velocity$$
If you don't have a motion profile, kA will simply be zero, and and kS might also be negligible unless you plan to operate at very low RPM.

An elevator system will look similar:
$$output = kS(signOf(velocity)) + kV*velocity + kA*\Delta velocity + kG$$
Without a motion profile, you cannot properly utilize kV and kA, which simplifies down to 
$$output = kS(signOf(velocity)) + kG$$
where $velocity$ is generally derived by $\Delta position$ (since you know the current and previous positions).

Lastly, elevator systems differ only by the cosine term to scale kG. 
$$output = kS(signOf(velocity)) + kV*velocity + kA*\Delta velocity + kG*cos(currentAngle)$$
Again simplifying for systems with no motion profile, you get
$$output = kS(signOf(velocity)) + kG*cos(currentAngle)$$
It's helpful to recognize that because the angle is being fed to a $sin$ function, you cannot use degrees here! Make sure to convert. 


Of course, the intent of a feed-forward is to model your mechanics to improve control. As your system increases in complexity, and demands for precision increase, optimal control might require additional complexity! A few common cases: 
- If you have a pivot arm that extends, your kG won't be constant! 
- Moving an empty system and one loaded with heavy objects might require different feed-forward models entirely.
- Long arms might be impacted by motion of systems they're mounted on, like elevators or the chassis itself! You can add that in and apply corrective forces right away.


#### Feed-forward vs feed-back

Since a feed-forward is prediction about how your system behaves, it works very well for fast, responsive control. However, it's not perfect; If something goes wrong, your feed-forward simply doesn't know about it, because it's not _measuring_ what actually happens. 

In contrast, feed-back controllers like a  [[PID]] are designed to act on the error between a system's current state and target state, and make corrective actions based on the error. Without first encountering system error, it doesn't do anything.

The combination of a feed-forward along with a feed-back system is the power combo that provides robust, predictable motion.


## FeedForward Code

WPILib has several classes that streamline the underlying math for common systems, although knowing the math still comes in handy! The docs explain them (and associated warnings) well. 
https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/feedforward.html

Integrating in a robot project is as simple as crunching the numbers for your feed-forward and adding it to your motor value that you write every loop.


```java
ExampleSystem extends SubsystemBase(){

	SparkMax motor = new SparkMax(...)
	// Declare our FF terms and our object to help us compute things.
	double kS = 0.0;
	double kG = 0.0;
	double kV = 0.0;
	double kA = 0.0;
	ElevatorFeedforward feedforward = new ElevatorFeedforward(kS, kG, kV, kA);
	
	ExampleSubsystem(){}

	Command moveManual(double percentOutput){
		return run(()->{
			var output ;
			//We don't have a motion profile or other velocity control
			//Therefore, we can only assert that the velocity and accel are zero
			output = percentOutput+feedforward.calculate(0,0);
			// If we check the math, this feedforward.calculate() thus 
			// evaluates as simply kg;
			
			// We can improve this by instead manually calculating a bit
			// since we known the direction we want to move in
			output = percentOutput + Math.signOf(percentOutput) + kG;
			motor.set(output);
		})
	}

	Command movePID(double targetPosition){
		return run(()->{
			//Same notes as moveManual's calculations 
			var feedforwardOutput = feedforward.calculate(0,0);
			// When using the Spark closed loop control, 
			// we can pass the feed-forward directly to the onboard PID
			motor
			.getClosedLoopController()
			.setReference(
				targetPosition,
				ControlType.kPosition,
				ClosedLoopSlot.kSlot0,
				feedforwardOutput, 
				ArbFFUnits.kPercentOut
			);
			//Note, the ArbFFUnits should match the units you calculated!
		})
	}

	Command moveProfiled(double targetPosition){
		// This is the only instance where we know all parameters to make 
		// full use of a feedforward.
		// Check [[Motion Profiles]] for further reading
	}
	
}
```



> [!TIP] 2026 update?
> Rev released a new FeedForward config API that might allow advanced feed-forwards to be run directly on controller. Look into it and add examples!
> https://codedocs.revrobotics.com/java/com/revrobotics/spark/config/feedforwardconfig


## Finding Feed-Forward Gains


> [!warning] High gains
> When tuning feed-forwards, it's helpful to recognize that values being _too high_ will result in notable problems, but gains being _too low_ generally result in lower performance. 
> Just remember that the lowest possible value is 0; Which is equivalent to not using that feed forward in the first place. Can only improve from there. 

#### Finding kS and kG
These two terms are defined at the boundary between "moving" and "not moving", and thus are closely intertwined. Or, in other words, they interfere with finding the other. So it's best to find them both at once.

It's easiest to find these with manual input, with your controller input scaled down to give you the most possible control.

Start by positioning your system so you have room to move both up and down. Then, hold the system perfectly steady, and increase output until it *just barely* moves upward. Record that value. 
Hold the system stable again, and then decrease output until it just *barely* starts moving down. Again, record the value. 

Thinking back to what each term represents, if a system _starts_ moving up, then the provided input _must_ be equal to $kg+ks$; You've negated both gravity _and_ the friction holding it in place. Similarly, to start moving down, you need to be applying $kg-ks$. This insight means you can generate the following two equations  

$$ ks = (BarelyGoesUpValue - BarelyGoesDownValue)/2 $$
$$ kg =  (BarelyGoesUpValue + BarelyGoesDownValue)/2 $$

Helpfully, for systems where $kG=0$ like roller systems, several terms cancel out and you just get $kS= BarelyMovesValue$ .

For pivot/arm systems, this routine works as described _if_ you can calculate kG at approximately horizontal. It cannot work if the pivot is vertical. If your system cannot be held horizontal, you may need to be creative, or do a bit of trig to account for your recorded $kG$ being decreased by $cos(angle)$

Importantly, this routine actually returns a kS that's often *slightly* too high, resulting in undesired oscillation. That's because we recorded a *minimum* that _causes_ motion, rather than the _maximum_ value that _doesn't_ cause motion. Simply put, it's easier to find this way. So, we can just compensate by reducing the calculated kS slightly; Usually multiplying it by 0.9 works great. 

#### Finding roller kV
Because this type of system system is also relatively linear and simple, finding it is pretty simple. We know that $output=kV*targetRPM$, and expect $maxOutput = kv*maxTargetRPM$. 

We know $maxTargetRPM$ is going to be constrained by our motor's maximum RPM, and that maxOutput is defined by our api units (either +/-1.0 for "percentage" or +/-12 for "volt output"). 

This means we can quickly assert that $kV$ should be pretty close to $maxOutput / maxVelocity$. 
#### Finding kV+Ka 

Beyond roller kV, kA and kV values are tricky to identify with simple routines, and require [[Motion Profiles]] to take advantage of. As such, they're somewhat beyond the scope of this article.

The optimal option is using [[System Identification]] to calculate the system response to inputs over time. This can provide optimal, easily repeatable results. However, it involves a lot of setup, and potentially hazardous to your robot when done without caution.

The other option is to tune by hand; This is not especially challenging, and mostly involves a process of moving between goal states, watching graphs, and twiddling numbers. It usually looks like this:
- Identify two setpoints, away from hard stops but with sufficient range of motion you can hit target velocities. 
- While cycling between setpoints, ihen increase kV until the system generates velocities that match the target velocities. They'll generally lag behind during the accelleration phase. 
- Then, increase kA until the accelleration shifts and the system perfectly tracks your profile. 
- Increase profile constraints and and repeat until system performance is attained. Starting small and slow prevents damage to the mechanics of your system.

This process benefits from a relatively low P gain, which helps keep the system stable. Once your system is tuned, you'll probably want a relatively high P gain, now that you can assert the feed-forward is keeping your error close to zero. 






[^kcos]:Note, you might observe that the kCos output,  $output=kg*Cos(current Angle)$ is reading the current system state, and say "hey!  That's a feed _back_ system, not a feed _forward_!" and you are technically correct; the best kind of correct. However, kCos is often implemented this way, as it's much more stable than the feed-forward version. In that version, you apply $kCos(expectedAngle)$, regardless of what $actualAngle$ happens to actually be. Feel free to do a thought experiment on how this might present problems in real-world systems.