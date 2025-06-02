Set up a mock project  with a nominal, standard code structure


Project should have a subsystem that
- Is in a subsystem folder
- Has 3 components in a (logic, [[Physics Simulation]], [[Mechanism2d]])
- Has a factory method to get a control command (can be mocked up)
- Has a trigger that indicates a mechanism state (can be mocked up based on timers)
Has an additional sensor subsystem system that
- Provides a trigger for a condition (can be mocked up)
Has a controller and 
Has an Autos class to hold autos
- With an auto chooser initialization 
- A single basic auto using subsystem and sensor
