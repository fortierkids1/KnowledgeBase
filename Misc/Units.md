---
aliases: 
tags: []
---


Proper documentation: 
https://docs.wpilib.org/en/stable/docs/software/basic-programming/java-units.html


The Units library offers a robust, consistent way to maintain real world measurements across your code base. This has several useful applications. 

## Cleaning up code with Units 

Here's some problems with passing things around as Doubles
```java
//Old and terrible
	double angleForTopGoal=17.4; //Degrees ; Unit documented in comment
	double angleForTopGoalDegrees=17.4; // unit documented in var name
	//elsewhere
	math.sin(MathUtils.deg2Rad(angleforTopGoal)); //Conversions are kinda unclean
	// This functionality also does not exist for inch->meters or velocities!
	
	//Declaring functions gets cluttered too
	// It's documented but not enforced... 
	void setPosition(double angleRad){....};
	//So it's easy to later call it with the wrong unit type
	exampleSubsystem.setPosition(angleForTopGoal); // Forgot to convert :bug: 
	
```

But, with the Units library...
```java
//New Hotness
	Angle topGoal=Degrees.of(17.4); //Unit declared directly as a type
	math.sin(topGoal); // Compiler error! math.sin takes a double.
	math.sin(topGoal.in(Radians)); // Easily fixed with conversion api

	//Our api is also very clearly defined
	void setPosition(Angle angleRad){....};
	//No bugs! any Angle type works, regardless of how it was created.
	exampleSubsystem.setPosition(angleForTopGoal);
```

We can also utilize them just for consistent unit conversions.

```java
Degrees.of(180).in(Radians);
Inches.of(124).in(Meters)
RPM.of(5700).in(Rotations.per(Second))
```


## Recommendations for use

#### Make physical constants Units types
This almost always streamlines the code, by ensuring that your various definitions can be easily converted within your class. 
```java
private const Distance lowestHeight=Inches.of(7);
private const Distance maxHeight=Inches.of(42);
```

We'll generally fetch these units using a tape measure, which will give us inches. However, physics simulations and many other interactions will give us Metric units, so these can appear in unexpected locations!

This is *doubly* true with Angular units. Measuring and reasoning through them with Pi tends to be difficult, so we use Degrees instead. But math operations universally use Radians. This means we end up doing a fair amount of conversions for these unit types.

#### Use Units as your interface type
Having exposed public interfaces use Units streamlines streamlines subsystem and command interactions. Like a good API, this helps improve separation of concerns about what units are being used where. 

```java
class ExampleSubsystem(){
	public Command setPosition(Distance setpoint){...}
	public Angle setAngle(Angle setpoint){...}
	public Distance getPosition(){...}
	public Angle getAngle(){...}
}
```


#### For Conversions

In some cases, we just have to use atypical units that don't match everything else in the file. When this comes up, the Units library helps ensure that the actual base number stays consistent with everything else.

```java
Inches.of(100).in(Meters);
Degrees.of(99).in(Rad)

Degrees.of(encoder.get()).in(Radians)
```

This likely will come up fairly rarely if we're following good API conventions and using Units across the board, but there's a few cases such as encoder readings that often fall into this. 


#### When Not to use
###### Math operations
Generally, if you have a lot of math to do, you'll want to drop to a `double` to make things easier to type.
There are some built in math functions for math involving other Unit types, which might make sense for certain tasks, however.  
```java
public Command setPosition(Distance setpoint){
	return run(()->{
		double inches=setpoint.in(Inches);
		/* maths go here */
		motor.setReference(inches);
	})
}
```

###### Unnecessary conversions
There's also a few cases where the data type is strictly controlled, and locked to a `double` or `integer` type for other APIs.

The most frequent occurrence of this is using APIs for hardware systems. If these systems are being used internally for lots of operatations, it might make sense to fully track them internally using a `double` or `integer` type, and then only convert them to Unit types at public interfaces.