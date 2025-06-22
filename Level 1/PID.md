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

## Synopsis
A PID system is a [[Closed Loop Controller]] designed to reduce system error through a simple, efficient mathematical approach.

You may also appreciate Chapter 1 and 2 from [[controls-engineering-in-frc.pdf]] , which covers PIDs very well. 

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

A critical detail in good PID controllers is the iZone. We can easily visualize what problem this is solving by just asking "What happens if we get a game piece stuck in our system"?
Well, we _cannot_ get to our setpoint. So, our errorSum gets larger, and larger.... until our system is running _full power_ into this obstacle. That's not great. Most of the time, _something_ will break in this scenario. 

So, the iZone allows you to constrain the amount of error the controller actually stores. It might be hard to visualize the specific numbers, but you can just work backward from the math. If `output = errorsum*kI`, then `maxIDesiredTermOutput=iZone*kI`. So `iZone=maxIDesiredTermOutput/kI`.



Lastly, what's the D in PID?

Well, it's less intuitive, but let's try. Have you seen the large spike in output when you change a setpoint? Give the output a plot, if you so desire. For now, let's just reason through a system using the previous example PI values, and a large setpoint change resulting in an error of 20. 

Your PI controller is now outputting a value of 2.0 ; That's double full power! Your system will go full speed immediately with a sharp jolt, have a ton of momentum at the halfway point, and probably overshoot the final target. So, what we want to do is constrain the speed; We want it _fast_ but not _too_ fast. So, we want to reduce it according to how fast we're going.
Since we're focusing on error as our main term, let's look at the rate the error changes. When the error is changing *fast* we want to _reduce_ the output. The difference is simply defined as `error-previousError`, so a similar strategy with gains gives us `output+=kP*(error-previousError)` . 
This indeed gives us what we want: When the rate of change is high, the contribution is negative and large; Acting to reduce the total output, slowing the corrective action.


However, this term has another secret power, which disturbance rejection. Let's assume we're at a steady position, and the system is settled, and `error=0`. Now, let's bonk the system downward, giving us a positive error. Suddenly `nonzero-0` is positive, and the system generates a upward force. For this interaction, all components of the PID are working in tandem to get things back in place.

## Limitations of PIDs
OK, that's enough nice things. Understanding PIDs requires knowing when they work well, and when they don't, and when they actually cause problems. 

- PIDs are reactive, not predictive. Note our key term is "error" ; PIDs only act when the system is _already_ not where you want it, and must be far enough away that the generated math can create corrective action.
- Large setpoint changes break the math. When you change a setpoint, the P output gets really big, really fast, resulting in an output spike. When the PID is acting to correct it, the errorSum for the I term is building up, and cannot decrease until it's on the other side of the setpoint. This almost _always_ results in overshoot, and is a pain to resolve.  
- Oscillation: PIDs inherently generate oscillations unless tuned perfectly. Sometimes big, sometimes small.
- D term instability: D terms are notoriously quirky. Large D terms and velocity spikes can result in bouncy, jostly motion towards setpoints, and can result in harsh, very rapid oscillations around the zero, particularly when systems have significant [[Mechanical Backlash]].
- PIDS vs Hard stops: Most systems have one or more [[Hard Stops]], which present a problem to the I term output. This requires some consideration on how your encoders are initialized, as well as your setpoints.
- Tuning is either simple....or very time consuming.

So, how do you make the best use of PIDs?
- Reduce the range of your setpoint changes. There's a few ways to go about it, but the easiest are [[clamping]] changes, [[Slew Rate Limiting]] and [[Motion Profiles]] . With such constraints, your error is always small, so you can tune more aggressively for that range. 
- Utilize [[FeedForwards]] to create the basic action; Feed-forwards create the "expected output" to your motions, reducing the resulting error significantly. This means your PID can be tuned to act sharply on disturbances and unplanned events, which is what they're designed for.

In other words, this is an _error correction_ mechanism, and if you avoid _adding_ error to begin with, you more effectively accomplish the motions you want. Throwing a PID at a system can get things moving in a controlled fashion, but care should be taken to recognize that it's not intended as the primary control handler for systems. 
## Tuning



#### The math



