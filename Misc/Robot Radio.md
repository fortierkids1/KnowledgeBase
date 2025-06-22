


> [!NOTE] See Official Documentation
> The official radio documentation is complete and detailed, and should serve as your primary resource. 
> https://frc-radio.vivid-hosting.net/
> 
> However, It's not always obvious _what_ you need to look up to get moving. Consider this document just a simple guide and jumping-off point to find the right documentation elsewhere




https://frc-radio.vivid-hosting.net/overview/quick-start-guide

https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-3/radio-programming.html


## Setting up the radio for competition
You don't! The Field Technicians at competitions will program the radio  for competitions. 

When configured for competition play, you _cannot_ connect to the radio via wifi. Instead, use an ethernet cable, or 


## Setting up the radio for home

The home radio configuration is a common pain point

https://frc-radio.vivid-hosting.net/overview/programming-your-radio-at-home#overview
#### Option 1: Wired connection
This option is the simplest: Just connect the robot via an ethernet or USB, and do whatever you need to do. For quick checks, this makes sense, but obviously is suboptimal for things like driving around. 

#### Option 2: 2.4GhZ Wifi Hotspot
The radio _does_ have a 2.4ghz wifi hotspot, albeit with some limitations. This mode is suitable for many practices, and is generally the recommended approach for most every-day practices due to ease of use.

https://frc-radio.vivid-hosting.net/access-points/setting-vh-109-to-access-point-mode#instructions

Note, this option requires access to the tiny DIP switches on the back of the radio! You'll want to make sure that your hardware teams don't mount the radio in a way that makes this impossible to access. 

#### Option 3: Tethered Bridge

This option uses a _second_ radio to connect your laptop to the robot. This is the most cumbersome and limited way to connect to a robot, and makes swapping who's using the bot a bit more tricky. 

However, this is also the most performant and reliable connection method. This is recommended when doing extended driving sessions, final performance tuning, and  other scenarios where you're trying to simulate competition-ready environments. 

This option has a normal robot on one end, and your driver-station setup will look the following image.  See https://frc-radio.vivid-hosting.net/overview/practicing-at-home for full setup directions
![[vidid-radio-wifi-bridge.png.png]]


## Bonus Features

#### Port Forwarding
Port forwarding allows you to bridge networks across different interfaces. 

The practical application in FRC is being able to access network devices via the USB interface! This is mostly useful for quickly interfacing with Vision hardware like the [[Limelight Basics|Limelight]] or [[PhotonVision Basics|Photonvision]] at competitions.

```java
//Add in the constructor in Robot.java or RobotContainer.java

// If you're using a Limelight
PortForwarder.add(5800, "limelight.local", 5800);
// If you're using PhotonVision
PortForwarder.add(5800, "photonvision.local", 5800);
```

https://docs.wpilib.org/ja/latest/docs/networking/networking-utilities/portforwarding.html

#### Scripting the radio

The radio has some scriptable interfaces, allowing programmatic access to quickly change or read settings. 

https://frc-radio.vivid-hosting.net/advanced-topics/programming-your-radio-advanced