---
aliases:
  - Flywheel
---

Superstructure component that holds a large amount of kinetic energy at a high velocity. Typically part of a subsystem oriented at launching game pieces.

## Success Criteria
- [ ] Create a Flywheel system
- [ ] Tune with appropriate FeedForwards + PID to hit and maintain target RPMs 
- [ ] 

## Difference to a Roller

In some senses, none: A "Flywheel" is technically a spinning mass that provides inertial power to linked systems. We often add flywheels to our shooter rollers to add mass and make shots more consistent. 

 In FRC, we often use the term "flywheel subsystem" to help disambiguate the "spinning mass" part of a shooter from the "angular adjustment" part of a shooter. Otherwise, without good nomencalture, *both* parts get called "shooter", which is relatively unhelpful and can make things a bit confusing.

However, there's also physical differences in control and intent. The extra mass requires significantly more energy, resulting in either slower spinup, or *much* more power draw that can brown out your robot. We're also adding this inertia to stabilize shots, implying a desire for accuracy, precision, and consistency. In other words, this is very deep in the zone of performance tuning.

## Power Considerations

When spinning up a high-inertia system, the power draw can be *incredible*, typically pulling the full allotted power for all motors involved. 

If we consider a brand new FRC battery, they have a rated capacity of 300A; A single FRC motor will pull 80-100A of that. If we have two motors, we'll pull 160A-200A. Keeping in mind we  have  *other* motors doing *other things*, especially 4 motors doing driving, you can easily sag the system voltage, causing loss of power and browning out one or more critical components. 

Because of this, it's often a strategic choice on how how to handle power. Some teams (1540) advise just removing all power constraints: The reasoning is that by giving unlimited power to your flywheels, you get them up to speed quickly, trusting that the voltage sag is short enough that the various power regulation systems can recover properly. 
Other teams implement dynamic voltage constraints; Polling the various robot systems, and allocating power according to current system draw, and subsystem needs. 

Both methods are worth considering, and both have been done on Stormbots in various seasons.


## Default Commands and automatic behavior

When operating with flywheels, the *ideal* behavior is simply to never have to change velocity. Theoretically, if the velocity doesn't change, you don't use power. 

In reality, this is impossible ; Air resistance, bearing friction, motor internal resistance, and precession all work to sap flywheel energy and speed.  And of course, every time we change speed, we're using power from our battery. 

It's also kind of unsafe to keep flywheels spinning at *extremely* high velocities all the time, and we often need to change velocity for different shots anyway.

As such, it's good practice to consider what to do when *not* shooting. This will change each season and each robot, and might change based on how a game involves. But, good considerations include:

- Can we idle to a slower, safer speed instead of stopping? 
- Coasting to slow down rather than using brake or active PIDs
- When a shot is abandoned, can we maintain shooting speed for a time to prevent a need for significant adjustment a short time later?
- Can we give drivers options to boost RPM in advance?

The goal is to maximize safety, minimize lag between driver + action, and minimize total speed adjustments. 

## System Identification + automatic tuning

For precision velocity control, the [[FeedForwards|FeedForward]] model, [[PID]] tunings, and [[Motion Profiles|Motion Profile]] are all very important. You mostly need to get and maintain the target RPM extremely quickly, while  minimizing overall settle time. 

In some cases, you *might* actually find that minor PID overshoot doesn't cause problems, and can result in quicker time-to-target and overall settling.

Sometimes, using SysID can help you; This is a program that runs some test procedures, using the resulting physical measurements to analyze system response, and thus generate PID and FF values. If set up correctly, these values can be fed back into your robot config, helping you quickly account for hardware changes, wheel wear, and other factors that might have impacted your system performance.

Instead of painful precision tuning via graphs, implementing SysID into your java code can let you run these similar to an Auto sequence, letting you respond quickly to emergency repairs during competition. 

> [!TIP] Title
> See Crescendo's robot code for examples on automated flywheel tuning procedures

Note, that the recommended approach is *still* to do good feed-forward modelling and motion profiles before PID tuning. With this setup, it's likely that you can use relatively large PI values for getting up to target speeds, and PD terms and flywheel inertia will resolve in good disturbance rejections.

## Communication + Lighting

Drivers *need* to know when they can utilize an on-target flywheel. When in performance mode, they will be cutting every corner for performance, and their trust in robot feedback is crucial. Unfortunately, RPM is largely invisible to drivers in competitions. 

In these cases, the robot's best feedback source is a [[Subsystem Lighting|LED Subsystem]] ; Simply have the bot  lighting give the all-clear to fire. In practice, this helps the drivers get a "feel" for spinup time, and in competitions gives them certainty that a shot will work as expected. 

Note, that the feedback itself can be challenging: Remember, the goal is "will our shot make it" more than "Is the shooter at the target RPM". Some notable considerations:
- Sometimes far-away shots at higher velocities only succeed with a lower RPM error than closer, nearby ones going at a slower speed. 
- Overshoot/undershoot might fall within an permissible, but cause missed shots due to how the acceleration affects the physics.
- Flywheel speed is only *one* aspect; You also have robot ground speed, rotational speed, and (possibly) the angle of your shooter configuration. Drivers must know what is and isn't accounted for.

At the end of the day, your system might simply green-light a good RPM range and be good enough. Or, you might need to create a complex state machine to track and count system stability and settle time, or chain boolean logic for other conditions. 

Whatever you do, your efforts put toward driver indicators will *always* be in vain if your drivers *don't trust it*. These systems require good communication with drivers to establish the trust boundaries for indicators, and to maintain that trust with accurate signals.
## Dynamic Setpoints vs Stepped Setpoints

When working with a fixed-angle shooter, your system will always have to account for shot distance by adjusting RPM continuously. If RPM adjustment won't work, you simply can't make make a shot. 

However, when given an adjustable angle shooter (see 2024 robot), programmers have 2 control axes to resolve shot distance. This allows a great amount of freedom in what a shot looks like. This lets us apply our design criteria, and consider our options:

- Fix the angle, and have dynamic RPM adjustment
- Fix the RPM, and adjust the angle
- Adjust both at once all the time. 

With the last one ruled out, you probably  want to consider which parameter to lock in place to optimize shots for a given area. In most cases, the best approach is to use an interpolating [[Lookup Tables|Lookup Table]] (LUT). This allows you to convert a given known value (usually distance) into a tested combination of angle and distance. 

Such a table can be very quickly generated using practice field elements, and provides very robust performance. With good distance spacing, you can quickly fix problem zones, and then nature of a LUT allows you to seamlessly switch between which output is fixed..
## Trajectory Calculation + Optimized control

This is an ongoing advanced topic, and in early stages: [[Flight Trajectory Calculations]]



#### See also:
[[System Identification]]
[[Lookup Tables]]