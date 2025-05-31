Requires:
[[Basics]]
### Completion Requirements
- Create a command that runs indefinitely
- Have that command start+end on a joystick button
- Create a command that starts on a joystick press, and ends on a different press
- Create a command that runs when the robot is enabled, and ends on a condition 
- Create a default command that lets you know when it's running
- Create a runCommand using a function pointer
- Create a runCommand using a lambda

### Notes
In order to simplify requirements, this module can just use print statements to track things that are happening if they don't have motors yet.
If they have motors, the above can just Spin a motor, or do a simulation