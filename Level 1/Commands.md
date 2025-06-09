Requires:
[[Robot Code Basics]]
## Success Criteria
- [ ] Create a command that runs indefinitely
- [ ] Have that command start+end on a joystick button
- [ ] Create a command that starts on a joystick press, and stop it with a different button
- [ ] Create a default command that lets you know when it's running through [[Basic Telemetry|Telemetry]]
- [ ] Create a runCommand using a function pointer
- [ ] Create a runCommand using a lambda



> [!NOTE] Learning order
> You can learn this without having done [[Motor Control]], but it's often more fun to learn alongside it in order to have more interesting, visual commands while experimenting.
> The commands provided as an example just print little messages visible in the RioLog, allowing this to be set up without motors

## What is a command

A Command is an event driven code structure that allows you manage when code runs, what resources it uses, and when it ends. 

In the context of a robot, it allows you to easily manage a lot of the complexity involved with managing multiple [[Subsystems]] 

The code structure itself is fairly straightforward, and defines a few methods; Each method defines what code runs at what time. 
```java
class ExampleCommand extends CommandBase{
	public ExampleCommand(){}
	public void initialize(){}
	public void execute(){}
	public boolean isFinished(){ return false; }
	public void end(boolean cancelled){}
}
```

Behind the scenes, the robot runs a command scheduler, which helps manage what runs when. Once started, a command will run according to the following flowchart, more formally known as a [[State Machines|state machine]]. 

```mermaid
stateDiagram-v2
	direction LR
	[*] --> initialize
	initialize --> execute
	execute --> isFinished
	isFinished --> execute : false
	isFinished --> end : true
	end -->[*]
```

This is the surface level complexity, which sets you up for how to view, read, and write commands. 

## Requirements and resources

A key aspect of Commands is their ability to claim temporary, exclusive  ownership over a [[Subsystem]] .  This is done by passing the subsystem into a command, and then adding it as a requirement
```java
class ExampleCommand extends CommandBase{
	public ExampleCommand(ExampleSubsystem subsystemName){
		addRequirements(subsystemName);
	}
```

Now, whenever the command is started, it will forcibly claim that subsystem. It'll release that claim when it runs it's end() block.

This ability of subsystems to hold a claim on a resource has a lot of utility. The main value is in preventing you from doing silly things like trying to tell a motor to go forward _and_ backward at once.



## Events and interruptions

Now that we've established subsystem ownership, what happens when you *do* try to tell your motor to go forward and then backward?

When you start the command, it will forcibly interrupt other commands that share a resource with it, ensuring that the new command has exclusive access. 

It'll look like this
```mermaid
stateDiagram-v2
	direction LR
	[*] --> initialize
	initialize --> execute
	execute --> isFinished
	isFinished --> execute : false
	isFinished --> end : true
	end -->[*]

	note left of initialize
		existing commands that share subsystems are cancelled here
	end note
	note left of end
		subsystems are released here.
	end note
```

When a command is cancelled, the command scheduler runs the commands `end(cancelled)` block, passing in a value of true. Whole not typical, *some* commands will need to do different cleanup routines depending on whether they exited on a task completion, or if something else kicks em off a subsystem. 

## Starting and Stopping Commands

Commands can be started in one of 3 ways:
- via a [[Triggers|Trigger]]'s start condition
- Directly scheduling it via the command's `.schedule()` method.
- Automatically as a DefaultCommand

They can be stopped via a few methods
- When the command returns `true` from it's isFinished() method
- When launched by a [[Triggers|Trigger]], and the run condition is no longer met
- Calling a command's `.cancel()` method directly
- When the command is cancelled by a new command that claims a required subsystem.
## Default Commands

It's often the case that a subsystem will have a clear, preferred action when nothing else is going on. In some cases, it's stopping a spinning roller, intake, or shooter. In others it's retracting an intake. Maybe you want your lights to do a nice idle pattern. Maybe you want your chassis joystick to just start when the robot does.

Default commands are ideal for this. Default commands run just like normal commands, but are automatically re-started once nothing else requires the associated subsystem resource.

Just like normal command, they're automatically stopped when the robot is disable, and cancelled when something else requires it. 
Unlike normal commands, it's _not_ allowed to have the command return true from `isFinished()`. The scheduler expects default commands to run until they're cancelled. 

Also unlike other commands, a subsystem _must_ require the associated subsystem, and _cannot_ require other subsystems. 

