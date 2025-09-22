
Absolute encoders are sensors that measure a physical rotation directly. These differ from [[Encoder Basics|Relative Encoders]] due to measurement range, as well as the specific data they provide.

## Success Criteria
- [ ] Take testbench, define a range of motion with measurable real-world angular units.
- [ ] Configure an absolute encoder to report units of that range
- [ ] Validate that the reported range of the encoder is accurate over the fully defined range of motion.
- [ ] Validate the 


## Differences to Relative encoders

If we recall how [[Encoder Basics|Relative Encoders]] work, they tell us nothing about the system until verified against a reference. Once we have a reference and initialize the sensor, then we can track the system, and compute the system state.  

In contrast, absolute encoders are designed to capture the full system state all at once, at all times. When set up properly, the sensor itself is a reference.

## Similarities to Relative Encoders

Both sensors track the same state change (rotation), and when leveraged properly, can provide complete system state information

## Mechanical Construction

While the precise construction can vary, many absolute encoders tend to work in the same basic style: divide your measured distance into two regions. Then divide those two regions into two more regions each, and repeat as many times as needed to get the desired precision!

When you do this across a single rotation, you get a simple binary encoder shown here: 

![[encoder-gray-code.png]]

With 3 subdivisions, you can divide the circle in $2^3$ regions. This means at all times, you know where you are within a 45 degree region. Since each region is on/off, it's common to describe absolute encoders as "X bit" resolution; One bit per sensor region.

Commonly, you'll see encoders with one of the following resolutions. 

| Resolution (bits) | divisions | Resolution (degrees) |
| ----------------- | --------- | -------------------- |
| 8                 | 256       | 1.40                 |
| 10                | 1024      | 0.35                 |
| 11                | 2048      | 0.17                 |
| 12                | 4096      | .09                  |

## Reading Absolute Encoders

