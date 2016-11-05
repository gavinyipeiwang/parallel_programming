# Parallel Programming

This repo contains solutions of some exercises I did when learning parallel programming.
## Why parallel programming?
I see parallel programming as a programming paradigm. The fundamental idea of it is to break down a task into small tasks which can be executed at the same time. A parallel program could leverage the power of multi-cores on a single machine, and could also run on multiple machines. However, it does not mean every program should be written like this. In fact, only graphic computing and some programs running on the server side (with lots of cores) can benefit from parallelism. For more details, I found [Amdahl's law] (https://en.wikipedia.org/wiki/Amdahl%27s_law) explains very well.
## The difference between multi-threading and parallel programming.
These two concepts are often confused, especially for people coming from Java background like me. The key difference is that multi-threading is not guaranteed running in parallel as the execution of one thread might rely on the result of another thread. A classic example is multiple threads performming transactions between two accounts. In this case, all threads have to run in sequence.
## The signature of parallel function in Scala.
I found the following functions very intuitive in terms of understanding the paradigm.
```scala
  def parallel[A, B](taskA: => A, taskB: => B): (A, B) = ???

  def nonParallel[A, B](taskA: A, taskB: B): (A, B) = ???
```  


