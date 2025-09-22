---
aliases:
  - Threads
  - Futures
---

A Future is a simplified, and much more user friendly application of threading

## Success Criteria
- [ ] ???

## Primer on Threads

A "thread" normally refers to a single chain of code being executed. Most code is "single threaded", meaning everything happens in order; For something to be done, it has to wait its turn.

With proper code setup, you can make it appear that code is doing multiple things at once. There's a few terms for this, but usually "concurrency" or "time sharing" come up here. However, you're still fundamentally waiting for other code to finish, and a _slow_ part of code holds up everything. This might be a complex computation, or a slow IO transfer across a network or data bus. 
Tasks like these don't take computational time, but _do_ take real world time in which we could be doing other things. 

Threads, on the other hand, can utilize additional processor cores to run code completely isolated and independently. Which is where the trouble starts.

### Thread Safety
Threads come with a bit of inherent risk: Because things are happening asynchronously (as in, not in sync with each other), you can develop issues if things are not done when  you expect them to be 

```java
//Set up two variables
var x;
var y;
//These two tasks are slow, so make a thrad for it!
Thread.spawn(()-> x=/*long computation for X*/)
Thread.spawn(()-> y=/*long computation for y*/)
//Sum things up!
var z = x+y
```
This will completely crash; It's unlikely that both threads A and B will have finished by the time the main thread tries to use their values. This example is obvious, but in practice, this can be very sneaky and difficult to pin down. 

Keep in mind anything running a co-processor is inherently a separate thread! These are usually wrapped by helpful containers to streamline interactions, but there's times where this distinction is important.

### Actual example of this data race 
In 2024, we had code managing Limelight data, which would 
- Check `tv`, the target valid data: This value means everything else is valid
- Get `tx` and `ty`, along with `getBotPose`
- Try to computer our pose
- .... and data is wrong?

What happened was simply that in some cases, after checking `tv` to assert valid data, the data changed, causing our calculations to break. The remote system (effectively a different thread) changed the data underneath us.

In some cases, we'd get values that should be valid, but instead they resulted in crashes. 

### Dealing with those

There's lots of strategies to manage threads, most with notable downsides. 

- Avoiding threads: The easiest strategy, but you don't improve performance
- Mutexes: Short for "mutually exclusive", and represents a lock. When using data shared with threads, you lock it, and unlock it when you're done. Notably, this means you spend a good amount of effort and time trying to deal with these locks, and make sure they're where they should be. 
- Splits and joins: If a thread ends, you don't have problems! So, you can just check a thread state and see if it's done with your value. Don't forget to restart it if needed.
- Message passing: Simply don't share data. Instead, just throw it in a queue, and let stuff handle it when it needs to. 

There's other strategies as well, but this brings us to...

## Futures

A Future combines several of those into one, very user friendly package. Conceptually, it represents a "future value" that has not yet been calculated, while actually containing the code to get that value.

Because it's oriented with this expectation, they're easy to think about and use. They're almost as straightforward as any other variable.

```java
//create a future and pass it some work.
CompletableFuture<double> future = new CompletableFuture.supplyAsync( ()-> {Timer.delay(5); /*some long running calculaton*/ return 4;} );
System.out.println("waiting....");
System.out.println( future.get() )
```

That's it. For the simplicity involved, it doesn't *feel* like you're using threads.... but you are. Notice that waiting prints out instantly; about 5 seconds before the number, in fact.

Futures handle most of the "busywork" for you; Managing thread operation, checking to see if it's done, and what the return value is. The thread runs in the background, but if it's not done by the time you get to `future.get()`, it'll automatically stop the main thread, wait until the future thread *is* done, get the value, and then resume the main thread. This will demonstrate it clearly. However, if the future is done, you just race on ahead. 


```java
//create a future and pass it some work.
CompletableFuture<double> future = new CompletableFuture.supplyAsync( ()-> {Timer.delay(5); /*some long running calculaton*/ return 4;} );
System.out.println("waiting....");
Timer.delay(6); // do some busywork on the main thread too
System.out.println("Done with main thread!");
System.out.println( future.get() ); //will print instantly; The thread finished during main thread's work!
```

### Actually using them in FRC

Threads would be really nice in a few places, but in particular, building autos. Autos take a *very* long time to build, and you have a lot of them. And you don't want them wasting time if you're not actually running an auto. 

But remember that Futures represent a "future value", and "contain the code to build it". A Command is a future value, and has a process to build it.... so it's a perfect fit. But you also have to select one of several autos. This is easily done: 

```java
CompletableFuture<Command> selectedAutoFuture = CompletableFuture.supplyAsync(this::doNothing);
SendableChooser<Supplier<Command>> autoChooser = new SendableChooser<>();
```

A full example is in [[/Programmer Guidance/auto-selection]], but the gist is that 
- A Future takes a `Supplier<Command>`: A function that returns a command
- The AutoChooser then has a list of functions that build and return an auto command. 
- When you change the chooser, you start a new future, and start building it. 
- If and when the auto process should start.... the code just waits for the process to finish as needed, and runs it. 

Conveniently, you don't *need* to return values. You can, if needed, run the void version, using a Runnable or non-returning lambda.

```java
CompletableFuture<?> voidedFuture = CompletableFuture.supplyAsync(()->{}); 
if(voidedFuture.isDone()) /* do a thing */ ;
```
While not exactly the intended use case, this allows you to easily run and monitor background code without worry. 

### Gotchyas

Be aware, that as with all threads you generally should not 
- Write to data accessible by other threads; You don't know when something is trying to read that value. Do writes in the main thread. 
- Read data being written to by other threads; This should be easy to reason about. Constants and fixed values are fine, but don't trust state variables.

