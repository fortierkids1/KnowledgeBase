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
> The vast majority of colors contained in the WPILib color class are not great, they were designed for use on an lcd screen rather than a standard RGB LED strip and do not translate well

## Blinkins

![[blinkin.png]]
The blinkin is an LED driver made by REV Robotics for FRC with support for both 12v RGB LEDs and 5v Addressable LEDs. The blinkin comes installed 100 premade patterns. In code it is controlled like a servo, with microsecond pulses to control what pattern is selected. The StormBots have written a class recording these pulse lengths for use in future code.

>[!bug] Strip Selection
>The blinkins have an error where electrical noise can trigger a command designed for factory testing where the strip selection output will be switched, turning off whatever LEDs you have connected. To correct this you have to send a 2125 us pulse to the blinkin periodically to ensure it is on the right strip.

## Addressable LED
Controlling LEDs using addressable LED requires that the LED strip be connected to the RIO via PWM. 