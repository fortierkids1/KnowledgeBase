---
aliases:
  - Climber
---


## Success Criteria
- [ ] Configure the climber system's encoder
- [ ] Configure soft limits to restrict motion
- [ ] Write a homing routine
- [ ] Write triggers to indicate climber's target positions/states
- [ ] Write a viable control mode for the climber
## Synopsis

A climber represents a subsystem which is intended to raise the robot off of the ground. This is a common game task, although the requirements for "climbing" vary from year to year. 

These can range from 
- obtain and climb a rope
- grab a horizontal bar and do a hold yourself up
- somehow get 2 feet off the ground onto a platform 

In general, climbing tasks also require that you *remain* in the climbed position after the game ends, providing interesting mechanics and control considerations.
## Difference from normal systems

*Torque*. Climbers typically represent a *significant* amount of power, and if left running uncontrolled, will destroy your robot and contacting objects in slow motion while you scramble to disable it. 

As a result, consistent [[Homing Sequences|Homing]] routines and strong enforcement of the homed state is critical to a successful climber. These systems are often difficult or impossible to move by hand, and will often not be in the correct "starting position" when the robot is enabled.

In some cases, the significant torque makes tuning and calibration trivial; You just give it a P value and it eventually gets to the new place, regardless of if it's lifting a bot or not. 
In other cases, the significant change in load on the motor means you need to change [[PID]] and [[FeedForwards]] gains based on whether you're actually climbing, or just moving the system while not climbing.

In many cases, the act of climbing will add additional physical constraints on the robot. Commonly, if you grab a bar, the bar and/or wall now form significant obstacles that restrict the actuator range. You might have other actuators that need to be in custom positions.

## Arm Climbers
While somewhat uncommon, sometimes an arm is used to raise the robot. No special notes beyond the standard differences.

## Roller Climbers

Systems that grab a dangling field element (a chain or rope) work similar to [[SuperStructure Rollers|Rollers]] have no initial homing process, but *do* develop physical constraints once a rope is secured.

For these systems, you would want to make sure that you can detect the attached field element, and then enable safe upper constraints limits to prevent your bot from running out of chain, or otherwise driving through systems. 

Note, that this *can* be very difficult! Winding ropes and chains may collect unevenly, and you should ensure you have appropriate considerations for various ways it can wind. If your system cannot reliably detect end-of-motion conditions, such a system may need an additional switch or contact plate to provide additional data.

## Winch Climbers

Winch systems are [[SuperStructure Rollers|Rollers]] permanently attached to a rope; Generally, these are a split subsystem with a separate, low-power mechanism that secures the rope to the field, followed by the winch doing the high-torque action. 

Unlike other roller systems, these are more closely tied to [[#Linear Climbers]] and [[SuperStructure Elevator|Elevators]], as they generally require a homing process based on the unspooled length of rope, often equated to the height of the hook/grabber. 

In many cases, the bot designers will simply make the "attachment" mechanisms spring loaded or passive, and automatically driven through unspooling the winch.

In other cases, Winches involve active placement mechanisms. In this setup, the two actuators need to be moved together accurately to avoid causing physical conflicts and damage. Using good [[Forward Kinematics]] and [[Motion Profiles]] is essential to ensuring both systems move at the same rates, which will necessarily be the speed of the slower system. 
During the climb action, setting the placement mechanism to "coast mode" and disabling output is often advisable; This prevents surprises when forces in the robot cause unexpected motion that would cause the actuated placement system to fight eachother. 

When working with Winch systems, having excess unspooled rope is extremely hazardous; This can bind wires, tangle other subsystems, or get re-spooled wrong, and with the significant power on winch shafts, it's easy to cause damage. It's important to make a re-spooling or re-homing option readily available for effective resets and preperation.


## Linear Climbers

These systems work similarly to [[SuperStructure Elevator|Elevators]]. 

Generally, you will operate these with extremely low current limits during homing and "preparation" motions, and shifting to "full power" modes once the driver properly initializes the climb, or if you can detect an engaged field element. 

## Compound/split climbers

In some cases, climbers might several different actuating parts working together due to design goals. This is likely either a generic [[Compound Subsystem]] with different actuator types, or a [[Compound Subsystem#Handling "twinned" subsystems|Twinned Subsystem]] with two mostly identical actuators operated separately but simultaneously due to design constraints.


## Halting Motion after Match end

For many climbing tasks, the robot must "be in the air" for several seconds after the match ends and the motor controllers are disabled. 

The typical way to facilitate this is via the use of Brake mode on the climbing system; With high torque systems, this will create significant resistance, and the robot will generally stay up long enough to secure points. 

In other cases, it may be necessary to actuate a mechanism to "lock" the bot in place. 

In all cases, care should be taken to ensure that the robot is safely reset after climbs, prior to the next attempted motion. 

## Switching Brake/Coast Modes

Due to the need to hang, we can assert that we should *always* end matches in Brake mode. 

When the robot is powered off, the motor is (effectively) in coast mode.

However, when the drivers need to manipulate the bot (such as in practice or queuing for a match), we generally *also* want it in coast mode.

As a result, the typical climber states for Brake/Coast is as follows:
- Initialize on boot to coast mode (allowing human adjustment) before matches start.
- When initiating the climb, or a climb attempt is detected, apply Brake mode.

The other considerations vary on climb: 
- If the robot can safely de-climb in coast mode, apply Coast when the robot is enabled. This facilitates resets during practices, since the bot can be enabled+disabled to allow human assistance
- If the robot cannot safely de-climb via coast, but can via actuation, provide this to your drivers for quick field resets.

