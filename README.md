# TrackTimeWithMonad

![Java CI with Maven](https://github.com/arosenbach/TrackTimeWithMonad/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)

#### Get returned value and elapsed time of a single method

```java
final Timed<List<User>> timedUsers = serviceB.getUsers(Arrays.asList("id1", "id2", "id3"));
final List<User> users = timedUsers.getValue();
final long elapsed = timedUsers.elapsed(TimeUnit.MILLISECONDS);
```


#### Get returned value and elapsed time of a chain of methods
```java
final Timed<List<User>> timedUsers = serviceA.getUserIds()
                                             .flatMap(serviceB::getUsers);
final List<User> users = timedUsers.getValue();
final long elapsed = timedUsers.elapsed(TimeUnit.MILLISECONDS); // total elapsed time
```

#### Get elapsed time of one of the methods called
```java
// ServiceA.java
public Timed<List<String>> getUserIds() {
    final Stopwatch stopwatch = Stopwatch.createStarted("ServiceA::getUserIds");
    final List<String> userIds = ...
    stopwatch.stop();
    return Timed.of(userIds,stopwatch);
}
```

```java
final Timed<List<User>> timedUsers = serviceA.getUserIds()
                                             .flatMap(serviceB::getUsers);
timedUsers.elapsed("ServiceA::getUserIds", TimeUnit.MILLISECONDS)
                   .ifPresent(elapsed -> System.out.println("ServiceA::getUserIds time : " + elapsed + " ms"));
```
