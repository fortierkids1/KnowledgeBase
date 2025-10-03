---
aliases:
  - Field2d
  - Field Odometry
---
#stub 

## Synopsis

https://docs.wpilib.org/en/stable/docs/software/dashboards/glass/field2d-widget.html
## Success Criteria
- [ ] Create a Widget on a dashboard to show the field
- [ ] Populate the Field with your robot, and one or more targets
- [ ] Utilize the Field to help develop a "useful feature" displayed on the Field

As a telemetry task, success is thus open ended, and should just be part of your development process; The actual feature can be anything, but a few examples we've seen before are
- showing which of several targets your code has deemed best, and react to it
- a path your robot is following, and where the bot is while following it
- The current Vision targets and where your bot is when seeing them
- A field position used as an adjustable target 
- The projected path for a selected auto
- Inidcate proximity or zones "zones" for performing a task, such as the acceptable range for a shooting task or intaking process.
## Odometry Fundamentals

Odometry is also known as Position Tracking. In FRC, this is generally regarded as knowing the position of your robot in the game field. It is most beneficial when combined with the robot to then have the robot move between positions on the field, and interact with other known positions. 

Actually obtaining odometry depends on the design of the bot: [[Level 1/Differential Drive Odometry|Differential Drive Odometry]] or [[Swerve Odometry]] , and often involves vision systems via [[Limelight Odometry|LimeLight Odometry]] or [[PhotonVision Odometry]]

This document is concerned with the prerequisite: Being able to actually view, compare, and validate positions using robot telemetry.
## Odometry Telemetry


![[telemetry-elastic.png]]

Telemetry for Odometry revolves around the Field2D object. Both Glass and Elastic can display the Field2D object as a game field on the dashboard, including associated robot positions and any secondary objects.

In general, Glass is the superior option for most programming tasks and development, allowing easier customization to help reduce clutter and provide clarity between objects. 

The WPILib Docs are excellent at helping demonstrate many of these examples:
https://docs.wpilib.org/en/stable/docs/software/dashboards/glass/field2d-widget.html

## Pose
In Robots, a Pose represents a specific location and state of a robot's actuator. Within the context of odometry, a pose represents the robot's location on a 2D plane. In most cases, this means the location on the game field. 

In order to represent such a state, we need 3 things: The X position, the Y position, and a rotation. 

In WPILib, this is handled by the [[Pose2d]] object, which can be provided to and returned from many utilities interacting with the robot position, including drivetrain path planning, vision, simulation, and telemetry.

### Field2D Basics

The `Field2D` object in WPILib contains a number of utilities for keeping tabs on odometry. It can be added with just a couple lines. 

```java 
class Drivetrain extends SubsystemBase{
	Field2D field = new Field();
	
	public Drivetrain(){//constructor
		//We only want to send the actual Field object _once_,
		//so we often do it in the constructor. Changes will be 
		//sent automatically as the Field2d object itself is updated.
				
		//Preferred: Set the NetworkTables name explicitly, 
		//and give it a unique, descriptive name
		SmartDashboard.putData("ChassisField",field);
		
		// This form uses the default name "Field" as the key.
		// Not recommended in most cases
		//SmartDashboard.putData(field);
	}

}
```


This creates a blank, empty field, with your robot position, probably at (0,0)  in the bottom left corner, on the blue side of the field. 

Meaningfully updating the robot pose is out of scope, and differs by drivetrain type; However, the basic gist is 

```java 
class Drivetrain extends SubsystemBase{
	Field2D field = new Field();
	//Set up an odometry object to track our robot
	DifferentialDrivePoseEstimator odometry=new DifferentialDrivePoseEstimator(
	 /* Complex constructor parameters; not relevant*/
	)
	
	public Drivetrain(){//constructor
		SmartDashboard.putData("ChassisField",field);
	}
	
	public void periodic(){
		//Read the odometry object and set the pose. //Reference only
		field.setRobotPose(odometry.getPoseMeters());
		
		// Or, we can just set it manually for testing.
		field.setRobotPose(new Pose2d(2.7,3.1, new Rotation2d(Math.PI/2.0) ));	
	}
}
```


