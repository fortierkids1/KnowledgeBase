---
tags:
---


## Code Resources

These have lots of documentation, which is helpful to go through before doing non-trivial work with Poses.

- [WPILIB Rotation + Translation](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/geometry/pose.html)
- [Pose2D class reference](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/geometry/Pose2d.html)
- [Translation2D class reference](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/geometry/Translation2d.html)
- [Transformations](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/geometry/transformations.html)

## The basics

#### What's a pose?

A "Pose" in robotics represents the state of a controlled actuator. Most commonly  in FRC, we use this to reference the robot's position on the field for [[Basic Odometry+Telemetry|Field Odometry]] , as the robot itself is the most critical actuator in most cases. 

However, be mindful that "pose" can still reference other actuators (like arms, elevators, etc), and many of these tools and techniques can be useful for actuators operating within a single plane (which tends to be a lot of them).

#### Pose2d vs Translation2d

A Pose2d and a Translation2d differ in that a Pose has a rotation component. A Translation does not. 

While these are conceptually very similar, the methods contained by these classes differ greatly. Pose2d is missing several classes you might expect to be present, when working with XY data, such as the distance between two Poses. 

When working with Poses, watch for cases where `.getTranslation2d()`  method to drop the rotation component and open up methods that facilitate your math.
#### Transformations

A Transformation (using Transform2d and Twist2d) represent a *change* to a Pose. These are most commonly used internally by WPILib odometry classes. 


## Helpful ways to use these classes

#### Calculate distance between two poses

```java
pose1.getTranslation2d().getDistance(pose2.getTranslation2d)
```

#### Find the nearest target

```java
ArrayList<Pose2d> targets = Arraylist.of(new Pose2d() /*targets here*/)
bot.getTranslation2d().nearest(targets)
```

#### Point interpolation + Pathing
Pose2D and Translation2d can both be useful for generating simple linear paths or intermediate points using the `interpolate` method 
```java
startpose.interpolate(endpose,t);
```
This method returns a pose between startpose and endpose, with `t` being effectively a percentage completion: At 0, it returns startpose, at 1, it returns endpose, and returns a pose along a linear path otherwise. 

This can be useful for inverse kinematics, allowing you to generate straight line motion, even when the system motion is non-linear (such as a combination Arm+Elevator or Arm+Extension system).

For Drivetrains, it can be helpful for generating the final segment of paths to scoring objectives. Since these are often against a wall, re-setting from a bad lineup often means drivers backing up, since automated routines might be unable to deal with wall collisions. Using an initial pose slightly away from the wall, with a final pose at the target allows automated resets to apply a backoff automatically, resolving these issues.

