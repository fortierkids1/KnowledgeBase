
A compound subsystem is one that has multiple, interlinked mechanisms. Many common systems as [[SuperStructure Intake|Intakes]] or [[Superstructure Shooter|Shooters]] fall into this, as do complex kinematic systems like extendable [[SuperStructure Arm|Arms]], or Arms riding [[SuperStructure Elevator|Elevators]] form a compound subsystem.

These systems have common quirks in how they interact with [[Commands|Commands]] and [[Subsystems|Subsystem]] control flow.


## Subsystem Requires and Commands

Generally, you do not want to bundle two or more actuators together into the same subsystem; Inevitably, you will need a Command that tries to `require()` one, but forcibly require the second. 


This makes some command sequences awkward, since you need to require and control an actuator you don't care about. Alternatively, streamlining the system to automatically control just one or the other means extending the subsystem with multiple state tracking, as you can't rely on `defaultCommands()` which won't be active.


Combining multiple actuators in one system also can lead you to cases that block code re-use. Having simple commands that control "half" the system cannot be run in parallel without obscure, poorly documented command modifiers or forcibly removing the command mutexes. 

Lastly, you need to name and manage commands to interact with both actuators, making the subsystem large and complicated.


## Wrapper Classes

A better structure is to simply break them into two subsystems, giving them the minimal controls needed to act independently. 

You can then use a separate container class to house utility functions, or combined Command classes that interact with both systems.

Note, the wrapper class will not be a subsystem itself: WPILib does not support "requires" through nested subsystems.

## Handling Default Commands

DefaultCommands by their nature must require themselves, and can *only* require themselves; This means it's not possible to have a "default" for two subsystems. 

In some cases, you may be able to simply leave each individual part with a default command. 

However, if you wish to have them default as a group, you can emulate the "default command" behavior through the use of a Trigger, and detection of the currently commanded state of any controlled systems. 

![[Triggers#Playing nice with other commands|Trigger]]


## Handling dependent kinematics

If your systems should be operating with dependent kinematics or logic for decision making, you can do this a couple ways:

```java
// Via Dependency injection of state functions
BottomSystem bottom = new BottomSystem();
TopSystem top = new TopSystem(bottomsystem::getPosition);
```

This allows a clear distinction of systems, while still permitting TopSystem to access details it may need to see on the bottom.

This interaction is pretty ineffective things such as two-way collision conflicts. For such cases, you likely want to use a [[State Machines|State Machine]] to control motion.

While not recommended, you *can* bind cross-dependencies between two systems. However, this cyclic connection should clue you to the need to [[Refactoring|Refactor]] some of your code interactions. 

```java
//Construct the subsystem 
public class BottomSystem extends SubsystemBase{
	// Leaving this blank will likely result in runtime errors, 
	// so initialize it with an appropriate Supplier function
	DoubleSupplier topPosition = ()->0 ;
	
	//Create a function to register it
	public void addTopPositionSupplier(DoubleSupplier supplier){
		topPosition=supplier;
	}
}
public class RobotContainer{
	// Create the appropriate systems
	BottomSystem bottom = new BottomSystem();
	TopSystem top = new TopSystem();
	RobotContainer(){
		bottom.addTopPositionSupplier(top.getPosition());
		top.addBottomPositionSupplier(bottom.getPosition());
	}
}
```


