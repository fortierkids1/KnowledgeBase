---
tags:
  - stub
---

Requires 
[[Commands]]
[[Encoder Basics]]

## Success Criteria
- [ ] Create a PID system on a test bench
- [ ] Tune necessary PIDs using encoders
- [ ] Set a velocity using a PID
- [ ] Set a angular position using a PID
- [ ] Set a elevator position using a PID
- [ ] Plot the system's position, target, and error as you command it.


# TODO

TODO:
Add some graphs
https://github.com/DylanHojnoski/obsidian-graphs
Write synopsis
https://docs.revrobotics.com/revlib/spark/closed-loop
## Synopsis
A PID system is a [[Closed Loop Controller]] designed to reduce system error through a simple, efficient mathematical approach.

You may also appreciate Chapter 1 and 2 from [[controls-engineering-in-frc.pdf]] , which covers PIDs very well. 



![[Closed Loop Controller#What's a Closed Loop Controller]]


## Definitions:

Before getting started, we  need to identify a few things: 
- A setpoint: This is the goal state of your system. This will have units in that target state, be it height, meters, rotations/second, or whatever you're trying to do. 
- An output: This is often a motor actuator, and likely 
- A measurement: The current state of your system from a sensor; It should have the same units as your Setpoint.
- Controller: The technical name for the logic that is controlling the motor output. In our case, it's a PID controller, although many types of controllers exist.

## Deriving a PID Controller from scratch

To get an an intuitive understanding about PIDs and feedback loops, it can help to start from scratch, and kind of recreating it from the basic assumptions and simple code.

Let's start from the core concept of "I want this system to go to a position and stay there". 

Initially, you might simply say "OK, if we're _below_ the target position, go up.  If we're above the target position, go down." This is a great starting point, with the following pseudo-code.

```java
setpoint= 15  //your target position, in arbitrary units
sensor= 0 //Initial position
if(sensor < setpoint){ output = 1 }
else if(sensor > setpoint){ output = -1 }
motor.set(output)
```

However, you might see a problem. What happens when setpoint and sensor are _equal_? 

If you responded with "It rapidly switches between full forward and full reverse", you would be correct. If you also thought "This sounds like it might damage things", then you'll understand why this controller is named a "Bang-bang" controller, due to the name of the noises it tends to make.

Your instinct for this might be to simply _not go full power_. Which doesn't solve the problem, but reduces it's negative impacts. But it also creates a new problem. Now it's going to oscillate at the setpoint (but less loudly), and it's _also_ going to take longer to get there.

So, let's complicate this a bit. Let's take our previous bang-bang, but split the response into two different regions: Far away, and closer. This is easier if we introduce a new term: Error. Error just represents the difference between our setpoint and our sensor, simplifying the code and procedure. "Error" helpfully is a useful term, which we'll use a lot.

```java
run(()->{
	setpoint= 15  //your target position, in arbitrary units
	sensor= 0 //read your sensor here
	error = setpoint-sensor 
	if     (error > 5){ output = -1 }
	else if(error > 0){ output = -0.2 }
	else if(error < 0){ output = 0.2 }
	else if(error < -5){ output = 1 }
	motor.set(output)
})
```

We've now slightly improved things; Now, we can expect more reasonable responses as we're close, and fast responses far away. But we still have the same problem; Those harsh transitions across each else if. Splitting up into more and more branches doesn't seem like it'll help. To resolve the problem, we'd need an infinite number of tiers, dependent on how far we are from our targets. 

With a bit of math, we can do that! Our `error` term tells us how far we are, and the sign tells us what direction we need to go... so let's just scale that by some value. Since this is a constant value, and the resulting output is proportional to this term, let's call it kp: Our proportional constant. 

```java
run(()->{
	setpoint= 15  //your target position, in arbitrary units
	sensor= 0 //read your sensor here
	kp = 0.1
	error = setpoint-sensor 
	output = error*kp
	motor.set(output)
)}
```

Now we have a better behaved algorithm! At a distance of 10, our output is 1. At 5, it's half. When on target, it's zero! It scales just how we want. 

Try this on a real system, and adjust the kP until your motor reliably gets to your setpoint, where error is approximately zero. 

In doing so, you might notice that you can _still_ oscillate around your setpoint if your gains are too high. You'll also notice that as you get closer, your output drops to zero. This means, at some point you stop being able to get closer to your target. 

This is easily seen on an elevator system. You know that gravity pulls the elevator down, requiring the motor to push it back up. For the sake of example, let's say an output of 0.2 holds it up. Using our previous kP of 0.1, a distance of 2 generates that output of 0.2. If the distance is 1, we only generate 0.1... which is not enough to hold it! Our system actually is only stable _below_ where we want. What gives! 

 This general case is referred to as "standing error" ; Every loop through our PID fails to reduce the error to zero, which eventually settles on a constant value. So.... what if.... we just add that error up over time? We can then incorporate that error into our outputs. Let's do it.

```java
setpoint= 15  //your target position, in arbitrary units
errorsum=0
kp = 0.1
ki = 0.001
run(()->{
	sensor= 0 //read your sensor here
	error = setpoint-sensor
	errorsum += error
	output = error*kp + errorsum*ki
	motor.set(output)
}
```

The mathematical operation involved here is called integration, which is what this term is called. That's the "I" in PID. 
In many practical FRC applications, this is probably as far as you need to go! P and PI controllers can do a lot of work, to suitable precision. This a a very flexible, powerful controller, and can get "pretty good" control over a lot of mechanisms. 

This is probably a good time to read across the  [WPILib PID Controller](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/pidcontroller.html)  page; This covers several useful features. Using this built-in PID, we can reduce our previous code to a nice formalized version that looks something like this.
```java
PIDController pid = new PIDController(kP, kI, kD);
run(()->{
	sensor = motor.getEncoder.getPosition();
	motor.set(pid.calculate(sensor, setpoint))
})
```

A critical detail in good PID controllers is the iZone or ErrorZone. We can easily visualize what problem this is solving by just asking "What happens if we get a game piece stuck in our system"?
Well, we _cannot_ get to our setpoint. So, our errorSum gets larger, and larger.... until our system is running _full power_ into this obstacle. That's not great. Most of the time, _something_ will break in this scenario. 

So, the iZone allows you to constrain the amount of error the controller actually stores. It might be hard to visualize the specific numbers, but you can just work backward from the math. If `output = errorsum*kI`, then `maxIDesiredTermOutput=iZone*kI`. So `iZone=maxIDesiredTermOutput/kI`.



Lastly, what's the D in PID?

Well, it's less intuitive, but let's try. Have you seen the large spike in output when you change a setpoint? Give the output a plot, if you so desire. For now, let's just reason through a system using the previous example PI values, and a large setpoint change resulting in an error of 20. 

Your PI controller is now outputting a value of 2.0 ; That's double full power! Your system will go full speed immediately with a sharp jolt, have a ton of momentum at the halfway point, and probably overshoot the final target. So, what we want to do is constrain the speed; We want it _fast_ but not _too_ fast. So, we want to reduce it according to how fast we're going.
Since we're focusing on error as our main term, let's look at the rate the error changes. When the error is changing *fast* we want to _reduce_ the output. The difference is simply defined as `error-previousError`, so a similar strategy with gains gives us `output+=kP*(error-previousError)` . 
This indeed gives us what we want: When the rate of change is high, the contribution is negative and large; Acting to reduce the total output, slowing the corrective action.


However, this term has another secret power, which disturbance rejection. Let's assume we're at a steady position, and the system is settled, and `error=0`. Now, let's bonk the system downward, giving us a sudden large, positive error. Suddenly `nonzero-0` is positive, and the system generates a upward force. For this interaction, all components of the PID are working in tandem to get things back in place quickly.

Adding this back in, gives us the fundamental PID loop:
```java
setpoint= 15  //your target position, in arbitrary units
errorsum=0
lastSensor=0
kp = 0.1
ki = 0.001
kd = 0.01
run(()->{
	sensor= 0 //read your sensor here
	error = setpoint-sensor
	errorsum += error
	errordelta = sensor-lastSensor
	lastSensor=sensor
	output = error*kp + errorsum*ki + errordelta*kd
	motor.set(output)
}
```

## Limitations of PIDs
OK, that's enough nice things. Understanding PIDs requires knowing when they work well, and when they don't, and when they actually cause problems. 

- PIDs are reactive, not predictive. Note our key term is "error" ; PIDs only act when the system is _already_ not where you want it, and must be far enough away that the generated math can create corrective action.
- Large setpoint changes break the math. When you change a setpoint, the P output gets really big, really fast, resulting in an output spike. When the PID is acting to correct it, the errorSum for the I term is building up, and cannot decrease until it's on the other side of the setpoint. This almost _always_ results in overshoot, and is a pain to resolve.  
- Oscillation: PIDs inherently generate oscillations unless tuned perfectly. Sometimes big, sometimes small.
- D term instability: D terms are notoriously quirky. Large D terms and velocity spikes can result in bouncy, jostly motion towards setpoints, and can result in harsh, very rapid oscillations around the zero, particularly when systems have significant [[Mechanical Backlash]].
- PIDS vs Hard stops: Most systems have one or more [[Hard Stops]], which present a problem to the I term output. This requires some consideration on how your encoders are initialized, as well as your setpoints.
- Tuning is either simple....or very time consuming.
- Only works on "Linear" systems: Meaning, systems where the system's current state does not impact how the system responds to a given output. [[SuperStructure Arm|Arms]] are an example of a *non*-linear system, and to a given output very differently when up and horizontally. These cannot be properly controlled by *just* a PID. 

So, how do you make the best use of PIDs?
- Reduce the range of your setpoint changes. There's a few ways to go about it, but the easiest are [[clamping]] changes, [[Slew Rate Limiting]] and [[Motion Profiles]] . With such constraints, your error is always small, so you can tune more aggressively for that range. 
- Utilize [[FeedForwards]] to create the basic action; Feed-forwards create the "expected output" to your motions, reducing the resulting error significantly. This means your PID can be tuned to act sharply on disturbances and unplanned events, which is what they're designed for.

In other words: This is an *error correction* mechanism. By reducing or controlling the initial error a PID would act on, you can greatly simplify the PID's affect on your system, usually making it easier to get better motions. Using a PID as the "primary action" for a system might work, but tends to generate unexpected challenges.



## Tuning

Tuning describes the process of dialing in our "gain values"; In our examples, we named these kP, kI, and kD. These values don't change the *process* of our PID, but it changes how it responds.

There's actually several "formal process" for tuning PIDs; However, in practice these often are more complicated and aggressive than we really want. You can read about them if you'd like [PID Tuning via Classical Methods](https://eng.libretexts.org/Bookshelves/Industrial_and_Systems_Engineering/Chemical_Process_Dynamics_and_Controls_(Woolf)/09%3A_Proportional-Integral-Derivative_(PID)_Control/9.03%3A_PID_Tuning_via_Classical_Methods)

In practice though, the typical PID tuning process is more straightforward, but finicky.
- Define a small range you want to work with: This will be a subset of 
- Create a plot of your setpoint, current state/measurements, and system output. [[Basic Telemetry]] is usually good enough here.
- Starting at low values, increase the P term until your system starts to oscillate near the goal  state. Reduce the P term until it doesn't. Since you can easily 
- Add an I term, and increase the value until your system gets to the goal state with minimal overshoot. Often I terms should start very small; Often around 1%-10% of your P term. Remember, this term is summed *every loop*; So it can build up very quickly when the error is large. 
- If you're tuning a shooter system, get it to target speed, and feed in a game piece; Increase the D term until you maintain the RPM to an effective extent. 


> [!NOTE] Rev Velocity Filtering
> Rev controllers by default implement a velocity filter, making it nearly impossible to detect rapid changes in system velocity. This in turn makes it nearly impossible to tune a D-term.
> #todo Document how to remove these filters


> [!DANGER] Hazards of Tuning
> Be aware that poorly tuned PIDs might have very unexpected, uncontrolled motions, especially when making big setpoint changes.
> They can jolt unexpectedly, breaking chains and gearboxes. They can overshoot, slamming into endstops and breaking frames. They'll often oscillate shaking loose cables, straps, and stressing your robot.
> Always err on the side of making initial gains smaller than expected, and focus on safety when tuning. 


> [!DANGER] Setpoint Jumps + Disabled robots
> Remember that for PID systems the setpoint determines motor output;  If the bot  is disabled, and then re-enabled, the bot *will* actuate to the setpoint!
> Make sure that your bot handles re-enabling gracefully; Often the best approach is to re-initialize the setpoint to the bot's current position, and reset the PID controller to clear the I-term's error sum.





## Streamlining tuning the proper way

In seasons past, a majority of our programming time was *just* fiddling with PID values to get the bot behaviour how we want it. This *really sucks*. Instead, there's more practical routines to *avoid* the need for precision PID tuning. 

- Create a plot of your setpoint, current state/measurements, and system output. [[Basic Telemetry]] is usually good enough here.
- Add a [[FeedForwards|FeedForward]] : It doesn't have to be perfect, but having a basic model of your system *massively* reduces the error, and significantly reduces time spent fixing PID tuning. This is *essential* for Arms; The FeedForward can easily handle the non-linear aspects that the PID struggles with.
- In cases where game pieces contribute significantly to the system load, account for it with your FeedForward: Have two different sets of FeedForward values for the loaded and unloaded states
- Use [[Motion Profiles|Motion Profiles]]: A Trapezoidal profile is optimal and remarkably straightforward. This prevents many edge cases on PIDs such as sharp transitions and overshoot. It provides very controlled, rapid motion. 
	- Alternatively, reduce setpoint changes through use of a Ramp Rate or [[Slew Rate Limiting]]. This winds up being as much or more work than Motion Profiles with worse results, but can be easier to retrofit in existing code.
	- An even easier and less effective option is simply [[clamping|Clamp]] clamp the setpoint within a small range around the current state. This provides a max error, but does not eliminate the sharp transitions.
- Set a *very small* ClosedLoopRampRate; Just enough to prevent high-frequency oscillations, which will tend to occur when the setpoint is at rest, especially against [[Hard Stops]] or if [[Mechanical Backlash|Backlash]] is involved. This is just a [[Slew Rate Limiting|Slew Rate Limiter]] being run on the motor controller against the output.

From here, the actual PID values are likely to barely matter, making tuning extremely straightforward: 
- Increase the P term until you're on target through motions and not oscillating sharply at rest
- Find a sensible output value that fixes static/long term disturbances (change in weight, friction, etc). Calculate the target iZone  to a sensible output just above what's needed to fix those. 
- Start with I term of zero; Increase the I term if your system starts lagging during some long motions, or if it sometimes struggles to reach setpoint during
- If your system is expected to maintain it's state through predictable disturbances (such as maintaining shooter RPM when launching a game piece), test the system against those disturbances, and increase the D term as needed. You may need to decrease the P term slightly to prevent oscillations when doing this.
- Watch your plots. A well tuned system should 
	- Quickly approach the target goal state
	- Avoid overshooting the target
	- Settle on a stable output value
	- Recover to the target goal state (quickly if needed)
	
## TODO

- Discontinuity + setpoint wrappping for PIDs + absolutes
- 