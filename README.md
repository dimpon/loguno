# Loguno
[![Maven Central](https://img.shields.io/maven-central/v/org.loguno/loguno-processor.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.loguno%22%20AND%20a:%22loguno-processor%22)
[![Build Status](https://travis-ci.com/dimpon/loguno.svg?branch=master)](https://travis-ci.com/dimpon/loguno)
[![Coverage Status](http://img.shields.io/coveralls/dimpon/loguno/master.svg?style=flat-square)](https://coveralls.io/r/dimpon/loguno?branch=master)
[![Apache V2 License](http://img.shields.io/badge/license-Apache%20V2-green.svg)](https://github.com/dimpon/loguno/blob/master/LICENSE)

The library for logging abstraction. It decouples logging aspect from your main business code.
Technically, it generates logging commands and injects them to java code during compiling. 
In this way, your initial source code become free from logging code.

## Getting Started

Just add maven dependency:
```xml
<dependency>
    <groupId>org.loguno</groupId>
    <artifactId>loguno-processor</artifactId>
    <version>0.0.3</version>
    <scope>provided</scope>
</dependency>
```
And may be Slf4j dependency, cause now Loguno supports only Slf4j:
```xml
 <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.25</version>
 </dependency>

 <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.25</version>
 </dependency>
```
Then, put annotation @Loguno.Logger on class level and put annotation @Loguno on some method...  But better see examples below! 

### Why someone needs it?

When we write a code we want to concentrate on main task. Secondary aspects shouldn't distract us.
But reality is different. For example logging. We write logging commands, everywhere and they intertwine our code.
"I'm entering in the method...", "The input parameters are...", "The local variable is...", "The exception is caught.." and so on and so on.
Forget it. See the few samples:

```java
You write:
@Loguno("We're going to Mars. [Parameter:{}={}]")
public void launchRocket(String planet, int crew, Date timeOfArrival){
}

Loguno gerenates:
public void launchRocket(String planet, int crew, Date timeOfArrival) {
    LOG.info("We're going to Mars. Parameter:{}={},Parameter:{}={},Parameter:{}={}", "planet", planet, "crew", crew, "timeOfArrival", timeOfArrival);
}
```
Want to log only 1 parameter? Easy!
```java
You write:
public void launchRocket(@Loguno String planet, int crew, Date timeOfArrival){
}

Loguno gerenates:
public void launchRocket(String planet, int crew, Date timeOfArrival) {
    LOG.info("org.loguno.test.SpaceMonkey.launchRocket Method parameter {}={}", "planet", planet);
}
```
More complex example with local variable:
```java
You write:
@Loguno
public void hiAlien(int i){
    @Loguno
    int u =2+i;
}

Loguno gerenates:
public void hiAlien(int i) {
    LOG.info("org.loguno.test.SpaceMonkey.hiAlien Method is called. Parameter {}={}", "i", i);
    int u = 2 + i;
    LOG.info("org.loguno.test.SpaceMonkey.hiAlien Local variable {}={}", "u", u);
}
```
Annotate local variables everywhere:
```java
List<String> aa = new ArrayList();
for (@Loguno String o : aa) {
:
}
:
:
@Loguno
Function<String, String> fun = (a) -> {
    @Loguno
    Object counter = a;
    return a;
};
:
:
try (@Loguno Stream<String> lines = dir.stream()) {
:
}
```
...and Loguno injects logging commands!

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
        :
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

Interested? Welcome to our [WIKI](https://github.com/dimpon/loguno/wiki)!




