---
tags:
---

Requires:[[Robot Code Basics]]
Recommends:[[Commands]]


## Success Criteria
- [ ] Spin a motor
- [ ] Configure a motor with max current
- [ ] Control on/off via joystick button
- [ ] Control speed via joystick

## Setup and prep

> [!Caution] Learning order
> This curriculum does not require or assume solid knowledge of [[Commands|Command]] structure; It's just about spinning motors. All code here uses existing Example structure that's default on Command Based Robot projects. 
> However, it's recommended to learn Motor Control alongside or after [[Commands]], as we'll use them for everything afterwards anyway.

> [!NOTE] Rev Lib
> This documentation assumes you have the third party Rev Library installed. You can find instructions here. 
> https://docs.wpilib.org/en/latest/docs/software/vscode-overview/3rd-party-libraries.html

> [!NOTE] Wiring and Electrical
> This document also assumes correct wiring and powering of a motor controller. This should be the case if you're using a testbench. 

> [!NOTE] Identifying the right motor
> Most of our motors will have sticky notes with the correct ID number; However, if something doesn't seem to work, you can check it using this process using the 
> [[Rev Hardware Client]]
> ![[Rev Hardware Client#Identifying a motor ID]]



## Java Code

### Minimal Example

Conceptually, running a motor is _really_ easy, with just a few lines of code! You can, in fact, run a motor with this.

```java
// ExampleSubsystem.java
ExampleSubsystem extends SubsystemBase{
	/// Other code+functions; Ignore for now.
	int motorID = 0; //This will depend on the motor you need to run
	SparkMax motor = new SparkMax(motorID,MotorType.kBrushless);

	public ExampleSubsystem(){}

	public void periodic(){
		// Will run once every robot loop
		// Motors will only run when it's enabled...
		// but will *always* run when enabled!
		motor.set(0.1);
		//Motor range is [-1 .. 1], but we want to run slow for this
	}
}
```

The hard part is always in making it do what you want, when you want. 

### Practical Example
Most useful robots need a bit more basic setup for the configuration. This example code walks through some of the most common configurations used when making a simple motor system.
```java
// ExampleSubsystem.java
ExampleSubsystem extends SubsystemBase{
	/// Other code+functions; Ignore for now.
	int motorID = 0; //This will depend on the motor you need to run
	SparkMax motor = new SparkMax(motorID,MotorType.kBrushless);
	
	public ExampleSubsystem(){
		//First we create a configuration structure
		var config = new SparkMaxConfig();
		//This is the "passive" mode for the motor.
		// Brake has high resistance to turning
		// Coast will spin freely. 
		// Both modes have useful applications
	    config.idleMode(IdleMode.kBrake);
	    // This changes the direction a motor spins when you give
	    // it a positive "set" value. Typically, you want to make 
	    // "positive inputs" correlate to "positive outputs",
	    // such as "positive forward" or "positive upward"
	    config.inverted(false);
		// Reduce the maximum power output of the motor controller. 
		// Default is 80A, which is *A LOT* of power!.
		// 10 is often a good starting point, and you can go up from there
	    config.smartCurrentLimit(10);
	    // This controls the maximum rate the output can change. 
	    // This is in "time between 0 and full output". A low value
	    // makes a more responsive system. 
	    // However, zero puts a *lot* of mechanical and electrical 
	    // strain on your system when there's turning things on/off.
	    // Having 0.05 is generally an optimal value for starting.
	    // Check the [[Slew Rate Limiting]] article for more info!
	    config.openLoopRampRate(0.05);

		//Lastly, we apply the parameters
	    motor.configure(
	      config, 
	      ResetMode.kResetSafeParameters, 
	      PersistMode.kNoPersistParameters
	    );
	}

	public void periodic(){ }
}
```


![[Rev Hardware Client#^fb223b]]


## A Controllable Robot

#### Make it spin

The next goal is to make a motor spin when you press a button, and stop when you release it.

> [!TIP] Commands
> The following examples utilize the Command structure, but do not require understanding it. You can just follow along, or read [[Commands]] to figure that out. 
> 
> The minimum bit of knowledge is that 
> - Commands are an easy way to get the robot to start/stop bits of code
> - Commands are an easy way to attach bits of code to a controller 
> - The syntax looks a bit weird, that's normal.
> 
> Trying to avoid the Command structure entirely is simply a lot of work, or involves a lot of really bad habits and starting points to unlearn later.

We're going to work with some Example code that exists in any new project. The relevant part looks like this.

```java
// ExampleSubsystem.java
ExampleSubsystem extends SubsystemBase{
	/// Other code+functions be here
	
	public Command exampleMethodCommand() {
		return runOnce(
			() -> {
			  /* one-time action goes here */
			});
	}

	
	public void periodic(){ }
}
```

Let's fill in our subsystem with everything we need to get this moving. 

```java
// ExampleSubsystem.java
ExampleSubsystem extends SubsystemBase{
	int motorID = 0; //This will depend on the motor you need to run
	SparkMax motor = new SparkMax(motorID,MotorType.kBrushless);
	
	public ExampleSubsystem(){
		var config = new SparkMaxConfig();
		// ! Copy all the motor config from the Practical Example above! 
		// ... omitted here for readability ... 
		motor.configure(
			config,
			ResetMode.kResetSafeParameters, 
			PersistMode.kNoPersistParameters
	    );
	}
	
	public Command exampleMethodCommand() {
		return runOnce(
			() -> {
			  motor.set(0.1) // Run at 10% power
			});
	}

	public void periodic(){ }
}
```

Without any further ado, if you deploy this code, enable the robot, and hit "B", your motor will spin! 

`[!!alert-triangle|CAUTION|var(--color-red-rgb)]` Note it will not *stop* spinning! Even when you disable and re-enable the robot, it will continue to spin. We never *told* it how to stop.

An important detail about smart motor controllers is that they remember the last value they were told to output, and will keep outputting it. As a general rule, we want our code to always provide a suitable output. [[Commands]] give us a good tool set to handle this, but for now let's just do things manually.

#### Making it stop

Next let's make two changes: One, we copy the existing `exampleMethodCommand`. We'll name the new copy `stop`, and have it provide an output of 0.

Then, we'll just rename `exampleMethodCommand` to `spin`. We should now have two commands that look like this. 

```java
// ExampleSubsystem.java
ExampleSubsystem extends SubsystemBase{
	// ... All the constructor stuff 
	
	public Command spin() {
		return runOnce(
			() -> {
			  motor.set(0.1) // Run at 10% power
			});
	}

	public Command stop() {
		return runOnce(
			() -> {
			  motor.set(0) // Run at 10% power
			});
	}

}
```

We'll *also* notice that changing the function name of  `exampleMethodCommand` has caused a compiler error in RobotContainer! Let's take a look

```java
public class RobotContainer{
	// ... Bunch of stuff here ...
	private void configureBindings() {
		// ... Some stuff here ...
		
		//The line with a an error
		m_driverController.b()
		.whileTrue(m_exampleSubsystem.exampleMethodCommand());
	}
}

```

Here we see how our joystick is actually starting our  "spin" operation. Since we changed the name in our subsystem, let's change it here too. 

We also need to actually run `stop` at some point; Controllers have a number of ways to interact with buttons, based on the [[Triggers|Trigger]] class. 

We already know it runs `spin` when pressed (`true`), we just need to add something for when it's released. So, let's do that. The final code should look like

```java
public class RobotContainer{
	// ... Bunch of stuff here ...
	private void configureBindings() {
		// ... Some stuff here ...
		
		m_driverController.b()
		.whileTrue(m_exampleSubsystem.spin())
		.onFalse(m_exampleSubsystem.stop())
		;
	}
}
```

If you deploy this, you should see the desired behavior: It starts when you press the button, and stops when you release it.

> [!TIP] Real code flow
> Just a heads up: Once we're experienced with  [[Commands|Commands]] we'll have better options for handling stopping motors and handling "default states". This works for simple stuff, but on complex robots you'll run into problems.

#### Adding a Joystick

So far, we have a start/stop, but only one specific speed. Let's go back to our subsystem, copy our existing function, and introduce some parameters so we can provide arbitrary outputs via a joystick. 

Just like any normal function, we can add parameters. In this  case, we're shortcutting some technical topics and just passing a whole joystick.

```java
// ExampleSubsystem.java
ExampleSubsystem extends SubsystemBase{
	// ... All the constructors and other functions 
	
	public Command spinJoystick(CommandXboxController joystick) {
		return run(() -> { // Note this now says run, not runOnce.
			motor.set(joystick.getLeftY());
		});
	}
}
```

Then hop back to RobotContainer. Let's make another button sequence, but this time on A
```java
public class RobotContainer{
	// ... Bunch of stuff here ...
	private void configureBindings() {
		// ... Some stuff here ...
		
		//Stuff for the B button
		
		//our new button
		m_driverController.a()
		.whileTrue(m_exampleSubsystem.spinJoystick(m_driverController))
		.onFalse(m_exampleSubsystem.stop())
		;
	}
}
```

Give this a go! Now, while you're holding A, you can control the motor speed with the left Y axis.

> [!TIP] Real code: Don't pass joysticks
> Long term, we'll avoid passing joysticks; This makes it *very* hard to keep tabs on how buttons and joysticks are assigned. However, at this stage we're bypassing [[Lambdas|Lambdas]] which are the technical topic needed to do this right. 
> If you can do this with your [[Commands]] knowledge, go ahead and fix it after going through this!






---
## Integrating Command Knowledge

We've dodged a lot of "the right way" to get things moving. If you're coming back here after learning [[Commands]], we can adjust things to better represent a real robot with more solid foundations. 

#### Adding a spin button + Making it stop

Knowing how commands work, you know there's a couple wrong parts here. 
- The runOnce is suboptimal; This sets the motor, exits, and makes it hard to detect when you cancel it. 
- We don't command the robot at all times during motion; Most of the time it's running it's left as the prior state
- We don't need to add an explicit stop command on our joystick; We have the `end` state of our commands that can help. 

Putting those into place, we get
```java
// ExampleSubsystem.java
ExampleSubsystem extends SubsystemBase{
	// ... All the constructor stuff 
	
	public Command spin() {
		return run(() -> {
			  motor.set(0.1) // Run at 10% power
			})
			.finallyDo(()->motor.set(0))
			;
	}
}
```

This lets us simplify our button a bit, since we now know spin() always stops the motor when we're done.
```java
public class RobotContainer{
	// ... Bunch of stuff here ...
	private void configureBindings() {
		// ... Some stuff here ...
		
		m_driverController.b()
		.whileTrue(m_exampleSubsystem.spin())
	}
}
```

As before, our button now starts and stops.

#### Adding a Joystick

Previously, we just passed the whole joystick to avoid DoubleSuppliers and Lambdas. Let's now add this properly. 

```java
// ExampleSubsystem.java
ExampleSubsystem extends SubsystemBase{
	// ... All the constructors and other functions 
	
	public Command spinJoystick(DoubleSupplier speed) {
		return run(() -> {
			motor.set(speed.get());
		})
		.finallyDo(()->motor.set(0))
		;
	}
}
```

Then hop back to RobotContainer, and instead of a joystick, pass the supplier function.
```java
public class RobotContainer{
	// ... Bunch of stuff here ...
	private void configureBindings() {
		// ... Some stuff here ...
		
		//Stuff for the B button
		
		//our new button
		m_driverController.a()
		.whileTrue(m_exampleSubsystem.spinJoystick(m_driverController::getLeftX))

		;
	}
}
```

There we go! We've now corrected some of our shortcuts, and have a code structure more suited for building off of.

