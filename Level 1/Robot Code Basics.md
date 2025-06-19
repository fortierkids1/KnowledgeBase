[[Motor Control]]
[[Commands]]
[[Encoder Basics]]


## Success Criteria
- [ ] Install the WPILib VS Code IDE
- [ ] Make a new robot project
- [ ] Create a new subsystem
- [ ] Install the Rev third Library
- [ ] Basic requirements to start working  on robot projects
- [ ] Create a new empty subsystem 
- [ ] Create a new empty command
- [ ] Add your new command and subsystem to RobotContainer.

#### Goals 

Programming prerequisites, listed in [[Coding Basics]] for now


https://docs.wpilib.org/en/stable/docs/software/commandbased/structuring-command-based-project.html

# Robot Code structure

When you open a new robot project you'll see a lot of files we'll interact with.

- `src/main`
	- `deploy`
	- `java`
	- `frc/frc/robot`
		- `commands`
			- `ExampleCommand.java`
		- `subsystems`
			- `ExampleSubsystem.java`
		- `Constants.java`
		- `Main.java`
		- `Robot.java`
		- `RobotContainer.java`
- `vendordeps`

For typical projects, you'll be spending the most time in `RobotContainer`, `subsystems`, and occasionally `commands`. 

For some early practice projects, or special use cases you might also interact with `Robot.java` a bit.

## Third Party Libraries

Many helpful utilities we'll use for robot projects are represented using code that's not available by default. WPILib has a small manager to assist with installing these, detailed here: 

https://docs.wpilib.org/en/latest/docs/software/vscode-overview/3rd-party-libraries.html#adding-offline-libraries

## Third Party Tools
We'll also utilize a number of software tools for special interactions with hardware or software components. Some of these include 
- [[Rev Hardware Client]] , a motor controller management tool
- [[LaserCan|GrappleHook]] , which manages the LaserCan configuration
- [NI Driver Station](https://docs.wpilib.org/en/stable/docs/software/driverstation/driver-station.html) is needed for systems that will want to enable and drive a robot. 

## Putting Things in the Proper Place

The hardest part of getting started with robots is figuring out _where_ your robot code goes. 

#### Robot.java : A microcausm of the complete robot

`Robot.java` is a very powerful file, and it's possible to write your _entire_ robot in just this one file! For reasons we'll get into, we _do not_ want to do this. However, the setup of it does a good job explaining how a robot works. Let's look at the structure of this file for now

```java
public class Robot extends TimedRobot {
	private Command m_autonomousCommand;
	private final RobotContainer m_robotContainer;
	public Robot() {
		m_robotContainer = new RobotContainer();
	}
	
	public void robotPeriodic() {}
	
	public void disabledInit() {}
	public void disabledPeriodic() {}
		
	public void autonomousInit() {}
	public void autonomousPeriodic() {}
	
	public void teleopInit() {}
	public void teleopPeriodic() {}
	
	public void testInit() {}
	public void testPeriodic() {}
	
	//a few more ignored bits for now
}
```


From the pairing, we can group these into several different modes
- "Robot"
- Autonomous
- Teleop
- Test

Indeed, if we look at our [[Driver Station]], we see several modes mentioned.
![[driverstation.jpg]]
Teleop, Auto, and Test are simply selectable operational modes. However, you might want to utilize each one slightly differently.

"Practice mode" is intended to simulate real matches: This DriverStation mode runs Autonomous mode for 15 seconds, and then Teleop Mode for the remainder of a match time. 

"Disabled" mode is automatically selected whenever the robot is not enabled. This includes when the robot boots up, as well as as whenever you hit "disabled" on the driver station.
Disabled mode will _also_ cancel any running [[Commands]] . 

"Robot mode" isn't an explicit mode: Instead, of "Robot Init", we just use the constructor: It runs when the robot boots up. In most cases, the primary task of this is to set up Robot Container.
`robotPeriodic` just runs every loop, regardless of what other loop is also running.


We can also see a grouping of 
- Init
- Periodic
Whenever any new "mode" starts, we first run the Init function once, and _then_ we run the periodic. The robot will continue run associated Periodic functions every loop, 50 times per second. 

We generally won't add much code in Robot.java, but understanding how it works is a helpful starting point to understanding the robot itself. 

#### RobotContainer.java
As mentioned above, the "Robot Container" is created as the robot boots up. When you create a new project, 
This file contains a small number of functions and examples to help you organized. 

```java
public class RobotContainer(){
	ExampleSubsystem subsystem = new ExampleSubsystem();
	ExampleCommand command = new ExampleCommand();
	CommandXboxJoystick joystick = new CommandXboxJoystick(0);
	RobotContainer(){
		configureBindings();
	}
	public void configureBindings(){
		//Not a special function; Just intended to help organize 
	}
	public Command getAutonomousCommand(){/*stuff*/}
}
```

This file introduces a couple new concepts  
- [[Commands]], which form the  "actions" you want the robot to perform
- [[Subsystems]], or the different parts of the robot that could perform actions
- [[Joysticks]] , which serve as the standard input method.

The use of Commands and Subsystems goes a *long* way to managing complex robot interactions across many subsystems. However, they're certainly tricky concepts to get right off the bat. 

#### Constants.java
Sometimes, you'll have oddball constants that you need to access in multiple places in your code. Constants.java advertises itself as a place to sort and organize files. 

Without getting too into the "why", in general you should minimize use of Constants.java; It leads to several problems as your robot complexity increases. 

Instead, simply follow good practices for scope encapsulation, and keep the constants at the lowest necessary scope. 

- If a value is used once, just use a value. This includes a lot of setup values like PID tuning values.
- If your value is used repeatedly inside a subsystem, make it a private constant in that subsystem. This is common for conversion factors, min/max values, or paired output values
- If a constant is strongly associated with a subsystem, but needs to be referenced elsewhere, make it a public constant in that subsystem.
- Lastly, if something is not associated with a subsystem, and used repeatedly across multiple subsystems, constants.java is the place. 

If you find yourself depending on a lot of constants, you might need to consider [[Refactoring]] your code a bit to streamline things.  Note that Stormbots code has almost nothing in here! 
