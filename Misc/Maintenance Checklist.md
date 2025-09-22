
During competition season, things are busy. Everything is stressful, the days are long, and students and bots are put through the wringer. Students may not always be present at every critical moment, and it's both unreasonable and unwise to expect this to be the case.

During these times, proper upkeep of the robot is both critical and challenging. So, to ensure effective, efficient upkeep, we create a solid process to validate the bot, and prevent errors.


#### DON'T PANIC
While everyone initially pushes back on rigid methodology here, there's a reason it's done! It's not busywork: it actually _greatly_ simplifies and streamlines the season.

There's a lot of benefits to good procedure
- Peace of mind: Everyone involved knows that whatever *does* goes wrong was unavoidable, as every possible preventative measure had been taken. There's no "blame" or frustration.
- A well documented procedure can be easily handed off to others, meaning no "critical students" get stuck not being able to take breaks when they need it.
- A well practiced procedure can be executed _very_ quickly, reducing time being stuck in the pit.
- A well maintained bot is less likely to break.
- A bot that's not broken doesn't require a lot of effort to fix.
- A bot that's not broken is more likely to do well
- A bot that's doing well makes for an exciting competition experience.

If you aren't convinced though, ask a build team veteran. Good upkeep is all positives. 

#### Maintenance Best Practices

The critical structure of robust maintenance is solid, robust checklists. These come in two major flavors
- Documented procedures: These simply explain how to apply the checklists, expectations for the team, and special considerations that may come up. These are likely to be ordered, step by step walkthroughs.
- Task lists: A specific set of things to mark off. These are typically grouped rather than ordered, allowing multiple people to claim a specific group, and check items in parallel.
However, these blur together very quickly, so don't get too tied up on this. 

Each season will demand it's own list of various detail and size. However, year to year, there's a few evergreen lists to consider:

- **Daily upkeep**: A list of items that should be checked once a day when the bot is in use. This can cover less frequent wear and tear, or more invasive, detailed inspections. 
- **Post-Repair validation**: This usually involves additional validation of the affected systems to ensure correct re-assembly, and making sure nearby systems were not affected.
- **Pre-match validation**: This list should cover any last-minute checks to ensure that the robot gets on the field in the best possible state, and make sure nothing got disrupted since the post-match checked.
- **Post-match validation**: This list should be checking for any unseen damage or major wear. Catching problems after a match ensures the greatest chance to fix potential issues before the next one. This is likely to be the biggest, most exhaustive list. 
- **Damaged Bot process**: The bot _will_ break. When it does, having a clear path to to assess the damage, mitigate further damage, and prepare for the next match is crucial. This is likely to be *the* most stressful part of the day, and will put your preparation to the test. 
- **Operation Confirmation process**: This is a full system + code check, basically just doing all game tasks to the amount you can inside the pit. This ensures that mechanical aspects of the bot work, and that the code is working nominally.

During practice, you generally want to consider a "match" a change of the battery. 

#### Bolt alignment marks

A critical maintenance detail is that the build related teams will mark properly tightened bolts, nuts, and rivits with marks similar to the ones shown here. If the marks are mismatched, you know that it's loose and needs tightening or adjustment. 
This allows for rapid, precise inspection of the robot, and will be referenced several times in the checklist. 

![[bolt alignment marks.png]]


### How to create checklists

Many issues are persistent across all bots; Damaged gears, loose bolts, damaged gears, cracked rollers, etc. A lot of checklist items can be started without any specialized bot-specific knowledge.

However, as the robot undergoes programming and drive practice, you'll notice additional wear, unexpected failure modes, and quirks. Things like unexpected contact, frame bends, or specific bolts being particularly prone to loosening. Make note of these quirks, and add them to your various checklists.

Whenever possible, work to design out these various failure modes. This can be as simple as changing a gear ratio, or as complex as reworking a mechanism to move motors around.

For items that cannot be designed out, consider the severity and frequency. If a failure mode is severe or frequent, it should go on a per-match checklist. If something minor and infrequent, just check it daily.

### Updating checklists

These lists should be living documents, reflecting the robot's current physical state. As additional failures are identified, they should be added.

If a design change is proven to resolve an issue, the check frequency can be reduced or even removed entirely in some cases. However, never remove an item that simply "hasn't occurred recently", unless there's been a fundamental change to the system.




