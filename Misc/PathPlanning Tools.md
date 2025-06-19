
## Success Criteria
- [ ] Choose a PathPlanning tool
- [ ] Implement the Java framework for the selected tool
- [ ] Model the robot's physical parameters for your tool
- [ ] Drive a robot along a target trajectory using one of these tools

Followup to:
[[Auto Differential]]
[[Swerve Motion]]

## The tools
https://docs.wpilib.org/en/stable/docs/software/pathplanning/index.html
https://choreo.autos/
https://pathplanner.dev/home.html
## Understanding the concepts

https://docs.wpilib.org/en/stable/docs/software/pathplanning/trajectory-tutorial/trajectory-tutorial-overview.html



## Planning vs other method
Do you need path planning to make great autos? Maybe! But not always.

PathPlanning can give you extremely fast, optimized autos, allowing you to squeeze every fraction of a second from your auto. However, it can be challenging to set up, and has a long list of requirements to get even moderate performance.



# Further Research

#### Pure Pursuit
Unlike "path planning" algorithms that attempt to define and predict robot motion, Pure Pursuit simply acts as a reactive path follower, as the name somewhat implies.

https://www.mathworks.com/help/nav/ug/pure-pursuit-controller.html
![[Pasted image 20250612135241.png]]

This algorithm is fairly simple and conceptually straightforward, but with some notable limitations. However, the concept is very useful for advancing simpler autos