#stub 
### The very basics
- variables
- classes
- Functions
- "structs" or data classes
- Introduce enums (don't need to impliment)
- Explaining inherited classes as interfaces (because we deal with them).
- "null" objects -> initialization (mostly avoided by minimizing indefinite declarations)


### Explicit avoids in the lesson plan (deal with as needed)
- Complex stuff involving inheritence or extending classes. Aside from creating new commands or triggers (which mostly act as an interface), we don't do anything with this, and the few places we could it creates confusion because of hidden, unexpected behaviors.
- Implimenting/using Arrays/Lists: Not especially common in simpler tasks, and java makes interacting messy. Handle as needed later.
- Error handling. Super gross in java, and we mostly need basics. Cover if necessary (mostly Lasercan), but prefer converting errors to Optionals and doing type checking instead of bubbling up (which lasercan also does).


###  Skip ahead to robot-coding
- Teach the "command/robotcontainer/subsystem" model early. If you do a ton of bot development before getting to "typical bot structure", code structure becomes a mess and it becomes harder to use any advanced features and reason through more complex tasks. It also gets the wrong habits engrained, which becomes an unlearning process later. It's OK to say "We'll get into the details of why this is" later, they'll figure things out.
- Putting complex logic into a command is a great way to prototype all sorts of bot ideas, and should help them when doing "upgrades" to older code.
- Consider one "trial project" for subsystems to house a single, simple system, and learn the basic java education. No need to get too deep beyond helping set up. Then, once they have it after a project or two, move into modelling "complex work" with commands.
- It's helpful when "prototyping" complex things using "commands" because you can cycle through different ways of prototyping it easily by swapping out commands being selected.

    - Subsystems
        - Robot nouns.
        - Containers for a single actuator or "resource"
        - Allows "locks" and requires
        - Organized place to keep associated code.
    - Commands
        - The robot "verbs", and actions systems can take.
        - Can be basic and stil be useful when decorated
        - Typical commands shouldn't end; Decorate or make shortcuts for ones that do
