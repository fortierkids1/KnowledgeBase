Requires:[[Robot Code Basics]]
Recommends:[[Commands]]


## Success Criteria
- [ ] Spin a motor
- [ ] Configure a motor with max current
- [ ] Control on/off via joystick button
- [ ] Control speed via joystick

## Setup and prep

> [!Caution] Learning order
> This curriculum does not require or assume the [[Commands|Command]] structure; It's just about spinning motors. 
> However, it's recommended to learn Motor Control alongside or after [[Commands]], as we'll use them for everything afterwards anyway.

> [!NOTE] Rev Lib
> This documentation assumes you have the third party Rev Library installed. You can find instructions here. 
> https://docs.wpilib.org/en/latest/docs/software/vscode-overview/3rd-party-libraries.html

> [!NOTE] Wiring and Electrical
> This document also assumes correct wiring and powering of a motor controller. This should be the case if you're using a testbench. 





## Reference Implementation

```java
// Robot.java
public Robot extends TimedRobot{

	public void teleopPeriodic(){
		
	}
}
```