## Example 2024 Robot Team Operating Procedures


> [!NOTE] General Procedures
> - [ ] When flipping robot make sure shooter is held to prevent slamming
> 


> [!NOTE]  Pre-Match Procedure
> - [ ] Complete pre-match checklist
> - [ ] Assign person to walk in front of cart
> - [ ] Deliver robot to drivers
> - [ ] Deliver driver station to drivers
> - [ ] Report any issues or changes to drivers
> - [ ] Return to stands unless doing ongoing repairs in pit


> [!NOTE]  Standard Match Procedure
> - [ ] Watch match for potential issues
> - [ ] If issue is detected, watch for repeatability/consistency and identify likely causes


> [!NOTE] Technician Prematch Checklist
> (the technician had a 5-10 item list on a lanyard, but was not recorded with the others. It mostly was validating completion of the others, and a few critical items worth having double-checked, or relating to on-field setup Robot Team could not do)
> - [ ] Confirm checklist completion
> - [ ] Make sure rollers are clean
> - [ ] ???


> [!NOTE]  Operation Confirmation Procedure
> (typically handled by programmers, but anyone who knows the controller can do this)
> - [ ] Put Robot on blocks
> - [ ] Let climber home.
> - [ ] Intake note
> - [ ] Set shooter RPM + angle. Let drop. 
> - [ ] Load Note into dunkarm.
> - [ ] Dunkarm to speaker.
> - [ ] Move climbers to climb position. 
> - [ ] Dunkarm manual/trap mode up
> - [ ] Score note.
> - [ ] Drive forward OK
> - [ ] Translate left OK
> - [ ] All wheels spin all the way around
> 


> [!NOTE]  Damaged Robot Post-Match Procedure
> - [ ] Standard post-match mostly applies
> - [ ] Make sure code changes are tested early ie: not with 5 minutes before queue
> - [ ] Identify primary cause of failure 
> - [ ] Identify secondary failures and additional damage caused by primary failure
> - [ ] If needed, send person to pits to prepare equipment/spares
> - [ ] If needed, summon assistance/specialists for damaged systems. 
> - [ ] Repair bot
> - [ ] Apply post-repair checklist for affected systems (if applicable)
> - [ ] Apply robot off-field checklist
> - [ ] 2 additional people also apply post-repair + off field checklist for the system(s) for completeness.
> - [ ] Apply operating confirmation procedure
> - [ ] Evaluate checklist/processes: Can something be added to help prevent this failure going forward
> 

## Example 2024 Checklists

> [!NOTE] Robot Team Beginning of the Day Checklist
> 
> This is the time of day robot team should be working on major non critical tweaks to the function of the robot. Examples: brand new autos, new april tag stuff
> 
> 
> Pit Setup
> - [ ] Make sure battery cart is plugged in and batteries are charging
> 
> Frameworks
> - [ ] Check Frame Bolts (Paint Pen Lines), starting with top and then flipping robot to check bottom
> - [ ] Check Wheels/Tread and replace as needed
> - [ ] Check Belly Pan Rivets
> - [ ] Check Frame square-ness
> - [ ] Check swerve modules for crunchyness
> 
> Functions
> - [ ] Check Climber bearings
> - [ ] Check Functions Bolts (Intake first, then flip robot upright and move on to shooter, then climber, and finally dunk arm), also using paint pen lines to verify tightness
> - [ ] Check Shooter Wheels for wear
> - [ ] Check Rollers for bends or cracks
> - [ ] Check Belts and printed roller pulleys
> - [ ] Check Shooter Pivot Tubes and Bolts
> - [ ] Check Baby NEO on dunk arm (pinion engagement/slipping)
> - [ ] Check Shooter Pivot sector gear and pinion
> - [ ] Check Shooter pivot vp engagement
> 
> Controls
> - [ ] Check CAN Wire Connections
> - [ ] Check SparkMAX absolute encoder attachment
> - [ ] Check Zip ties near Climber Hooks
> - [ ] Check Power distribution wires/connector tightness
> - [ ] Check Plugins to RIO
> 
> Programming
> - [ ] Check Wheel Alignment
> - [ ] Check Dunk Arm Soft Limits
> - [ ] Check Note Intaking
> - [ ] Auto Note Align Checks
> 


