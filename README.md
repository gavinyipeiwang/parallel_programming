# Parallel Programming

This repo contains solutions of some exercises I did when learning parallel programming.
## Why parallel programming?
I see parallel programming as a programming paradigm. The fundamental idea of it is to break down a task into small tasks which can be executed at the same time. A parallel program could leverage the power of multi-cores on a single machine, and could also run on multiple machines. However, it does not mean every program should be written like this. In fact, only graphic computing and some programs running on the server side (with lots of cores) can benefit from parallelism. For more details, I found [Amdahl's law] (https://en.wikipedia.org/wiki/Amdahl%27s_law) explains very well.
## The difference between multi-threading and parallel programming.
These two concepts are often confused, especially for people coming from Java background like me. The key difference is that multi-threading is not guaranteed running in parallel as the execution of one thread might depend on the result of another thread. A classic example is multiple threads performming transactions between two accounts. In this case, all threads have to run in sequence.
## The signature of parallel function in Scala.
I found the following functions very intuitive in terms of understanding the paradigm.
```scala
  def parallel[A, B](taskA: => A, taskB: => B): (A, B) = ???

  def nonParallel[A, B](taskA: A, taskB: B): (A, B) = ???
```
Task A and Task B do not any dependency in between, therefore instead of executing they sequentially, they could be executed in parallel.
## How do I identify TaskA and TaskB for a given computation?
Keep "Divide and Conquer" in mind. In the following example, Task A and B refer to the pointwise exponent process for a given array from index left to right:
```scala
def normsOfPar(inp: Array[Int], p: Double, left: Int, right: Int, out: Array): Unit = {
  if (right - left < threshold) {
    var i = left
    while (i < right) {
      inp(i) = Math.pow(inp(i), p).toInt
      i += 1
    }
  } else {
    val mid = (left + right) / 2
    parallel(normsOfPar(inp, p, left, mid, out), normsOfPar(inp, p, mid, right, out))
  }
}
```
### Data-Parallelism
In short, if you can't parallelize your tasks, then try to parallelize your data. In Scala, some collection operations are parallelizable, some are not. For example,
```scala
//not parallelizable
def foldLeft[B](z: B)(f: (B, A) => B): B
//parallelizable
def fold(z: A)(f: (A, A) => A): A
```
### Parallel Collection Hierarchy
Traits ParIterable[T], ParSeq[T], ParSet[T] and ParMap[K, V]. Side effecting operations are not parallelizable. Avoid mutations to the same memory locations without proper synchronization. Never write/read to a collection that is concurrently traversed.
### Data-Parallel Abstraction
Iterators

Splitters
```scala
  trait Splitter[A] extends Iterator[A] {
    def split: Seq[Splitter[A]]
    def remaining: Int
  }
```
Builders
```scala
  trait Builder[A, Repr] {
    def +=(elem: A): Builder[A, Repr]
    def result: Repr
  }
```
Combiners
```scala
  trait Combiner[A, Repr] extends Builder[A, Repr] {
    def combine(that: Combiner[A, Repr]): Combiner[A, Repr]
  }
```







