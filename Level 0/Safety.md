---
tags:
  - stub
---
## Safety fundamentals
FRC robots, fundamentally, are fast, powerful machines that are built with insufficient time, by students of mixed experience. Which is to say, even at their their best, there's potential for a lot of things to go wrong.

Being safe around the bot doesn't just involve preparing for likely faults, but always acting on *could possibly* go wrong.

#### Big considerations

While there's a few safety concerns when the robot is powered up, the majority of programmer work and concern 
- HANDS OUT. The robot should never be enabled while people are actively engaging with the robot.
- Watch the space: Be aware of the robot's travel potential and work envelope
- Be assertive: While you own the robot, you own the space around the robot.
  
#### Involved Roles

- **Operator** : The person in charge of the bot at any given time
- **Driver** : The person manning the controller and triggering actions
- **Spotter**: Someone who's sole responsibility is ensuring the bot is disabled in case of potential harm or unexpected events.
- **Participants** : People actively engaged with the robot in some level, and paying attention to what it's doing.


#### Pre-flight checklist
- Check controller configurations for the computer, and make sure controllers are in the right ports on the driverstation
- Make sure you know what code is going to be running on the robot.

#### Before enabling, always
- Make sure you have your safety glasses on
- Make sure people around the robot have safety glasses on
- Actively check to make sure hands are clear of the robot, and people are clear of all mechanisms, including drive train.
- If people are working on the robot, establish functionality and ownership and make sure it's clear to enable.
- Make sure people *around* the robot are attentive to the robot.
- If there are inattentive people, clear them away from the robot. 
- Check battery voltage, and avoid running the bot battery too low
- Make sure controllers are clear, and no buttons/joysticks are pressed accidentally. 
- Make sure the enter button is easily hit to disable the robot, or a spotter is handling the enter button.
- Announce intent to enable the robot, by saying "ENABLING!" at a volume sufficient for anyone surrounding the robot to hear.
- Wait for people around the robot to acknowledge your enabling intent.
- Actively check to make sure your people + hands are clear of the robot *again*
- If everything is clear, you may now enable the robot.
#### DO NOT
- Shout "ENABLING!" while enabling the robot. Do it *before* with time to check that it was acknowledged.
- Surprise anyone by shouting "ENABLING!" while they're working in the robot. Anyone near the robot should know what you're doing, and that you're about to enable it, or they should not be near the robot.
- 

#### High Risk Testing Procedure
This is an additional set of procedures when testing code with high uncertainty, or with high risk in case of error. A few examples where this is needed
- Initial subsystem bringup
- The build teams have had disassembled or modified the system in any way
- Changing sensor configurations, feedback loops, or other motion control code beyond small tuning adjustments.
- Changing code that handles collision constraints
- The bot has bugs/faults that cause loss of control at random

In such cases, the following steps should be used to ensure that the robot behaves as anticipated, and brings the system

- Dedicated a spotter. This person's *sole job* is watching the robot, and being ready to hit the Enter key to disable it. 
- The Operator's job is to announce what specific action is to be performed, and what the expected motion of the robot should be for any given test. 
- If the robot does *anything else*, the Spotter should disable the robot immediately.
- If the robot does anything in a way that might cause damage to itself or others, the Spotter should also disable the robot immediately.
- In case of a disable, the Spotter + Operator may re-negotiate the expected response of the robot robot for testing.
- Once code is known to work repeatedly, and under normal operating conditions, you can consider it operating in a known good state. 
#### Standard Operating Procedure
- A spotter is recommended if available in general.
- A spotter is required when operating near other people for testing, diagnostics, or demonstration.
- When driving, define an area in which the bot is allowed to be. Spotters should disable the bot if leaves the area, or approaches the edge at uncontrolled speeds..
- 

#### Code safety
The way your code is written, and how well it performs can contribute to the safety or danger of the robot . 
- Predictable, controlled motion is safer. 
- Bugs can result in unexpected motion and loss of control, causing direct damage to people or the bot. 
- Unexpected motion can launch game pieces or bot parts.
- 

#### Operator safety
Programmers also act as drivers of the robot, simulating gameplay tasks, but in different conditions. 
Unlike drivers, where the tasks are performed in an open field, programmers are often running it in smaller spaces, with less physical spacing, and may have people assisting within close contact of the bot. 

This brings in several other key concerns such as 
- Knowing the controllers and actions, at least to the degree you can safely operate the robot in the state you're testing in. 
- Unplugging controllers that are not relevant to your testing to avoid accidental presses or motion.
- Good communication with your teammates 
- Paying close attention to speed, and making sure to retain control
- Paying close attention to the robot Enable state.
- Paying close attention to _which_ code version is running.