> [!CAUTION] Command groups + default commands
> It's worth making a note that a Default Command cannot start during a Command Group that contains a command requiring the subsystem! If you're planning complex command sequences like an auto, make sure they don't rely on DefaultCommands as part of their operation.


## When to require

As you're writing new subsystems, make sure you consider whether you *should* require a subsystem. 

You'll always want to require subsystems that you will modify, or otherwise need exclusive access to. This commonly involves commands that direct a motor, change settings, or something of that sort. 

In some cases, you'll have a subsystem that _only_ reads from a subsystem. Maybe you have an LED subsystem, and want to change lights according to an Elevator subsystems's height. 
One way to do this is have a command that requires the LEDs (needs to change the lights), but does not require the Elevator (it's just reading the encoder).

As a general rule, most commands you write will simply require exactly one subsystem. Commands that need to require multiple subsystems can come up, but typically this is handled by command composition and command groups.
## External Commands

Every new project will have an example command in a dedicated file, which should look familiar
```java
class ExampleCommand extends CommandBase{
	public ExampleCommand(){
		//Runs once when the command is created as the robot boots up.
		//Register required subsystems, if appropriate
		//addRequirements(subsystem1, subsystem2...);
	}
	public void initialize(){
		//Runs once when the command is started/scheduled
	}
	public void execute(){
		//Runs every code loop
	}
	public boolean isFinished(){
		//Returns true if the command considers it's task done, and should exit
		return false;
	}
	public void end(boolean cancelled){
		//Perform cleanup; Can do different things if it's cancelled
	}
}
```
This form of command is mostly good for instructional purposes while you're getting started. 

On more complex robot projects, trying to use the file-based Commands forces a lot of mess in your Subsystems; In order for these to work, you need to make many of your Subsystem details public, often requiring you to make a bunch of extra functions to support them. 

## Command Factories

Command factories are the optimal way to manage your commands. With this convention, you don't create a separate  Command files, but create methods in your [[Subsystems|Subsystem]] that build and return new Command objects. This convention is commonly called a "Factory" pattern. 
Here's a short example and reference layout:

```java
//In your subsystem
Roller extends SubsystemBase{
	Roller(){}

	public Command spinForward(){
		return Commands.run(()->{
			System.out.println("Spin Forward!!");
		},this);
	}
}
```

```java 
//In your robotContainer, let's create a copy of that command
RobotContainer{
	RobotContainer(){
		joystick.a().whileTrue(roller.spinForward());
	}
}

```
That's it! Not a lot of code, but gives you a flexible base to start with.

This example uses `Commands.run()` one of the many options in the [Commands Class](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj2/command/Commands.html). These command shortcuts let you provide [[Lambdas]] representing some combination of a Command's normal Initialize, Execute, isFinished, or End functions. A couple notable examples are

- Commands.run : Takes a single lambda for the Execute blocks
- Commands.startRun : Takes two lambdas for the Initialize and Execute blocks
- Commands.startEnd : Takes two lambdas for the Initialize and End Blocks

Most commands you'll write can be written like this, making for simple and concise subsystems. 

> [!BUG] Watch the Requires
> Many `Commands` helpers require you to provide the required subsystem after the lambdas. If you forget, you can end up with multiple commands fighting to modify the current subsystem

Building on the above, Subsystems have several of these command helpers build in! You can see `this.startRun(...)`, `this.run(..)` etc; These commands work the same as the `Commands.` versions, but automatically include the current subsystem.

There's a notable special case in `new FunctionalCommand(...)`, which takes 4 lambdas for a full command, perfectly suitable for those odd use cases.

## Command Composition 

The real power of commands comes from the [Command Compositions](https://docs.wpilib.org/en/stable/docs/software/commandbased/command-compositions.html) , and "decorator" functions. These functions enable a lot of power, allowing you to change how/when commands run, and pairing them with other commands for complex sequencing and autos. 

For now, let's focus on the two that are more immediately useful: 
- `command.withTimeout(time)` , which runs a command for a set duration. 
- `command.until(()->someCondition)` , which allows you to exit a command on things like sensor inputs. 

Commands also has some helpful commands for hooking multiple commands together as well. The most useful is a simple sequence.
```java
Commands.sequence(
	roller.spinForward().withTimeout(0.1),
	roller.spinBackward().withTimeout(0.1),
	roller.spinForward().withTimeout(0.5)
)
```


