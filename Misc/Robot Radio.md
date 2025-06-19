

https://frc-radio.vivid-hosting.net/overview/quick-start-guide

https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-3/radio-programming.html


## Setting up the radio for competition
You don't! The Field Technicians at competitions will program the radio  for competitions. 

When configured for competition play, you _cannot_ connect to the radio via wifi. Instead, use an ethernet cable, or 


## Setting up the radio for home





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