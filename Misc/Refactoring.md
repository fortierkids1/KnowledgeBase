Refactoring is the process of re-structuring your code without notably altering the actual behavior of your code. 

Refactoring is a very common programming practice, serving purposes from performance, readability, maintainability, de-duplication, and as preparation for further work. 

## Abstractions + Managing Complexity

Refactoring tends to focus on how you manage complexity across your program. The way you represent your problem space in code is commonly called an "Abstraction". 

A good abstraction enables you to do complicated things easily and focus on solving problems, without having to think too hard on how they're being solved.

Without a good abstraction, the programmer will often trip over this complexity time and time again, distracting from the bigger picture problems.

A bad abstraction does more harm than good. This can range from being misleading, adding more complexity than it reduces, or just restricting you without good cause.   

Refactoring is, in a nutshell, finding a better abstraction. 

## Code smells: How to identify code to clean up

You'll often times hear the term "code smell" when it comes to identifying this type of issue. Just like a weird smell in your house, you can probably ignore it for a bit, but the longer you do, the harder it is to avoid. And, like a smell, the first sign is usually not the actual problem, but a symptom. You have to track it to the source, and once dealt with, the impact is bigger than it might first seem.

Creating this type of problem isn't a programmer failing though. Whenever a codebase grows in complexity, there's going to be little rough edges, quirks, and special cases left over. Left unchecked, you can easily build on these until they grow into major pitfalls that can completely stop progress.

Part of being a good programmer is learning how to identify such issues, form a corrective plan, and tidy up while it's still manageable. 
#### Duplication
Duplication tends to be the re-use of small bits of code just here and there. Eventually you might notice that this same bit of code is all over the place, sometimes slightly adjusted or modified. Importantly, you're probably suddenly seeing it because there's a bug in one copy.... that's probably in many of them. 

#### Readability

Readability tends to refer to the simple things in your code: Having good, clear naming conventions, having good indentation, and consistent bracket use.

It can also dip a bit into consistent writing patterns. Do your boolean functions swap between `isSystemLoaded` and `isSystemClear` ? Do different subsystems have different units?

These things don't strictly impact the way the code actually works, but can significantly impact the productivity of programmers working on it! In a multi-user setting, it's easy to tell if readability is off, because you'll probably have just as hard of a time reading their code as they do yours. 

#### Maintainability

Somewhat tied to the above, maintainability issues dip into the underlying structure a bit more. This normally shows up that the code just breaks more often than you think it should. Little changes cause random other problems, especially on parts of the code unrelated to what you're working on. 

This can also show up in noticing that the way your code is structured requires a lot of repetitive syntax to deal with, or functions that are not particularly useful in the current state.

#### Coding Pushback

Very much a vibe check; Is your code just feeling _difficult_ to add new code into? Is getting it to do what you want requiring a lot of mental overhead that's not related to solving bigger problems? 

## Doing the refactor

With a need for a refactor identified, it's time to actually do it. There's no hard-and-fast routine, but here's a good starting point 

#### Preperation

###### Version control
[[Git Basics|git]] is your friend! Git makes it easier to see big, sweeping changes, and see what bits you changed. It also, critically, ensures that if you get it wrong, you can easily start over and try again. Once you have a known good restore point, be fearless! Rip it apart!
###### Get it working
Get your code to a working state. Sometimes it means powering through what whatever code you were trying to add. Sometimes it's pulling your code out and putting it off for a bit. 
Basically, If your code doesn't work when you start the refactor, it's harder to judge if the refactor did what it was supposed to: Be cleaner, nicer, and work as it did before. 

###### Examine where the problems are
This is the hard part. You've gotten a whiff of the problem, but probably don't yet know how deep it goes, and you might not have a full picture of the solution solution quite yet. That's OK. 

One way to do it is find one of the spots that seem off, and just _fix it_. This will probably break something else, but that's alright. Fix that up too. Tidy it up. Sometimes doing this simple process leads you a good solution.

In the case that some simple poking didn't fix things, you may need to zoom out, and look at the big picture a bit more. Not just lines of code, but where whole functions and processes are kept. Sometimes you back yourself into a corner. Find the biggest problem you're dealing with, and just fix it in the quickest way possible, move things around, and see if that sparks any ideas for how to organize it. Be fearless! you always have [[Git Basics|Git]] at your disposal to get back to your starting point. 

###### Implement and standardize

With luck, poking around at the problems has led you to a better way to model your problems in code, and gotten you a few game plans. 

Now, you just have to do the legwork of tidying it up, making your cleanup routine the "new standard" for the project, and seeing if it brings up any new problems. 

