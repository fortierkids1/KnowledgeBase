#### Part of: 
[[SuperStructure Arm]]
[[SuperStructure Elevator]]
And will generally be done after most requirements for those systems
## Success Criteria
- [ ] Home a subsystem using a Command-oriented method
- [ ] Home a subsystem using a state-based method
- [ ] Make a non-homed system refuse non-homing command operations
- [ ] Document the "expected startup configuration" of your robot, and how the homing sequence resolves potential issues.
#### Lesson Plan
- Configure encoders and other system configurations
- Construct a Command that homes the system
- Create a Trigger to represent if the system is homed or not
- Determine the best way to integrate the homing operation. This can be 
	  - Initial one-off sequence on enable
	  - As a blocking operation when attempting to command the system
	  - As a default command with a Conditional Command
	  - Idle re-homing (eg, correcting for slipped belts when system is not in use)
  - 