Now, when we open our Field2D widget in Glass, we'll see that our robot is at a new position. If we fully implemented our Pose Estimator object, we'd see that this provides realtime tracking of our position.

A `.getRobotPose()` also exists, but tends to be less useful in practice, as most classes that will interact with the Robot and Field will likely have access to the Odometry object directly. 

Note, that the Field2d object we create is a unique reference, with it's own unique local data; only the NetworkTables key might have overlap when using default keys. This means if we want to properly share a Field across multiple classes, we either need to fetch the NetworkTables data and copy it over, create a single Field object in RobotContainer and pass it to, or create a DriverField object as a [[Singletons|Singleton]] that can facilitate a single object.
### Displaying Useful Targets

Where Field2d objects really shine is in adding supplemental information about other field objects. The utility varies by game, but it's great for showing a variety of things such as 
- Targets/objectives
- Nearest/best target
- Nearest game piece (detected by vision systems)


These can be done using the `getObject(name)` method; This grabs the named object from the field, creating it if it doesn't already exist.

```java 
class Drivetrain extends SubsystemBase{
	Field2D field = new Field();
	
	public Drivetrain(){//constructor
		SmartDashboard.putData("ChassisField",field);
	}
	
	public void periodic(){
		//Can provide any number of pose arguments
		field.getObject("GamePieces").setPoses(
			new Pose2d(1,1),
			new Pose2d(1,2),
			new Pose2d(1,3),
		);
		//You can also pass a single List<Pose2d> 
		//if you have a list of poses already
		
		field.getObject("BestGamePiece").setPose(
			new Pose2d(1,2)	
		);
	}

}
```

`[!!alert-triangle|Note|var(--color-yellow-rgb)]` It's worth considering that for objects that never move, you *could* set  objects once in the constructor, and they work fine. However, if the user accidentally moves them in the UI, it creates a visual mis-match between what the code is doing and what the user sees. As a result, it's often better to just throw it in a Periodic.

### Field2d + User Input

A niche, but very useful application of field objects is to get precise driver feedback on field location data.  This can be done using the following code setup:

```java 
class Drivetrain extends SubsystemBase{
	Field2D field = new Field();
	
	public Drivetrain(){//constructor
		SmartDashboard.putData("ChassisField",field);
		//Set the pose  exactly once at boot
		field.getObject("DriverTarget").setPose(
			new Pose2d(1,2)	
		);
	}
	
	public Command aimAtCustomTarget(){
		return run(()->{
			var target=field.getObject("DriverTarget").getPose();
			//Do math to figure out the angle to target
			//Set your drivetrain to face the target
		};
	}
	
	public void periodic(){
		//No setting DriverTarget pose here!
	};
}
```

In this case, we take advantage of the "set once" approach in constructors; The drivers or programmers can modify the position, and then we now read it back into the code. 

This can be very useful for testing to create "moving targets" to test dynamic behavior without having to drive the bot. It can also help you create "simulation" poses for testing math relating to Pose2D objects.

 is especially true for simulation, as this allows you quickly test pose-related functions and makes sure that things happen how you expect.

One practical application is match-specific targets for cooperating with allies. An example is in 2024 Crescendo: a common game tactic was to "pass" rings across the field, by shooting to an open area near where your allies would be. However, since the game pieces can get stuck in/on robots, and different robots have different intakes, and each ally has different sight lines, making the ideal pass target unknown until the actual match is about to start. An adjustable target let the alliance sort it out before a match without having to change the code.



### Displaying Paths

When doing path-planning for drivetrains, it's often helpful to display the full intended path, rather than a lot of individual poses. 

```java 
class Drivetrain extends SubsystemBase{
	Field2D field = new Field();
	
	public Drivetrain(){//constructor
		SmartDashboard.putData("ChassisField",field);
		//Set the pose  exactly once at boot
		field.getObject("DriverTarget").setPose(
			new Pose2d(1,2)	
		);
	}
	
	public Command buildAuto1(){
		var  trajectory=// Get the trajectory object from your path tool
		field.getObject("autoTrajectory").setTrajectory(trajectory);
		return run(()->{
			//draw the rest of the owl
		};
	}
	

}
```

In this use case, the work will likely be done inside your Auto class after selecting the appropriate one. 