Additionally, Futures are most effective when your code starts a computation, and then reacts to the completion of that computation afterward. They're intended for run-once use cases. 

For long-running background threads, you'd want to use something else better suited to it. 

### Pseudo Threads
Psuedo-threads are "thread-like" code structures that look and feel like threads, but aren't really. 

WPILib offers a convenient way to run psuedo-threads through the use of `addPeriodic()`. This registers a Runnable at a designated loop interval, but it's still within the thread safety of normal robot code. 

For many cases, this can certain time-sensitive features, while mitigating the hazards of real threads. 

https://docs.wpilib.org/en/stable/docs/software/convenience-features/scheduling-functions.html


## Real Threads 

Native Java Threads are a suitable way to continuously run background tasks that need to truly operate independent of the main thread. However, any time they interface with normal threads, you expose the hazard of data races or data corruption; Effectively, data changes underneath you, causing weird numerical glitches, or outright crashes.

In these cases, you need to meticulously manage access to the threaded data. Java has numerous built in helpers, but there's no shortcut for responsible coding. 

### Mutexes and Synchronized

The easiest way is use of the `synchronized` keyword in java; This is a function decorator (like `public` or `static`), which declares that a function 

```java
private double number=0;

public synchronized double increment(){
    number+=1;
}
public synchronized double double_increment(){
    number+=2;
}
// do some threads and run our code
public periodicThreadA(){ increment(); }
public periodicThreadB(){ double_increment(); }
```

This is it; If both A and B try to run `increment` simultaneously, it's thread will block until `increment` is accessable. Because of how we structure FRC code, this is often a perfectly suitable strategy; Any function trying to run a `synchronized` call has to wait until the _other_ synchronized functions are done. 

However, this comes with potential performance issues: The lock is actually protecting the base object (`this`, or the whole class object), rather than the more narrow value of `number`. So all `synchronized` objects share one mutex; Meaning if you have multiple, independently updating values, they're blocking each other needlessly. 


We can get finer-grain control by use of structures like this: 

```java
private double number=0;
private Object numberLock = new Object(); 

public double increment(){
    synchronized (numberLock){
        number+=1;
    }
}

public double double_increment(){
    synchronized (numberLock){
        number+=2;
    }
}

// do some threads and run our code
public periodicThreadA(){ increment(); }
public periodicThreadB(){ double_increment(); }
```

This structure is identical, but now we've explicitly stated the mutex; We can see it's locking on the function `increment`, rather than the data we care about, which is `number`.

Note that in both cases, _any_ access to `number` needs to go through a `synchronized` item.

Helpfully, you can clean this up for many common cases, as shown in the following example: Any Object class (any class or data structure; effectively everything but Int, Float, and boolean), can be locked directly; Avoiding a separate mutex. However, we may want to develop a notation to demarcate thread-accessed objects like this.

```java
private Pose2D currentPose = new Pose2D(); 

public double do_pose_things(){
    synchronized (currentPose){ //item can hold it's own thread mutex
        currentPose = new Pose2d();
    }
}
```



### Queing and message passing

Message passing is another threading technique that allows threads to interact safely. You simply take your data, and toss it to another thread, where it can pick it up as it needs to.

`SynchronousQueue` is a useful and simple case; This is a queue optimized to interface handoffs between threads. Instead of suppliers adding values indirectly, this queue allows functions to directly block until the other thread arrives with the data it wants. This is useful when one side is significantly faster than the other, making the time spent waiting non-critical. There's methods for both fast suppliers with slow consumers, and fast consumers with slow suppliers.

```java
SynchronousQueue<integer> queue = new SynchronousQueue<integer>;

public void fastSupplier(){ //ran at high speeds
    int value = 0; /*some value, such as quickly running sensor read*/
    queue.offer(value); //will not block; Will simply see there's no one listening, and give up
}
public void slowConsumer(){ //ran at low speeds
    int value = queue.take(); //will block this thread, waiting until fastSupplier tries to make another offer.
    //do something with the value
}
```

In most cases though, you want to keep track of all reported data, but the rate at which it's supplied doesn't always match the rate at which it's consumed. A good example is vision data for odometry. It might be coming in at 120FPS, or 0FPS. Even if it's coming in at the robot's 50hz, it's probably not exactly timed with the function.

Depending on the requirements, a `ArrayBlockingQueue` (First in First Out) or `LinkedBlockingDeque` (Last in First Out). These both have different uses, depending on the desired order.

```java
ArrayBlockingQueue<Pose2d> queue = new ArrayBlockingQueue<Pose2d>();

public void VisionSupplier(){
    Optional<Pose2d> value = vision.getPoseFromAprilTags();
    if(value.isPresent(){
        if(queue.remainingCapacity() < 1) queue.poll() // delete the oldest item if we don't have space
        queue.offer(value); //add the newest value.
    }
}

public void VisionConsumer(){ //ran at low speeds
    var value = queue.take(); //grab the oldest value from the queue or block to wait for it
    odometry.update(value);
}
```
Message passing helps you manage big bursts of data, have threads block/wait for new data, but do introduce one problem: You have to make sure your code behaves well when your queue is full or empty. 

In this case, it's sensible to just throw away the oldest value in our queue; We'll replace it with a more up-to-date one anyway. 
We also block when trying to retrieve new data. This is fine for a dedicated thread, but when ran on our main thread this would cause our bot to halt if we drive away from a vision target. In that case, we'd want to check to see _if_ there's a value first, or use `poll()` which returns `null` instead of waiting. The java docs can help you find the desired behavior for various operations.

Also be wary about the default sizes: By default, both queues can be infinitely large, meaning if your supplier is faster, you'll quickly run out of memory. Setting a maximum (reasonable) size is the best course of action. 
