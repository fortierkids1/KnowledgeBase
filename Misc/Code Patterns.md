---
aliases:
  - Factory Pattern
  - Dependency Injection
---
A "Pattern" describes a code technique to manage code complexity. Patterns are as much a communication tool as a coding one. Having a few patterns in your head might help you reason through complex problems. Similarly, if your code does follow a well known pattern, it's easy to communicate to others. 

There exist dozens of universally helpful patterns, and even more patterns exist for specific programming domains (web, robotics, data analysis, etc). This is just a few we'll reference through our documentation.

## Factory Pattern
The Factory pattern is one that bundles new item creation into a helper function call. It's called this because every time you call a factory, it spits out a new item for you. Just like factory-made objects in the real world, you don't have to know _how_ it's made to get one and use it.

Factories provide a few useful properties
- It provides a place to deal with a log of syntax once
- You can easily parameterize creation with function arguments
- The factory's scope can be different than the caller, letting the factory access functions you wouldn't be able to otherwise.

In FRC, we'll commonly use Factories to generate [[Commands]] and Command Sequences within [[Subsystems]], and we'll often have a few stray functions for assisting in Autos or button handling.

This example showcases how this is often used. 
```java
ExampleSubsystem extends SubsystemBase{
	//This factory builds a command that sets a height
	//This hides a lot of logic in one place
	public Command setHeight(double height){
		return new RunCommand(
			/* motor logic goes here, not relevant for example */
			,this
		);
	}
	
	//This factory builds a command that builds 4 setHeight
	//This one is more about parameterizing and reducing extra
	//syntax and typing for this process
	public Command UpDownTwice(double height,double timeout){
		return new SequentialCommandGroup(
			//We use the previous factory to build 
			setHeight(height).withTimeout(timeout),
			setHeight(0).withTimeout(timeout),
			setHeight(height).withTimeout(timeout),
			setHeight(0).withTimeout(timeout),
		);
	}
}

```

Many common functions in FRC already use factories like this behind the scenes! As a result, you might not see `new ....` too often in some of our documentation.

## Dependency Injection

This is a code pattern commonly used to pass sensor or subsystem information into subsystems. This allows one class to access other objects in a parent scope it couldn't normally access. 

In this example, both Chassis and Vision need to access our Odometry, so it can't be a child of either one. Instead, we make it a sibling, and pass a reference to create a dependency injection.

```java
RobotContainer(){
	SwervePosteEstimator odometry = new SwervePosteEstimator(/*...*/);
	Chassis chassis = new Chassis(odometry);
	Vision Vision = new Vision(odometry);
}
```

Dependency injection has a paired bit of code within each function. By java rules, the provided reference only lives through the constructor call. So, we need to save the reference within our class scope.

```java
public class Vision extends SubsystemBase(){
	//This is a place to hold the reference within the Vision system scope.
	//Note, this has no initialization or = sign.
	SwervePosteEstimator odometry;
	
	Vision(SwervePosteEstimator odometry){
		//  :luc_arrow_down_left:Directly specify the class scope 
		this.odometry=odometry;
		//            :luc_arrow_up_right: unspecified, uses closest scope;
		//               in this case, that's the function scope
	}
	
	public void update(){
		// Within other class functions, the only valid scope 
		// is the class instance; We no longer need to specify with this.
		odometry./*do stuff with the object*/
	}
}
```

In FRC code, we will use this a lot for subsystems and other shared, modifiable data. Dependency injection makes it easier and clearer which parts of your code will be working with a specific item, improving ease of debugging. 


## Singletons

Singletons are a way to ensure one, and only one instance of a class exists. This pattern is typically used to protect unique resources that require shared access.

In technical terms, in Java, a "static" reference is one that is known at compile time, and exists as a property of the "abstract class" rather than an "instance". Singletons use this to store a reference to a single instance of the object, and then share that reference.

```java
public class SingletonExample{
	//We create a static reference to a new 
	public static SingletonExample self;

	//Note the constructor is private! You cannot use `new ...`
	private SingletonExample(){/*initialization logic*/}

	public static SingletonExample getInstance(){
		if(self==null){self=new SingletonExample()};
		return self;
	}

	// ... Normal methods go here
}
```

Since the singleton runs the constructor, it allows code to run to initialize the singleton object, making  this pattern very useful. 

There's lots of use cases in FRC, with a common one being `DriverStation.getInstance()`, which provides access to the controlling laptop's Driver Station software.

Singletons also streamline scope access and initialization order: You simply run the static `getInstance` method, and it always works.  This use case makes it useful in FRC for sensor systems, avoiding the modification of class signatures required for Dependency Injection. 

In many applications Dependency Injection and Singletons can resolve similar scope and access issues; However, for mutable systems that maintain internal state, dependency injection is usually preferred due to the explicit, more easily traceable nature.
## State Machines

State machines are coding patterns that help manage sequencing, transitions, and constraints of a system.

This is a complicated topic with a lot of useful applications, so it actually has it's own page here: [[State Machines]]

While state machines do have code, in general state machines are communicated using the state diagram. When the state logic is broken down well, the actual code often becomes trivial.

![[State Machines#Example Jumping Character]]

