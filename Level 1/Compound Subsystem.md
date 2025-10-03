
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


## Handling "twinned" subsystems


> [!TIP] New Process!
> This subsystem type tends to be rare, and as such we do not implement this often to fully validate best practices. Feedback on the documentation and trial methods for optimal layout is requested!

Sometimes, your robot will have a subsystem that is two "identical" parts that are not kinetically linked. In these cases, the subsystem split can be quirky, and they tend to provide different tradeoffs

- Handling each half as it's own subsystem with independent code generates a lot of copy-pasted code, repeated bugfixes, and increased maintenance overhead. 
- Bundling both in the same subsystem can result in duplicate code to drive the separate motors,  especially if they each run independent, non-trivial logic like [[Motion Profiles|Motion Profiles]] 
- Providing a "base class" and then creating two instances: This simplifies control logic and reduces code duplication, but pushes all a fair amount of configuration and combined logic to higher-level code structures (such as RobotContainer . 
  
However, in general the final option is often worth it to prevent errors and streamline debugging. With proper naming, abstraction, and parameterize, these systems can be handled without significant overhead. 

#### Construct the base actuator class

While there's a few ways we could structure this *in general* we want to keep subsystem logic 
contained in it's own file for easy tracking, viewing, and modification. Since we expect twinned systems to have few difference, this structure provides a simple way to easily manage the adjustments.

```java 
//This forms our the base class both ExampleTwin systems will use 
class ExampleTwinBase extends SubsystemBase{
	//Private to prevent unexpected creation anywhere
	private ExampleTwinBase(boolean invert, double switchChannel){
		//Configure the motor using the provided parameters
		// making sure to use the parameters as appropriate.
		// In this example, 
		// using "invert" for motor direction, 
		// and switchChannel for a rio DIO port for something
		// but provide the necessary ones in whatever form makes sense.
	}
	
	public static BuildExampleLeft(){
		var inverted=false; //
		var channel=0; //example 
		return new ExampleTwinBase(inverted,channel);
	}
	
	public static BuildExampleRight(){
		var inverted=true; //
		var channel=1; //example 
		return new ExampleTwinBase(inverted,channel);
	}
	
	//Do the rest of your normal, non-static instance classes here.
}
```


```java
public class RobotContainer{
	//We can now build the two seperately, using the static functions
	// to handle the difference in configuration
	ExampleTwinBase exampleLeft=ExampleTwinBase.BuildExampleLeft();
	ExampleTwinBase exampleRight=ExampleTwinBase.BuildExampleRight();
}
```

Rev's Config api has a useful feature that allows you to combine configs; This facilitates splitting logic, and even allows you to provide a Spark config as a parameter directly.

```java
public ExampleRevParam(SparkBaseConfig customConfig){
	SparkBaseConfig config = new SparkMaxConfig();
	config./*whatever your configs that apply in both cases*/

	config.apply(customConfig); // Copy the special configs to the base config

	//Apply your config normally, getting everything at once
	motor.configure(
		config,
		ResetMode.kResetSafeParameters, 
		PersistMode.kNoPersistParameters
	);
};
```

We now have two subsystems.... which is not exactly optimal for clean Command setup. However, a simple wrapper class will help us out!

```java
public Example{ //NOTE: Not a subsystem! Just a generic class
	ExampleTwinBase left;
	ExampleTwinBase right;
	public ExampleSubsystem(ExampleTwinBase left, ExampleTwinBase right){
		this.left=left;
		this.right=right;
	}
	//Now, we can use *this* to house all our combo commands nice and cleanly!
	// Parallel runs both at once, and each end independently if configured.
	// When both are done, the parallel ends. 
	public Command doTheThing(){
		returns Commands.parallel(
			left.doTheThing(),
			right.doTheThing()
		);
	}
	
}
```


```java
public class RobotContainer{
	//We can now build the two seperately, using the static functions
	// to handle the difference in configuration
	ExampleTwinBase exampleLeft=ExampleTwinBase.BuildExampleLeft();
	ExampleTwinBase exampleRight=ExampleTwinBase.BuildExampleRight();
	Example example = Example(exampleLeft,exampleRight);
}
```


`[!!flame|Cleanup Note|var(--color-cyan-rgb)]` We *could* also just move the whole Builder calls directly in the wrapper, or directly into the Example file itself! Depending on the goals, this may or may not be ideal. Sometimes having access to each side is nice, so consider leaving yourself an easy option should the need arise.

```java
//Example of the streamlined, cleaned up code. The individual sides are now not accessable without going through our wrapper.
public class RobotContainer{
	Example example = Example(
		ExampleTwinBase.BuildExampleLeft(),
		ExampleTwinBase.BuildExampleRight()
	);
}
```