If you see any new problems, consider trying to plan ahead. Often, just a few minutes of planning can help avoid a refactor down the line, or at least make it easier. 


## Example Refactor: Subsystem API


A common error we've run into before is setting up [[Subsystems|Subsystem]] -> Robotcontainer interfaces in the wrong convention. These looked like so.

```java
//A custom subsystem class
ExampleRollers extends subsystem base

	public void runMotor(double power){/*code to run motor*/}
	public boolean thingLoaded(){/*checks a sensor*/}
}
```

```java 
//in robot container
joystick.a().whileTrue(Commands.run(exampleRollers.runMotor(1),exampleRollers));
joystick.b().whileTrue(Commands.run(exampleRollers.runMotor(-1),exampleRollers));
```

With just a few lines, we're able to start seeing some weird structural problems. 

- Duplication: We're typing out that `Commands.run(...,...)` bit a whole lot. We also don't seem to be using runMotor in any other way either.
- Maintainability: You have to remember the requires every time you run one of these commands! That's a booby trap waiting to happen. 
- Readability: It's unclear what directions 1 and -1 mean. It's probably guessable, but no the code doesn't make it clear, or provide guidelines.  
- Code Pushback: Let's say you want to have runMotor stop automatically when running forward, and do a quick jolt when running backwards to fix a jamming issue. You might rightly assume this should be in the subsystem.... but the structure doesn't support it. You have to put it in RobotContainer, creating a bunch of new commands. And if there's another use of the eject or intake added later, you're duplicating all this!

Let's walk through and check some specific problems and potential solutions:
- We want to remove the unnecessary syntax headaches when trying to run the subsystem
- We want to clear up the Requires mess, and make that automatic as part of our function call
- We want to make the actual actions read clearer, and avoid arbitrary values
- We want to be set up to to build more complex sequences easily.

Let's start with the first one. We can do this by just shoving those inside the subsystem.

```java
//A custom subsystem class
ExampleRollers extends subsystem base
	public void runMotor(double power){/*code to run motor*/}
	public boolean thingLoaded(){/*checks a sensor*/}

	public Command runMotorCommand(double power){
		return Commands.run(this.runMotor(1),this);
	}
}
```

```java 
//in robot container
joystick.a().whileTrue(exampleRollers.runMotorCommand(1));
joystick.b().whileTrue(exampleRollers.runMotorCommand(-1));
```

So far so good! This shortens the robotContainer up quite a bit. We also notice that this handily cleans up the risk of a forgotten Requires.

Next, we want to clarify the actions. Unfortunately, the action is tied to the value we provide. So we need to avoid passing in a value. This means we can't use the same function in both places, but that's OK. Let's just assign names and shortcuts and keep going. 

```java
//A custom subsystem class
ExampleRollers extends subsystem base
	public void runMotor(double power){/*code to run motor*/}
	public boolean thingLoaded(){/*checks a sensor*/}

	public Command runMotorCommand(double power){
		return Commands.run(this.runMotor(1),this);
	}
	public Command intake(){return runMotorCommand(1);}
	public Command eject(){return runMotorCommand(-1);}
}
```

```java 
//in robot container
joystick.a().whileTrue(exampleRollers.intake());
joystick.b().whileTrue(exampleRollers.eject());
```

This is looking good! Our RobotContainer now looks super clean! The subsystem is also pretty good. We're slowly building more complicated bits on top of each other, and we're not duplicating code in the process.

At this point, nothing we've done changes _how_ the code works. We've just shuffled things around, and changed where we manage various details. A good refactor! 

Now, we're ready to tackle what we _wanted_ to do: Add some features to stop the intake once it's loaded, and jostle things a bit before ejecting them. Fortunately, this is now *really* easy; We're well set up for it. 

```java
//A custom subsystem class
ExampleRollers extends subsystem base
	public void runMotor(double power){/*code to run motor*/}
	public boolean isThingLoaded(){/*checks a sensor*/}

	public Command runMotorCommand(double power){
		return Commands.run(this.runMotor(1),this);
	}
	public Command intake(){return runMotorCommand(1);}
	public Command eject(){
		//Jolt the system because it jams or something!
		return Commands.sequence(
			runMotorCommand(1).withTimeout(0.1), 
			runMotorCommand(-1),
		);
	}
}
```

```java 
//in robot container
joystick.a().whileTrue(exampleRollers.intake().until(example::isThingLoaded));
joystick.b().whileTrue(exampleRollers.eject());
```

Without getting to the weeds of  [[Subsystems]] and [[Commands]] best practices, part of the problem was solved by modifying a created command in RobotContainer, and part of it the base command created in the subsystem. The important detail is that we can fix things easily and properly. 
