[![Build Status](https://travis-ci.com/dimpon/loguno.svg?branch=master)](https://travis-ci.com/dimpon/loguno)
[![Coverage Status](http://img.shields.io/coveralls/dimpon/loguno/master.svg?style=flat-square)](https://coveralls.io/r/dimpon/loguno?branch=master)
[![Apache V2 License](http://img.shields.io/badge/license-Apache%20V2-blue.svg)](https://github.com/dimpon/loguno/blob/master/LICENSE)
# Loguno 
It is a library for logging abstraction. It unties the logging aspect from you main business code.
Technically, it generates logging commands and injects it to your java code during compiling. 
In this way, your code became free from logging code. 
   
#### Why someone needs it?

*You don't see the gopher but it is there!*

When we write a code we want to concentrate on business logic. Secondary aspects shouldn't distract us.
But reality is different. For example LOGGING. We write logging commands, everywhere and they intertwine your program.
"I'm entering in the method...", "The input parameters are...", "The local variable is...", "The exception is caught.." and so on and so on.
Forget it. See few samples:

```java
You write:
@Loguno
public void launchRocket(String toPlanet, int crew, Date timeOfArrival){
}
    
After compiling:
public void launchRocket(String toPlanet, int crew, Date timeOfArrival) {
    LOG.info("org.loguno.test.JustMonkey.launchRocket Method is called. Parameter {}={},Parameter {}={},Parameter {}={}", new Object[]{"toPlanet", toPlanet, "crew", crew, "timeOfArrival", timeOfArrival});
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
More complex example:
```java
You write:
@Loguno
public JustMonkey(@Loguno String a, String b) {
    super(a);
}

@Loguno
public void hiApe(int i){
    @Loguno
    int u =2+i;
}

After compiling:
public JustMonkey(String a, String b) {
    super(a);
    LOG.info("org.loguno.test.JustMonkey.<init> Method parameter {}={}", "a", a);
    LOG.info("org.loguno.test.JustMonkey.<init> Method is called. Parameter {}={},Parameter {}={}", new Object[]{"a", a, "b", b});
}

public void hiApe(int i) {
    LOG.info("org.loguno.test.JustMonkey.hiApe Method is called. Parameter {}={}", "i", i);
    int u = 2 + i;
    LOG.info("org.loguno.test.JustMonkey.hiApe Local variable {}={}", "u", u);
}
```

 
