# Functional Scala 2023 Talk: Mind the Gap

![image](https://github.com/dkarlinsky/mind-the-gap-talk/assets/1577551/c35a4692-819d-44b9-b93f-119524d22352)

This repo contains example code shown in the talk with some extras:

 * [ZioInterop](src/main/scala/mindthegap/ZioInterop.scala) - demonstrates ZIO calling non-ZIO code
 * [RunZio](src/main/scala/mindthegap/RunZio.scala) - demostrate executing ZIO effects from non-ZIO code
 * [ForkDaemonExample](src/main/scala/mindthegap/ForkDaemonExample.scala) - demonstrates starting background fibers from non-ZIO code
 * [StackExample](src/main/scala/mindthegap/StackExample.scala) - demostrates fiber traces attatched to errors and how to improve them
 * [MdcExample](src/main/scala/mindthegap/MdcExample.scala) - demonstrates using `ThreadLocalBridge` to propagate fiber state (based on `FiberRef`) to thread local state, used by legacy libraries 
