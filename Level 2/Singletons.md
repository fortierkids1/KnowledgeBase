## Success Criteria
- [ ] Create a Singleton class
- [ ] Use it in multiple places in your code

## Summary

Singletons are a coding structure (or "pattern") that represents a unique entity. It's designed to allow one, and _only_ one instance of a class.

This tends to be useful for controlling access to unique items like physical hardware, IO channels, and other such items. 

The techniques used in this pattern are also helpful for cases where you might be fine with multiple instances, _but_ you need to restrict the total number, or keep track in some way.

## Bare Minimum Singleton pattern

```java
public ExampleSingleton{
    private static ExampleSingleton instance;

	//note private constructor
    private ExampleSingleton(){}
    
    public static ExampleSingleton getInstance(){
        //Check to see if we have an instance; If not, create it. 
        if(instance==null) instance = new ExampleSingleton();
        //If so, return it. 
        return instance;
    }
    
	// Methods just work normally.
	public double exampleMethod(){
        return 0; 
    }
}
```

There's a few key details here: 
- `private ExampleSingleton(){}` The constructor is private, meaning you _cannot_ create objects using `new ExampleSingleton()`. If you _could_ do that, then you would create a second instance of the class! So, this is private, meaning only an instance of the class itself can create an instance.
- `public static ExampleSingleton getInstance()` ; This does the heavy lifting: It sees _if_ we have an instance, and if not, it actually creates one. If we _have_ an instance, it just returns a reference to it. This is how we ensure we only ever create _one_ instance of the class. This is static, which allows us to call it on the base class (since we won't have an instance until we do).
- `private static ExampleSingleton instance;` This is the reference for the created instance. Notice that it's `static`, meaning that the instance is "owned" by the base class itself. 


## Example Sensor Singleton
```java
public ExampleSensorSystem{
    private static ExampleSensorSystem instance;
    
    //Example object representing a physical object, belonging to
    //an instance of this class.
    //If we create more than one, our code will crash!
    //Fortunately, singletons prevent this. 
    private Ultrasonic sensor = new Ultrasonic(0,1);

    private ExampleSensorSystem(){} //note private constructor
    
    public static ExampleSensorSystem getInstance(){
        //Check to see if we have an instance; If not, create it. 
        if(instance==null) instance = new ExampleSensorSystem();
        //If so, return it. 
        return instance;
    }
    
    public double getDistance(){
        return sensor.getRangeInches();
    }
}
```

Elsewhere, these are all valid ways to interface with this sensor, and get the data we need

```java
ExampleSensorSystem.getInstance().getDistance();


var sensor = ExampleSensorSystem.getInstance();
// do other things
sensor.getDistance();
```


## When To Use Singletons

Rarely is often the right answer. While Singletons are useful in streamlining code in some circumstances, they also can obscure _where_ you use it, and how you're using it. Here's the general considerations

- You have something that is necessarily "unique"
- It will be accessed by several other classes, or have complicated scope.
- it is immutable: Once created, it won't be changed, altered or re-configured.
- You will not have any code re-use

In cases where it's less obvious, the "dependency injection" pattern makes more sense. You'll see the Dependency pattern used in a lot of FRC code for subsystems. Even though these *are* unique, they're highly mutable, and we want to track access due to the Requires and lockouts. 

Similarly, for sensors we probably one multiple of the _same type_. This means if we use a Singleton, we would have to re-write the code several times! (or get creative with class abstractions). 

> [!NOTE] Dependency Injection
> This pattern consists of passing a reference to items in a direct, explicit way, like so. 
> 
> ```java
> //We create an item
> ExampleSubsystem example = new ExampleSubsystem();
> 
> ExampleCommand example = new ExampleCommand(exampleSubsystem);
> ```
> 
> ```java
> class ExampleCommand(){
> 	ExampleSubsystem example;
> 	ExampleCommand(ExampleSubsystem example){
> 			this.example = example;
> 	}
>	public void exampleMethod(){
>		//has access to example subsystem
>	}	
> }
> ```

