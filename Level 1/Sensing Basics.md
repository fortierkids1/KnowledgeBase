---
tags:
  - stub
---

Requires:
[[Triggers]]

Hardware: 
- Switches
- Encoder
- LaserCan

## Success Criteria
- [ ] Create a Trigger that represents a sensor condition
- [ ] Create a Joystick command that runs indefinitely, but stops when the Trigger is in a true condition. 
- [ ] Repeat with a different sensor type
- [ ] Create a Trigger that performs a Command automatically when triggered

#### Summary
Sensing is interacting with physical objects, and changing robot behaviour based on it.
This can use a variety of sensors and methods, and will change from system to system



## Sensor Information transfer
Often simple sensors like break beams or switches can tell you something very useful about the system state, which can help you set up a different, more precise sensor. 

The most common application is in [[Homing Sequences|Homing]] such as a [[SuperStructure Elevator|Elevator]] type systems. On boot, your your [[Encoder Basics|Encoder]] may not properly reflect the system state, and thus the elevator position is invalid. But, if you you have a switch at the end of travel you can use this to re-initialize your encoder, as the simple switch.

