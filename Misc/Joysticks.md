

https://docs.wpilib.org/en/stable/docs/software/basic-programming/joystick.html 


## Team Conventions

Through experience, we've found that we often have multiple joysticks, a lot of different projects, and a lot of computers. 

Not surprisingly, if you wind up trying to read the wrong inputs from the wrong joysticks, you can end up with a lot of surprises, sometimes dangerously so. 

As a result, we found the best resolution is to simply define 
- Joystick 0 is always the "main" joystick, typically for the driver. 
- Joystick 1 is always the "operator" joystick for auxiliary controls. 

In code, we'd represent this as 
```java
Joystick driverInput = new Joystick(0);
Joystick operatorInput = new Joystick(1);
```

Then, in the [[Driver Station]] , make sure that it picks up the corresponding controllers in the same order: 0 for main/driver, and 1 for operator. 

## Custom controllers