> [!NOTE] Robot Team Post Match Checklist
> 
> Drivetrain
> - [ ] tread not unevenly worn or damaged
> - [ ] Tread within wear limits
> - [ ] swerve module mounting bolts
> - [ ] swerve module fork bolts
> - [ ] belly pan rivets
> - [ ] Motor temps OK (post-match only)
> 
> 
> Shooter Pivot
> - [ ] Full range of motion OK
> - [ ] Sector Gear bolts securely mounted
> - [ ] Versa gearbox bolts securely mounted
> - [ ] Gear meshing OK
> - [ ] Sector gear not worn/damaged
> - [ ] Drive gear not worn/damaged
> - [ ] 550 motor pinion transmits torque
> - [ ] Motor temps OK (post-match only)
> 
> 
> Flywheels
> - [ ] Shafts not bent
> - [ ] Wheel wear OK
> - [ ] Bearings OK / not crunchy
> - [ ] Bolts OK/ not backed out
> - [ ] Free spin/friction OK
> 
> 
> Dunk Arm
> - [ ] Full range of motion OK
> - [ ] Gears mesh OK and no chipped/broken teeth
> - [ ] Chain tension OK
> - [ ] LEDs secured OK
> 
> 
> Dunk Arm Rollers
> - [ ] Bearings OK
> - [ ] Rollers OK + not cracked
> - [ ] Rollers Cleaned
> - [ ] 550 transmitting torque OK
> - [ ] Bolts OK
> - [ ] Side plates not cracked or damaged
> - [ ] Alignment belt clocked correctly
> 
> 
> Climber
> - [ ] Full range of motion with no binding
> - [ ] Chain tension OK
> - [ ] mounting rivets on the frame
> - [ ] Mounting bolts on polycarb
> - [ ] Polycarb hooks not damaged
> 
> 
> Intake
> - [ ] rollers not bent or cracked
> - [ ] roller bolts not backed out
> - [ ] Intake frame aligned correctly/square
> - [ ] Intake towers bolted down completely
> 
> 
> Passthrough
> - [ ] rollers not bent or cracked
> - [ ] roller bolts not backed out
> - [ ] 550 pinion OK and transmits torque
> 
> 
> Controls
> - [ ] Battery leads OK
> - [ ] Spark max encoder cables checked
> - [ ] Dunk arm + dunkarm roller
> - [ ]  Intake
> - [ ] drivetrain
> - [ ] Shooter + flywheels
> - [ ] Wires near climber secured
> - [ ] Wires on back plate secured
> - [ ] Wires below shooter secured.
> - [ ] Network wires OK
> - [ ] LEDS OK
> 



> [!NOTE]  Pre-match Checklist
> - [ ] post-match checklist was completed since robot was last driven
> - [ ] Correct battery selected
> - [ ] Battery leads OK
> - [ ] Battery secured correctly, leads clear of passthrough
> - [ ] Correct bumpers secured
> - [ ] Rollers cleaned
> - [ ] Dunkarm
> - [ ] Intake
> - [ ] Passthrough
> - [ ] Do Operation Confirmation Procedure
> - [ ] Report unfixable problems to drive team ASAP
> - [ ] Square placed on robot cart
> - [ ] Drive station ready for handoff (controllers, charger, laptop)
> 




> [!NOTE] Post Repair Checklist 
> 
> Swerve Modules
>   - [ ] Rezero module offset
>   - [ ] Make sure all bolts are tight
>   - [ ] Make sure can ids are correct
>   - [ ] Make sure right gear ratio in module
> 
> 
> Intake
>   - [ ] Run intake to test performance
>   - [ ] Make sure can ids are correct
> 
> 
> Dunk Arm
>   - [ ] Verify end effector clocking
>   - [ ] Verify press on pinion goodness
>   - [ ] Run dunk arm to test performance
>   - [ ] Make sure can ids are correct
> 
> 
> Shooter
>   - [ ] Run shooter to test performance
>   - [ ] Check axial shaft slop
>   - [ ] Make sure can ids are correct
> 
> 
> Passthrough
>   - [ ] Run passthrough to test performance
>   - [ ] Make sure can ids are correct
> 
> 
> Climber
>   - [ ] Run passthrough to test performance
>   - [ ] Make sure can ids are correct
>   - [ ] Make sure no binding on internal bearing block
>   - [ ] Make sure chain and belt are appropriately tightened


