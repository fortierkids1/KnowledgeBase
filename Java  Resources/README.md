# Java Resources

Learning Java
---
[WPLib Getting Started](http://wpilib.screenstepslive.com/s/3120/m/7885/l/79405-installing-the-java-development-tools) Walks you through programming your first robot anbd setting up neatbeans.

[API Function List] (http://first.wpi.edu/FRC/roborio/release/docs/java/index.html) 


[Getting Started with Java for FRC](http://first.wpi.edu/Images/CMS/First/Getting_Started_with_Java_for_FRC.pdf)

[FRC Java Center](http://first.wpi.edu/FRC/frcjava.html)

[Mean Machines' GitHub](https://github.com/tmm2471) has some very cool example code, as well as cad models for some components.

Interfacing with Driver Station
------------------------
Reading match time, team color, and robot position [Interface with Driver Station](http://wpilib.screenstepslive.com/s/3120/m/7912/l/133045-driver-station-input-overview)

Interfacing with Sensors
------------------------
Many of these are discussed with basic sample code in the [WPILib Users's Guide](http://first.wpi.edu/Images/CMS/First/WPILibUsersGuide.pdf). That's a very helpful starting point, and then additional documention is listed at their [primary documentation site](http://wpilib.screenstepslive.com/s/3120/m/7912/l/85672-what-is-wpilib), some of which is specified below.

Distance Sensors/Sonar: See analog

[Quadrature Encoders](http://wpilib.screenstepslive.com/s/3120/m/7912/l/85770-measuring-rotation-of-a-wheel-or-other-shaft-using-encoders) are useful for very precise monitoring of rotation and position. This is used on the BunnyBot. It's also very important for many PID controllers.

I2C

[Analog Sensors](http://wpilib.screenstepslive.com/s/3120/m/7912/l/85775-analog-inputs) are handy for a variety of sensors, such as potentiometers and range sensors

[Analog Triggers](http://wpilib.screenstepslive.com/s/3120/m/7912/l/85776-analog-triggers) help set up "switches" when measuring movements with analog sensors. 

Absolute Encoders

[Geartooth and Counter library](http://wpilib.screenstepslive.com/s/3120/m/7912/l/85635-using-counters) is useful for monitoring shaft rotation and high-speed wheel rotation. 


Interfacing with motors and actuators
-------------------------------------
Ther'es lots of discussion here: [Speed Controller Objects](http://wpilib.screenstepslive.com/s/3120/m/7912/c/38335)

[PID Controllers](http://wpilib.screenstepslive.com/s/3120/m/7912/l/79828-operating-the-robot-with-feedback-from-sensors-pid-control)

[Servos](http://wpilib.screenstepslive.com/s/3120/m/7912/l/132341-repeatable-low-power-movement-controlling-servos-with-wpilib)


Other useful libraries and tools
--------------------------------
DriverStation

Network Communication

CAN Bus

Read Team color from the Control Center

State Machines

Threaded processing

