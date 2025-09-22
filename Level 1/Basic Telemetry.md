---
tags:
aliases:
  - NetworkTables
---
## Goals
Understand how to efficiently communicate to and from a robot for diagnostics and control 
## Success Criteria
- [ ] Print a notable event using the RioLog
- [ ] Find your logged event using DriverStation
- [ ] Plot some sensor data (such as an encoder reading), and view it on Glass/Elastic
- [ ] Create a subfolder containing several subsystem data points.


As a telemetry task, success is thus open ended, and should just be part of your development process; The actual feature can be anything, but a few examples we've seen before are


## Why you care about good telemetry
By definition, a program runs exactly as you the code was written to run. Most notably, this does not strictly mean the code runs as it was *intended* to. 

When looking at a robot, there's a bunch of factors that can have be set in ways that were not anticipated, resulting in unexpected behavior.

Telemetry helps you see the bot as the bot sees itself, making it much easier to bridge the gap between what it's doing and what it should  be doing. 


## Printing + Logging

Simply printing information to a terminal is often the easiest form of telemetry to write, but rarely the easiest one to use. Because all print operations go through the same output interface, the more information you print, the harder it is to manage.

This approach is best used for low-frequency information, especially if you care about quickly accessing the record over time. It's best used for marking notable changes in the system: Completion of tasks, critical events, or errors that pop up. Because of this, it's highly associated with "logging".

The methods to print  are attached to the particular print channels
```java
//System.out is the normal output channel
System.out.println("string here"); //Print a string
System.out.println(764.3); //you can print numbers, variables, and many other objects

//There's also other methods to handle complex formatting.... 
//But we aren't too interested in these in general.
System.out.printf("Value of thing: %n \n", 12);
```

A typical way this would be used would be something like this:
```java
public ExampleSubsystem{
	boolean isAGamePieceLoaded=false;
	boolean wasAGamePieceLoadedLastCycle=false;
	
	public Command load(){
		//Some operation to load a game piece and run set the loaded state
		return runOnce(()->isAGamePieceLoaded=true);
	}
	
	public void periodic(){
		if(isAGamePieceLoaded==true && wasAGamePieceLoadedLastCycle==false){
			System.out.print("Game piece now loaded!");
		}
		if(isAGamePieceLoaded==false && wasAGamePieceLoadedLastCycle==true){
			System.out.print("Game piece no longer loaded");
		}
		wasAGamePieceLoadedLastCycle=isAGamePieceLoaded
	}
}
```

Rather than spamming "GAME PIECE LOADED" 50 times a second for however long a game piece is in the bot, this pattern cleanly captures the changes when a piece is loaded or unloaded.

In a more typical [[Commands|Command based robot]] , you could put print statements like this in the `end()` operation of your command, making it even easier and cleaner. 

The typical interface for reading print statements is the RioLog: You can access this via the Command Pallet (CTRL+Shift+P) by just typing `> WPILIB: Start Riolog`. You may need to connect to the robot first. 

These print statements also show up in the DriverStation logs viewer, making it easier to pair your printed events with other driver-station and match events. 

## NetworkTables

Data in our other telemetry applications uses the NetworkTables interface, with the typical easy access mode being the SmartDashboard api. This uses a "key" or name for the data, along with the value. There's a couple function names for different data types you can interact with
```java
// Put information into the table
SmartDashboard.putNumber("key",0); // Any numerical types like int or float
SmartDashboard.putString("key","value"); 
SmartDashboard.putBoolean("key",false);
SmartDashboard.putData("key",field2d); //Many built-in WPILIB classes have special support for publishing
```

You can also "get" values from the dashboard, which is useful for on-robot networking with devices like [[Limelight Basics|Limelight]], [[PhotonVision Basics|PhotonVision]], or for certain remote interactions and non-volatile storage.
Note, that since it's possible you could request a key that doesn't exist, all these functions require a "default" value; If the value you're looking for is missing, it'll just  give you the provided default.
```java 
SmartDashboard.getNumber("key",0);
SmartDashboard.getString("key","not found");
SmartDashboard.getBoolean("key",false);
```

Networktables also supports hierarchies using the "/" seperator: This allows  you to separate things nicely, and the telemetry tools will let you interface with groups of values.

```java 
SmartDashboard.putNumber("SystemA/angle",0);
SmartDashboard.putNumber("SystemA/height",0);
SmartDashboard.putNumber("SystemA/distance",0);
SmartDashboard.putNumber("SystemB/angle",0);
```

While not critical, it is *also* helpful to recognize that within their appropriate heirarchy, keys are displayed in alphabetical order! Naming things can thus be helpful to organizing and grouping data. 
### Good Organization -> Faster debugging

As you can imagine, with multiple people each trying to get robot diagnostics, this can get *very* cluttered. There's a few good ways to make good use of Glass for rapid diagnostics:

- Group your keys using `group/key` . All items with the same `group/` value get put into the same subfolder, and easier to track. Often subsystem names make a great group pairing, but if you're tracking something specific, making a new group can help. 
- Label keys with units: a  key called `angle` is best when written as `angle degree` ; This ensures you and others don't confuse it with `angle rad`.  
- Once you have your grouping and units, add *more* values! Especially when you have multiple values that *should* be the same. One of the most frequent ways for a system to go wrong is when two values differ, but shouldn't.

A good case study is an arm: You would have 
- An absolute encoder angle
- the relative encoder angle
- The target angle
- motor output
And you would likely have a lot of other systems going on. So, for the arm you would want to organize things something like this

```java
SmartDashboard.putNumber("arm/enc Abs(deg)",absEncoder.getAngle());
SmartDashboard.putNumber("arm/enc Rel(deg)",encoder.getAngle());
SmartDashboard.putNumber("arm/target(deg)",targetAngle);
SmartDashboard.putNumber("arm/output(%)",motor.getAppliedOutput());
```

A good sanity check is  to think "if someone else were to read this, could they figure it out without digging in the code". If the answer is no, add a bit more info.

## Glass

Glass is our preferred telemetry interface as programmers: It offers great flexibility, easy tracking of many potential outputs, and is relatively easy to use. 

![[telemetry-glass.png]]
Glass does not natively "log" data that it handles though; This makes it great for realtime diagnostics, but is not a great logging solution for tracking data mid-match.

This is a great intro to how to get started with Glass:
https://docs.wpilib.org/en/stable/docs/software/dashboards/glass/index.html

For the most part, you'll be interacting with the NetworkTables block, and adding visual widgets using Plot and the NetworkTables menu item.
## Elastic

Elastic is a telemetry interface oriented more for drivers, but can be useful for programming and other diagnostics. Elastic excels at providing a flexible UI with good at-a-glance visuals for various numbers and directions.

![[telemetry-elastic.png]]

Detailed docs are available here: 
https://frc-elastic.gitbook.io/docs

As a driver tool, it's good practice to set up your drivers with a screen according to their preferences, and then make sure to keep it uncluttered. You can go to Edit -> :luc_lock:Lock Layout to prevent unexpected changes. 

For programming utility, open a new tab, and add widgets and items.


## Plotting Data

#todo 