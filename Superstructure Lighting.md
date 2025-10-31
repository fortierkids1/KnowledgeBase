---
aliases:
  - Lighting
tags:
  - stub
---

## Success Criteria
- [ ] Create an LED subsystem
- [ ] Use WPILib Color utility
- [ ] Control LEDS using Blinkin
- [ ] Control LEDS using AddressableLED


## LEDs

The primary purpose of LEDs in FRC is for communication with the driver. LEDs changing color can signal that the robot is in position, tell the human player what gamepiece we want, show that the robot is ready to shoot, or alert the drivers to the start of endgame. This can be a powerful tool to reduce operator doubt and make our cycles dramatically more efficient

## WPILib Color Utils

The WPI Library comes pre installed with a class with 143 pre defined colors in RGB format you can use as an input to control an LED strip

> [!warning] Warning
> The colors contained in the WPILib color class are not great



## Blinkins

![[blinkin.png]]
The blinkin is an LED driver made by REV Robotics for FRC with support for both 12v RGB LEDs and 5v Addressable LEDs

>[!bug] Strip Selection
>