# Loguno

The library for logging abstraction. It decouples logging aspect from your main codeflow.
Technically, it generates logging commands and injects it to java code during compiling. 
In this way, your code became free from logging code.

## Getting Started

Just add meven dependency:
```xml
<dependency>
    <groupId>org.loguno</groupId>
    <artifactId>loguno-processor</artifactId>
    <version>0.0.1</version>
    <scope>compile</scope>
</dependency>
```

### Why someone needs it?

*You don't see the gopher but it is there!*

When we write a code we want to concentrate on main task. Secondary aspects shouldn't distract us.
But reality is different. For example logging. We write logging commands, everywhere and they intertwine your program.
"I'm entering in the method...", "The input parameters are...", "The local variable is...", "The exception is caught.." and so on and so on.
Forget it. See the few samples:

```java
You write:
@Loguno
public void launchRocket(String toPlanet, int crew, Date timeOfArrival){
}
After compiling:
public void launchRocket(String toPlanet, int crew, Date timeOfArrival) {
    LOG.info("org.loguno.test.JustMonkey.launchRocket Method is called. Parameter {}={},Parameter {}={},Parameter {}={}", "toPlanet", toPlanet, "crew", crew, "timeOfArrival", timeOfArrival);
}
```
Whant to log only 1 parameter? Easy!
```java
You write:
public void launchRocket(@Loguno String toPlanet, int crew, Date timeOfArrival){
}
After compiling:
public void launchRocket(String toPlanet, int crew, Date timeOfArrival) {
    LOG.info("org.loguno.test.JustMonkey.launchRocket Method parameter {}={}", "toPlanet", toPlanet);
}
```
More complex example with local variable:
```java
You write:
@Loguno
public void hiApe(int i){
    @Loguno
    int u =2+i;
}
After compiling:
public void hiApe(int i) {
    LOG.info("org.loguno.test.JustMonkey.hiApe Method is called. Parameter {}={}", "i", i);
    int u = 2 + i;
    LOG.info("org.loguno.test.JustMonkey.hiApe Local variable {}={}", "u", u);
}
```
Exceptions handling:
```java
You write:
public void eat(String animal) throws @Loguno.WARN("Bananas are running out!") NoBananasException {
    final String b = "b";
    eat.doSmth();
}
After compiling:
public void eat(String animal) throws NoBananasException {
    try {
        String b = "b";
        this.eat.doSmth();
    } catch (NoBananasException e) {
        LOG.warn("Bananas are running out!", e);
        throw e;
    }
}
```
And the last case:
```java
You write:
public void monkeyJump() {
    try {
        jump();
    } catch (@Loguno.INFO("monkey gets hurt") FallDownException e) {
    }
}
After compiling:
public void monkeyJump() {
    try {
        this.jump();
    } catch (FallDownException e) {
        LOG.info("monkey gets hurt", e);
    }
}

```