The typical encoder we use in FRC is the [Rev Through Bore Encoder](https://docs.revrobotics.com/rev-crossover-products/sensors/tbe/application-examples) . This is a 10 bit encoder, and provides interfaces by either 
- Plugging it into the Spark Max
- Plugging it into the RoboRio's DIO port. 

#### Connected as a RoboRio DIO device
When plugged into the RoboRio, you can interface with it using the [DutyCycleEncoder](https://docs.wpilib.org/en/stable/docs/software/hardware-apis/sensors/encoders-software.html) class and associated features.

```java
public class ExampleSubsystem{
	// Initializes a duty cycle encoder on DIO pins 0 
	// Configure it to to return a value of 4 for a full rotation, 
	// with the encoder reporting 0 half way through rotation (2 out of 4)
	DutyCycleEncoder encoder = new DutyCycleEncoder(0, 4.0, 2.0);
	
	//... other subsystem code will be here ...
	
	public void periodic(){
		//Read the encoder and print out the value
		System.out.println(encoder.get());
	}
}
```

Real systems will likely use encoder ranges of  `2*Math.PI` (for Radians) or `360` (for degrees). 

The "zero" value will depend on your exact system, but should be the encoder reading when your system is at a physical "zero" value. In most cases, you'd want to correlate "physical zero" with an arm horizontal, which simplifies visualizing the system, and calculations for [[FeedForwards]] for [[SuperStructure Arm|Arm subsystems]] later. However, use whatever makes sense for your subsystem, as defined by your [[Robot Design Analysis]]'s coordinate system.


#### Connected as a Spark Max device

When a Through Bore Encoder is connected to a Spark, it'll look very similar to connecting a [[Encoder Basics|Relative Encoder]] in terms of setting up the Spark and applying/getting config, with a few new options

```java
ExampleSubsystem extends SubsystemBase{
	SparkMax motor = new SparkMax(10, MotorType.kBrushless);
	// ... other stuff
	public void ExampleSubsystem(){
		SparkBaseConfig config = new SparkMaxConfig();

		//Configure the reported units for one full rotation.
		// The default factor is 1, measuring fractions of a rotation.
		// Generally, this will be 360 for degrees, or 2*Math.PI for radians
		var absConversionFactor=360;
		config.absoluteEncoder
		.positionConversionFactor(absConversionFactor);
		//The velocity defaults to units/minute ; Units per second tends to
		//preferable for FRC time scales.
		config.absoluteEncoder
		.velocityConversionFactor(absConversionFactor / 60.0);
		//Configure the "sensor phase"; If a positive motor output 
		//causes a decrease in sensor output, then we want to set the 
		// sensor as "inverted", and would change this to true.
		config.absoluteEncoder
		.inverted(false);

		motor.configure(
			config,
			ResetMode.kResetSafeParameters,
			PersistMode.kPersistParameters
		);
	}
	// ... other stuff
	public void periodic(){
		//And, query the encoder for position.
		var angle = motor.getAbsoluteEncoder().getPosition();
		var velocity = motor.getAbsoluteEncoder.getVelocity();
		// ... now use the values for something.
	}
}
```


## Discontinuities
Remember that the intent of an absolute encoder is to capture your system state directly. But what happens when your system can exceed the encoder's ability to track it? 

If you answered "depends on the way you track things", you're correct. By their nature absolute encoders have a "discontinuity"; Some angle at which they jump from one end of their valid range to another. Instead of `[3,2,1,-1,-2]` you get `[3,2,1,359,358]!` You can easily imagine how this messes with anything relying on those numbers.. 

For a Through Bore + Spark configuration, by default it measures "one rotation", and the discontinuity matches the range of 0..1 rotations , or 0..360 degrees with a typical conversion factor. This convention means that it *will not* return negative values when read through `motor.getAbsoluteEncoder().getPosition()` ! 

![[absolute-angle-discontinuity.svg]]

Unfortunately, this convention often puts the discontinuity *directly* in range of motion, meaning we have to deal with it frequently. [[PID]] controllers *especially* do not like discontinuities in their normal range. 

Ideally, we can move the discontinuity somewhere we don't cross it due to physical hardware constraints.

There's a few approaches you can use to resolve this, depending on exactly how your system *should* work, and what it's built to do!

#### Zero Centering 
This is the easiest and probably ideal solution for many systems. The Spark has a method that changes the system from reporting `[0..1)`rotations to `(-0.5..0.5]`. rotations. Or, with a typical conversion factor applied, `(-180..180]` degrees. 
```java
ExampleSubsystem extends SubsystemBase{
	// ... other stuff
	public void ExampleSubsystem(){
		SparkBaseConfig config = new SparkMaxConfig();
		config.absoluteEncoder.zeroCentered(true);
		// .. other stuff
		}
	}
```

Most FRC systems won't have a range of 180 degrees, making this a very quick and easy fix. 

> [!WARNING] PID Integration
> Rev documentation makes it unclear if `zeroCentered(true)` works as expected with the onboard Spark PID controller. 
> If you test this, report back so we can replace this warning with correct information. 

#### Handle the Discontinuity in your Closed Loop

Since this is common, some [[PID]] or [[Closed Loop Controller|Closed Loop]] controllers can simply take the discontinuity directly in their configuration. This bypasses the need to fix it on the sensor side.

For Sparks, the configuration option is as follows:
```java
sparkConfig.closedLoop.positionWrappingInputRange(min,max);
```


> [!WARNING] Setpoint Wrapping in controllers 
> Be mindful of how setpoints are wrapped when passed to the controller! Just because the sensor is wrapped, doesn't mean it also handles setpoint values too.
> If the PID is given an unreachable setpoint due to sensor wrapping, it can generate uncontrolled motion. Make sure you check and use wrapper functions for setpoints as needed.


#### Handle the discontinuity in a function

In some cases, you can just avoid directly calling `motor.getAbsoluteEncoder().getPosition()`,  and instead go through a function to handle the discontinuity. This usually looks like this

```java
// In a subsystem using an absolute encoder
private double getAngleAbsolute(){
	double absoluteAngle = motor.getAbsoluteEncoder().getPosition();
	// Mote the discontinuity from 0 to -90
	if(absoluteAngle>270){
		absoluteAngle-=360;
	}
	return absoluteAngle;
}
```

This example gives us a range of -90 to 270, representing a system that could rotate anywhere *but* straight downward. 

This pattern works well for code aspects that live on the Roborio, but note this doesn't handle things like the onboard Spark PID controllers! Those still live with the discontinuity, and would cause problems.

#### Transfer the reading to a relative encoder
Instead of using the Absolute encoder as it's own source of angles, we simply refer to the [[Encoder Basics|Relative Encoder]]. In this case, both encoders should be configured to provide the same measured unit (radians/degrees/rotations of the system), and then you can simply read the value of the absolute, and set the state of the relative. 

More information for this technique is provided at [[Homing Sequences|Homing Sequences]], alongside other considerations for transferring data between sensors like this.


## Build Teams, Code, and Encoders

Since an absolute encoder represents a physical system state, an important consideration is preserving the physical link between system state and the sensor.

On the Rev Through Bore, the link between system state and encoder state is maintained by the physical housing, and the white plastic ring that connects to a hex shaft.

![[Rev Through Bore Encoder.png]]

You can see that the white hex ring has a small notch to help track physical alignment, as does the black housing. The notch's placement itself is unimportant; However, keeping the notch consistency aligned *is* very important!

If we take a calibrated, working system, but then re-assemble it it incorrectly, we completely skew our system's concept of what the physical system looks like. Let's take a look at an example arm.

![[absolute-sensor-rotation-error.svg]]

We can see in this case we have a one-notch error, which is 60 degrees. This means that the system *thinks* the arm is pointing up, but the arm is actually still rather low. This is generally referred to as a "clocking" error.

When we feed an error like this into motor control tools like a [[PID]], the discrepancy means the system will be driving the arm *well* outside your expected ranges! This can result in significant damage if it catches you by surprise. 

As a result, it's worth taking the time and effort to annotate the expected alignment for the white ring and the other parts of the system. This allows you to quickly validate the system in case of rework or disassembly. 

Ideally, build teams should be aware of the notch alignment and it's impact! While you can easily adjust offsets in code, such offsets have to ripple through all active code branches and multiple users, which can generate a to a fair amount of disruption. However, in some cases the code disruption is still easier to resolve than further disassembling and re-assembling parts of the robot. It's something that's bound to happen at some point in the season.


---

## Further Reading

#### Grey Code
Grey code encoders use binary subdivision similar to the "binary encoder" indicated above, but structure their divisions and output table differently. These differences make for some useful properties:
- Only one bit changes at a time during rotation
- Subdivisions are grouped in a way that reduces the rate of change on any given track

If you look closely, the Quadrature signal used by [[Encoder Basics|Relative Encoders]] is a special case of a 2 bit Grey Code! Looking for this "quadrature" pattern where each track has a 50% overlap to the change across adjacent tracks is a giveaway that an encoder is using gray code.

![[Gray-code-table-Gray-coding-to-read-position.gif]]
#### Analog absolute Encoders

In certain systems, you can measure an X and a Y offset, generating a sin and cosine value. The unique sin and cos values generate a unique angle with high precision.

![[Circle_cos_sin.gif]]


### Fun Theory: Range extension through gearing

In some cases like an Elevator you might want to track motion across a larger range than a single encoder could manage. This is most common for linear systems like [[SuperStructure Elevator|Elevators]]. 

By stepping an encoder down, can convert 1 rotation of travel (maybe ~1-3 inches at ~0.01" precision) into a more useful ~50 inches at ~0.5" precision! This gives you absolute knowledge of your system, but at a much lower precision.

However, if you were to stack a normal encoder on top, you could use each encoder within their optimal ranges: One encoder can provide a rough area, and the other can provide the precision.



### Fun Theory: Chinese Remainder Theorem

This is a numerical trick that can allow use of two smaller encoders and some clever math to extend two encoders ranges out a significant distance at high precision. This would permit absolute encoders to effectively handle [[SuperStructure Elevator|Elevator]] systems or other linear travel.

https://en.wikipedia.org/wiki/Chinese_remainder_theorem



##### TODO

- Advantages
- Disadvantages
- Discontinuity handling
- Integration with relative encoders
[[Homing Sequences]]
