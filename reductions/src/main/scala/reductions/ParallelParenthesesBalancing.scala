package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer (new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
    */
  def balance(chars: Array[Char]): Boolean = {
    chars.foldLeft(0)((acc, c) => {
      if (c == '(' && acc >= 0) acc + 1
      else if (c == ')' && acc >= 0) acc - 1
      else acc
    }) == 0
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
    * (if (zero? x) max (/ 1 x))
    */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    def traverse(idx: Int, until: Int, arg1: Int, arg2: Int): (Int, Int) = {
      var p = (arg1, arg2)
      for (i <- idx until until) {
        val c = chars(i)
        if (chars(i) == '(') {
          p = (p._1 + 1, p._2)
        } else if (c == ')') {
          if (p._1 > 0) {
            p = (p._1 - 1, p._2)
          } else {
            p = (p._1, p._2 + 1)
          }
        }
      }
      p
    }

    def reduce(from: Int, until: Int): (Int, Int) = {
      if (until - from <= threshold) traverse(from, until, 0, 0)
      else {
        val mid = (from + until) / 2
        val (left, right) = parallel(reduce(from, mid), reduce(mid, until))
        //(l1,r1) (l2, r2)
        val r = if (left._1 < right._2) left._1 else right._2
        (left._1 + right._1 - r, left._2 + right._2 - r)
      }
    }

    reduce(0, chars.length) ==(0, 0)
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
